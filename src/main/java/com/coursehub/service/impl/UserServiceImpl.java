package com.coursehub.service.impl;

import com.coursehub.components.DiscountScheduler;
import com.coursehub.converter.UserConverter;
import com.coursehub.dto.request.user.ChangePasswordRequestDTO;
import com.coursehub.dto.request.user.ProfileRequestDTO;
import com.coursehub.dto.response.user.UserDetailDTO;
import com.coursehub.dto.response.user.UserResponseDTO;
import com.coursehub.entity.DiscountEntity;
import com.coursehub.entity.UserDiscountEntity;
import com.coursehub.entity.UserEntity;
import com.coursehub.enums.UserStatus;
import com.coursehub.exceptions.auth.DataNotFoundException;
import com.coursehub.exceptions.user.*;
import com.coursehub.repository.DiscountRepository;
import com.coursehub.repository.UserDiscountRepository;
import com.coursehub.repository.UserRepository;
import com.coursehub.service.S3Service;
import com.coursehub.service.UserService;
import com.coursehub.utils.FileValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserConverter userConverter;
    private final S3Service s3Service;
    private final PasswordEncoder passwordEncoder;
    private final DiscountRepository discountRepository;
    private final UserDiscountRepository userDiscountRepository;
    private final DiscountScheduler discountScheduler;

    @Override
    public UserResponseDTO getMyInfo() {
        SecurityContext context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();
        UserEntity userEntity = userRepository.findByEmailAndIsActive(email, UserStatus.ACTIVE);
        if(userEntity == null){
            throw new DataNotFoundException("Data not found");
        }
        return userConverter.toUserResponseDTO(userEntity);
    }

    @Override
    @Transactional
    public ProfileRequestDTO updateProfile(ProfileRequestDTO request) {
        UserEntity user = getCurrentUser();
        Date birthDate = validateAndParseDateOfBirth(request.getDateOfBirth());
        updateUserBasicFields(user, request, birthDate);
        handleAvatarUpload(user, request.getAvatar());

        UserEntity savedUser = userRepository.save(user);
        return userConverter.toProfileRequestDTO(savedUser);
    }

    @Override
    public UserEntity getCurrentUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();
        return userRepository.findByEmailAndIsActive(email, UserStatus.ACTIVE);
    }

    private Date validateAndParseDateOfBirth(String dateOfBirthStr) {
        if (!StringUtils.hasText(dateOfBirthStr)) {
            return null;
        }
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateFormat.setLenient(false); // Avoid invalid dates like "2023-02-30"
            Date birthDate = dateFormat.parse(dateOfBirthStr);

            if (birthDate.after(new Date())) {
                throw new IllegalArgumentException("Date of birth must be in the past");
            }
            return birthDate;
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date format. Use yyyy-MM-dd", e);
        }
    }

    private void updateUserBasicFields(UserEntity user, ProfileRequestDTO request, Date birthDate) {
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setDateOfBirth(birthDate);
        user.setGender(request.getGender());
        user.setAddress(request.getAddress());
        user.setBio(request.getBio());
    }

    private void handleAvatarUpload(UserEntity user, MultipartFile avatar) {
        if (avatar == null || avatar.isEmpty()) {
            return;
        }

        try {
            FileValidationUtil.validateImageFile(avatar);
            String objectKey = generateAvatarObjectKey(avatar.getOriginalFilename());
            deleteOldAvatarIfExists(user.getAvatar());
            uploadNewAvatar(user, objectKey, avatar);

        } catch (Exception e) {
            throw new AvatarUploadException("Failed to upload avatar: " + e.getMessage());
        }
    }

    private String generateAvatarObjectKey(String originalFilename) {
        String fileName = UUID.randomUUID().toString();
        String extension = getFileExtension(originalFilename);
        return String.format("public/avatars/users/%s%s", fileName, extension);
    }

    private void deleteOldAvatarIfExists(String oldAvatarUrl) {
        if (oldAvatarUrl == null) {
            return;
        }

        try {
            s3Service.deleteObject(oldAvatarUrl);
        } catch (Exception e) {
            throw new AvatarNotFoundException("Failed to delete old avatar: " + e.getMessage());
        }
    }

    private void uploadNewAvatar(UserEntity user, String objectKey, MultipartFile avatar) throws AvatarUploadException, IOException {
        String avatarKey = s3Service.uploadFile(objectKey, avatar.getContentType(), avatar.getBytes());
        String avatarUrl = s3Service.generatePermanentUrl(avatarKey);
        user.setAvatar(avatarUrl);
    }

    private String getFileExtension(String filename) {
        if (filename == null) return "";
        int lastDotIndex = filename.lastIndexOf('.');
        return lastDotIndex == -1 ? "" : filename.substring(lastDotIndex);
    }

    @Override
    public UserDetailDTO getUserDetails(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        return userConverter.toUserDetailDTO(user);
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequestDTO request) {
        UserEntity currentUser = getCurrentUser();
        
        if (!passwordEncoder.matches(request.getCurrentPassword(), currentUser.getPassword())) {
            throw new IncorrectPasswordException();
        }
        
        if (passwordEncoder.matches(request.getNewPassword(), currentUser.getPassword())) {
            throw new SamePasswordException();
        }

        currentUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(currentUser);
    }

    @Override
    public String getDiscount(Long discountId) {
        DiscountEntity discountEntity = discountRepository.findById(discountId)
                .orElseThrow(() -> new DataNotFoundException("Discount not found"));
        if(userDiscountRepository.findByUserEntity_IdAndDiscountEntity_Id(getCurrentUser().getId(), discountEntity.getId()) != null) {
            throw new UserAlreadyOwnsDiscountException("You have already claimed this discount");
        }

        UserDiscountEntity userDiscountEntity = new UserDiscountEntity();
        userDiscountEntity.setDiscountEntity(discountEntity);
        userDiscountEntity.setUserEntity(getCurrentUser());
        userDiscountEntity.setIsActive(1L);
        userDiscountRepository.save(userDiscountEntity);
        discountScheduler.updateDiscountStatus(discountEntity);
        return "Get discount successfully";
    }


    public UserEntity getUserBySecurityContext() {
        SecurityContext context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();
        UserEntity userEntity = userRepository.findByEmailAndIsActive(email, UserStatus.ACTIVE);
        if(userEntity == null){
            throw new UserNotFoundException("User not found with email: " + email);
        }

        return userEntity;
    }

}
package com.coursehub.service.impl;

import com.coursehub.converter.DiscountConverter;
import com.coursehub.converter.UserConverter;
import com.coursehub.dto.request.discount.DiscountSearchRequestDTO;
import com.coursehub.dto.request.user.ChangePasswordRequestDTO;
import com.coursehub.dto.request.user.ProfileRequestDTO;
import com.coursehub.dto.response.discount.DiscountSearchResponseDTO;
import com.coursehub.dto.response.user.UserManagementDTO;
import com.coursehub.dto.response.user.UserResponseDTO;
import com.coursehub.entity.DiscountEntity;
import com.coursehub.entity.RoleEntity;
import com.coursehub.entity.UserDiscountEntity;
import com.coursehub.entity.UserEntity;
import com.coursehub.exceptions.auth.DataNotFoundException;
import com.coursehub.exceptions.user.*;
import com.coursehub.repository.DiscountRepository;
import com.coursehub.repository.RoleRepository;
import com.coursehub.repository.UserDiscountRepository;
import com.coursehub.repository.UserRepository;
import com.coursehub.service.S3Service;
import com.coursehub.service.UserService;
import com.coursehub.utils.FileValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserConverter userConverter;
    private final S3Service s3Service;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final DiscountRepository discountRepository;
    private final UserDiscountRepository userDiscountRepository;
    private final DiscountConverter discountConverter;
    private static final String USER_NOT_FOUND = "User not found";
    private static final String ACTIVE = "active";

    @Override
    public UserResponseDTO getMyInfo() {
        SecurityContext context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();
        UserEntity userEntity = userRepository.findByEmailAndIsActive(email, 1L);
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

    private UserEntity getCurrentUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();
        return userRepository.findByEmailAndIsActive(email, 1L);
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

    @Override
    @Transactional
    public void deleteProfile() {
        SecurityContext context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();
        UserEntity user = userRepository.findByEmailAndIsActive(email, 1L);
        if(user == null) {
            throw new DataNotFoundException(USER_NOT_FOUND);
        }

        if (user.getAvatar() != null) {
            s3Service.deleteObject(user.getAvatar());
        }

        user.setIsActive(0L);
        userRepository.save(user);
    }

    private String getFileExtension(String filename) {
        if (filename == null) return "";
        int lastDotIndex = filename.lastIndexOf('.');
        return lastDotIndex == -1 ? "" : filename.substring(lastDotIndex);
    }

    @Override
    public Page<UserManagementDTO> getAllUsers(Integer pageSize, Integer pageNo, String role, String status) {
        if (pageSize == null) pageSize = 10;
        if (pageNo == null) pageNo = 0;

        List<String> roles = Arrays.asList("LEARNER", "MANAGER");
        if (role != null && !role.isEmpty() && !role.equals("all")) {
            roles = Arrays.asList(role.toUpperCase());
        }

        Page<UserEntity> userPage;
        if (status != null && !status.isEmpty() && !status.equals("all")) {
            // Map status to isActive
            Long isActive = status.equals(ACTIVE) ? 1L : 0L;
            userPage = userRepository.findByRoleEntity_CodeInAndIsActive(
                roles,
                isActive,
                Pageable.ofSize(pageSize).withPage(pageNo)
            );
        } else {
            // Get all users regardless of status
            userPage = userRepository.findByRoleEntity_CodeIn(
                roles,
                Pageable.ofSize(pageSize).withPage(pageNo)
            );
        }
        
        return userPage.map(userConverter::convertToUserManagementDTO);
    }

    @Override
    public UserManagementDTO getUserDetails(Long userId) {
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
        return userConverter.convertToUserManagementDTO(user);
    }

    @Override
    @Transactional
    public void updateUserStatus(Long userId, String status) {
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new DataNotFoundException(USER_NOT_FOUND));
        
        // Map status string to isActive
        Long isActive = status.equals(ACTIVE) ? 1L : 0L;
        user.setIsActive(isActive);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateUserRole(Long userId, String role) {
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
        
        try {
            RoleEntity roleEntity = roleRepository.findByCode(role.toUpperCase());
            if (roleEntity == null) {
                throw new DataNotFoundException("Role not found");
            }

            user.setRoleEntity(roleEntity);
            userRepository.save(user);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid role: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));

        // Kiểm tra xem user có đăng ký khóa học nào không
        if (!user.getEnrollmentEntities().isEmpty()) {
            throw new UserDeletionException("User has enrolled courses and cannot be deleted");
        }

        if (user.getAvatar() != null) {
            try {
                s3Service.deleteObject(user.getAvatar());
            } catch (Exception e) {
                throw new AvatarNotFoundException("Failed to delete avatar: " + e.getMessage());
            }
        }

        userRepository.delete(user);
    }

    @Override
    @Transactional
    public UserManagementDTO createUser(ProfileRequestDTO request) {
        if (userRepository.existsByEmailAndIsActive(request.getEmail(), 1L)) {
            throw new IllegalArgumentException("Email already exists");
        }

        RoleEntity learnerRole = roleRepository.findByCode("MANAGER");
        if (learnerRole == null) {
            throw new DataNotFoundException("Default role not found");
        }

        UserEntity user = new UserEntity();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setIsActive(1L);

        user.setRoleEntity(learnerRole);

        UserEntity savedUser = userRepository.save(user);
        return userConverter.convertToUserManagementDTO(savedUser);
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequestDTO request) {
        // Get current user
        UserEntity currentUser = getCurrentUser();
        
        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), currentUser.getPassword())) {
            throw new IncorrectPasswordException();
        }
        
        // Check if new password is different from current
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
        return "Get discount successfully";
    }

    @Override
    public Page<DiscountSearchResponseDTO> getAllDiscounts(DiscountSearchRequestDTO discountSearchRequestDTO) {
        Pageable pageable = PageRequest.of(discountSearchRequestDTO.getPage(), discountSearchRequestDTO.getSize());
        Page<DiscountEntity> discountEntities = discountRepository.searchDiscountsOwner(
                discountSearchRequestDTO.getIsActive(),
                discountSearchRequestDTO.getCategoryId(),
                discountSearchRequestDTO.getCourseId(),
                getCurrentUser().getId(),
                discountSearchRequestDTO.getPercentage(),
                pageable
        );
        return discountConverter.toSearchResponseDTO(discountEntities);
    }


}

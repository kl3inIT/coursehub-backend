package com.coursehub.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.coursehub.converter.UserConverter;
import com.coursehub.dto.request.user.ProfileRequestDTO;
import com.coursehub.dto.response.user.UserManagementDTO;
import com.coursehub.dto.response.user.UserResponseDTO;
import com.coursehub.entity.RoleEntity;
import com.coursehub.entity.UserEntity;
import com.coursehub.entity.UserRoleEntity;
import com.coursehub.exception.auth.DataNotFoundException;
import com.coursehub.repository.RoleRepository;
import com.coursehub.repository.UserRepository;
import com.coursehub.service.S3Service;
import com.coursehub.service.UserService;
import com.coursehub.utils.FileValidationUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserConverter userConverter;
    private final S3Service s3Service;
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final RoleRepository roleRepository;

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
        // Get current user
        SecurityContext context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();
        UserEntity user = userRepository.findByEmailAndIsActive(email, 1L);

        // Validate email uniqueness if changed
        if (!user.getEmail().equals(request.getEmail())) {
            if (userRepository.existsByEmailAndIsActive(request.getEmail(), 1L)) {
                throw new IllegalArgumentException("Email already exists");
            }
        }

        // Validate dateOfBirth
        Date birthDate = null;
        if (StringUtils.hasText(request.getDateOfBirth())) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                dateFormat.setLenient(false);
                birthDate = dateFormat.parse(request.getDateOfBirth());

                // Validate date
                if (birthDate.after(new Date())) {
                    throw new IllegalArgumentException("Date of birth must be in the past");
                }
            } catch (ParseException e) {
                throw new IllegalArgumentException("Invalid date format. Use yyyy-MM-dd");
            }
        }

        // Update user fields
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setDateOfBirth(birthDate);
        user.setGender(request.getGender());
        user.setAddress(request.getAddress());
        user.setBio(request.getBio());

        // Handle avatar upload if user provided
        MultipartFile avatar = request.getAvatar();
        if (avatar != null && !avatar.isEmpty()) {
            try {
                // Validate file
                FileValidationUtil.validateImageFile(avatar);

                // Generate unique object key for S3
                String fileName = UUID.randomUUID().toString();
                String extension = getFileExtension(avatar.getOriginalFilename());
                String objectKey = String.format("public/avatars/users/%s%s", fileName, extension);

                // Delete old avatar if exists
                if (user.getAvatar() != null) {
                    try {
                        s3Service.deleteObject(user.getAvatar());
                    } catch (Exception e) {
                        log.warn("Failed to delete old avatar: {}", e.getMessage());
                    }
                }

                // Upload new avatar to S3
                String avatarKey = s3Service.uploadFile(objectKey, avatar.getContentType(), avatar.getBytes());
                String avatarUrl = s3Service.generatePermanentUrl(avatarKey);
                user.setAvatar(avatarUrl);
                
                log.info("Successfully uploaded new avatar for user: {}", user.getEmail());
            } catch (Exception e) {
                log.error("Failed to upload avatar: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to upload avatar: " + e.getMessage(), e);
            }
        }

        // Save all changes
        UserEntity savedUser = userRepository.save(user);
        return userConverter.toProfileRequestDTO(savedUser);
    }

    @Override
    @Transactional
    public void deleteProfile() {
        // Get current user
        SecurityContext context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();
        UserEntity user = userRepository.findByEmailAndIsActive(email, 1L);
        if(user == null) {
            throw new DataNotFoundException("User not found");
        }

        // Delete avatar if exists
        if (user.getAvatar() != null) {
            s3Service.deleteObject(user.getAvatar());
        }

        // Soft delete user
        user.setIsActive(0L);
        userRepository.save(user);
    }

    private String getFileExtension(String filename) {
        if (filename == null) return "";
        int lastDotIndex = filename.lastIndexOf('.');
        return lastDotIndex == -1 ? "" : filename.substring(lastDotIndex);
    }

    @Override
    public List<UserManagementDTO> getAllUsers() {
        List<UserEntity> users = userRepository.findAll();
        return users.stream()
            .map(userConverter::convertToUserManagementDTO)
            .collect(Collectors.toList());
    }

    @Override
    public UserManagementDTO getUserDetails(Long userId) {
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new DataNotFoundException("User not found"));
        return userConverter.convertToUserManagementDTO(user);
    }

    @Override
    @Transactional
    public void updateUserStatus(Long userId, String status) {
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new DataNotFoundException("User not found"));
        
        // Validate status
        if (!Arrays.asList("active", "inactive").contains(status)) {
            throw new IllegalArgumentException("Invalid status");
        }

        user.setIsActive(status.equals("active") ? 1L : 0L);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateUserRole(Long userId, String role) {
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new DataNotFoundException("User not found"));
        
        try {
            // Find role entity by code
            RoleEntity roleEntity = roleRepository.findByCode(role.toUpperCase());
            if (roleEntity == null) {
                throw new DataNotFoundException("Role not found");
            }

            // Create new UserRoleEntity
            UserRoleEntity userRoleEntity = new UserRoleEntity();
            userRoleEntity.setUserEntity(user);
            userRoleEntity.setRoleEntity(roleEntity);
            
            // Clear existing roles and add new one
            user.getUserRoleEntities().clear();
            user.getUserRoleEntities().add(userRoleEntity);
            
            userRepository.save(user);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid role: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new DataNotFoundException("User not found"));
        
        // Soft delete
        user.setIsActive(0L);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public UserManagementDTO createUser(ProfileRequestDTO request) {
        // Validate email uniqueness
        if (userRepository.existsByEmailAndIsActive(request.getEmail(), 1L)) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Find default LEARNER role
        RoleEntity learnerRole = roleRepository.findByCode("LEARNER");
        if (learnerRole == null) {
            throw new DataNotFoundException("Default role not found");
        }

        UserEntity user = new UserEntity();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setIsActive(1L);
        
        // Create default LEARNER role
        UserRoleEntity defaultRole = new UserRoleEntity();
        defaultRole.setUserEntity(user);
        defaultRole.setRoleEntity(learnerRole);
        user.getUserRoleEntities().add(defaultRole);

        UserEntity savedUser = userRepository.save(user);
        return userConverter.convertToUserManagementDTO(savedUser);
    }

}

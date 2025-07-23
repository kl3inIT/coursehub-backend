package com.coursehub.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.coursehub.exceptions.user.InvalidUserNameException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coursehub.components.OtpUtil;
import com.coursehub.converter.UserConverter;
import com.coursehub.dto.request.user.ProfileRequestDTO;
import com.coursehub.dto.response.user.UserDetailDTO;
import com.coursehub.dto.response.user.UserSummaryDTO;
import com.coursehub.entity.RoleEntity;
import com.coursehub.entity.UserEntity;
import com.coursehub.enums.ResourceType;
import com.coursehub.enums.UserStatus;
import com.coursehub.exceptions.auth.DataNotFoundException;
import com.coursehub.exceptions.user.UserAlreadyExistsException;
import com.coursehub.exceptions.user.UserNotFoundException;
import com.coursehub.repository.RoleRepository;
import com.coursehub.repository.UserRepository;
import com.coursehub.service.AdminService;
import com.coursehub.service.NotificationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;
    private final UserConverter userConverter;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpUtil otpUtil;

    private static final String USER_NOT_FOUND = "User not found";
    private final NotificationService notificationService;


    @Override
    public Page<UserSummaryDTO> getAllUsers(Integer pageSize, Integer pageNo, String role, UserStatus status) {
        if (pageNo == null || pageNo < 0) pageNo = 0;
        if (pageSize == null || pageSize <= 0) pageSize = 10;

        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("createdDate").descending());

        List<String> roles = new ArrayList<>();
        if (role == null || role.equalsIgnoreCase("all")) {
            roles.add("LEARNER");
            roles.add("MANAGER");
        } else {
            roles.add(role.toUpperCase());
        }

        if (status == null || "all".equalsIgnoreCase(String.valueOf(status))) {
            return userRepository.findUserSummaries(roles, pageable);
        } else {
            return userRepository.findUserSummariesWithStatus(roles, status, pageable);
        }
    }

    @Override
    @Transactional
    public UserDetailDTO createManager(ProfileRequestDTO request) {
        if (userRepository.existsByEmailAndIsActive(request.getEmail(), UserStatus.ACTIVE)) {
            throw new UserAlreadyExistsException("Email already exists");
        }

        if(request.getName().length() > 50) {
            throw new InvalidUserNameException("Name length exceeds maximum limit of 50 characters");
        }

        if(request.getEmail().length() > 100) {
            throw new DataNotFoundException("Email length exceeds maximum limit of 100 characters");
        }

        RoleEntity managerRole = roleRepository.findByCode("MANAGER");
        if (managerRole == null) {
            throw new DataNotFoundException("Default role 'MANAGER' not found");
        }

        String temporaryPassword = generateTemporaryPassword(8);

        UserEntity user = new UserEntity();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setIsActive(UserStatus.ACTIVE);
        user.setPassword(passwordEncoder.encode(temporaryPassword));
        user.setRoleEntity(managerRole);

        user.setPhone(request.getPhone());
        user.setBio(request.getBio());

        UserEntity savedUser = userRepository.save(user);

        // Send welcome email with temporary password
        otpUtil.sendPasswordToManager(savedUser.getEmail(), temporaryPassword);

        return userConverter.toUserDetailDTO(savedUser);
    }

    private String generateTemporaryPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    @Override
    @Transactional
    public void addWarning(Long userId, ResourceType resourceType, Long resourceId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));

        long currentWarnings = user.getWarningCount() != null ? user.getWarningCount() : 0L;
        user.setWarningCount(currentWarnings + 1);

        notificationService.notifyWarn(userId, resourceId, String.valueOf(resourceType));

        if (user.getWarningCount() >= 5) {
            user.setIsActive(UserStatus.BANNED);
            user.setActionReason("User has been banned due to excessive warnings");
            user.setLastActionAt(new Date());
            notificationService.notifyBan(userId, null);
        }

        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateUserStatus(Long userId, UserStatus status, String reason) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException(USER_NOT_FOUND));
        
        if ("Active".equalsIgnoreCase(status.getStatus())) {
            user.setIsActive(UserStatus.ACTIVE);
            user.setActionReason("Unban: " + reason);
            user.setLastActionAt(new Date());
            notificationService.notifyUnban(userId, reason);
        } else if ("Banned".equalsIgnoreCase(status.getStatus())) {
            user.setIsActive(UserStatus.BANNED);
            user.setActionReason("Ban: " + reason);
            user.setLastActionAt(new Date());
            notificationService.notifyBan(userId, reason);
        } else {
            user.setIsActive(UserStatus.INACTIVE);
            user.setActionReason(reason);
            user.setLastActionAt(new Date());
            notificationService.notifyBan(userId, reason);
        }
        userRepository.save(user);
    }

    @Override
    public UserDetailDTO getUserDetails(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        return userConverter.toUserDetailDTO(user);
    }
}

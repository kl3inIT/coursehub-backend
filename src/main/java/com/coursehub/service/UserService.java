package com.coursehub.service;

import com.coursehub.entity.UserEntity;
import com.coursehub.enums.ResourceType;
import com.coursehub.enums.UserStatus;
import org.springframework.data.domain.Page;
import com.coursehub.dto.request.user.ChangePasswordRequestDTO;
import com.coursehub.dto.request.user.ProfileRequestDTO;
import com.coursehub.dto.response.user.UserManagementDTO;
import com.coursehub.dto.response.user.UserResponseDTO;

public interface UserService {

    UserResponseDTO getMyInfo();
    ProfileRequestDTO updateProfile(ProfileRequestDTO request);
    void deleteProfile();
    
    // User Management methods
    Page<UserManagementDTO> getAllUsers(Integer pageSize, Integer pageNo, String role, UserStatus status);
    UserManagementDTO getUserDetails(Long userId);
    void updateUserStatus(Long userId, UserStatus status);
    UserManagementDTO createManager(ProfileRequestDTO request);
    void changePassword(ChangePasswordRequestDTO request);
    String getDiscount(Long discountId);
    void addWarning(Long userId, ResourceType resourceType, Long resourceId);
    UserEntity getUserBySecurityContext();

}

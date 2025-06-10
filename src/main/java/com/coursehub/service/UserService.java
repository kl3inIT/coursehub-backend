package com.coursehub.service;

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
    Page<UserManagementDTO> getAllUsers(Integer pageSize, Integer pageNo, String role, String status);
    UserManagementDTO getUserDetails(Long userId);
    void updateUserStatus(Long userId, String status);
    void updateUserRole(Long userId, String role);
    void deleteUser(Long userId);
    UserManagementDTO createUser(ProfileRequestDTO request);
    void changePassword(ChangePasswordRequestDTO request);
}

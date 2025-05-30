package com.coursehub.service;

import java.util.List;

import com.coursehub.dto.request.user.ProfileRequestDTO;
import com.coursehub.dto.response.user.UserManagementDTO;
import com.coursehub.dto.response.user.UserResponseDTO;

public interface UserService {

    UserResponseDTO getMyInfo();
    ProfileRequestDTO updateProfile(ProfileRequestDTO request);
    void deleteProfile();
    
    // User Management methods
    List<UserManagementDTO> getAllUsers();
    UserManagementDTO getUserDetails(Long userId);
    void updateUserStatus(Long userId, String status);
    void updateUserRole(Long userId, String role);
    void deleteUser(Long userId);
    UserManagementDTO createUser(ProfileRequestDTO request);
}

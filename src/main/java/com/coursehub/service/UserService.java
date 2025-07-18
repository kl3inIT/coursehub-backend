package com.coursehub.service;

import com.coursehub.dto.request.user.ChangePasswordRequestDTO;
import com.coursehub.dto.request.user.ProfileRequestDTO;
import com.coursehub.dto.response.user.UserDetailDTO;
import com.coursehub.dto.response.user.UserResponseDTO;
import com.coursehub.entity.UserEntity;


public interface UserService {

    UserResponseDTO getMyInfo();
    ProfileRequestDTO updateProfile(ProfileRequestDTO request);
    UserEntity getCurrentUser();
    // User Management methods
    UserDetailDTO getUserDetails(Long userId);
    void changePassword(ChangePasswordRequestDTO request);
    String getDiscount(Long discountId);
    UserEntity getUserBySecurityContext();
    Long countUsers();
    Long countUserIsActive();
}

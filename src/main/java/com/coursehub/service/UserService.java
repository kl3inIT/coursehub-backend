package com.coursehub.service;

import com.coursehub.dto.request.discount.DiscountSearchRequestDTO;
import com.coursehub.dto.response.discount.DiscountSearchResponseDTO;
import com.coursehub.entity.UserEntity;
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
    void deleteManager(Long userId);
    UserManagementDTO createManager(ProfileRequestDTO request);
    void changePassword(ChangePasswordRequestDTO request);
    String getDiscount(Long discountId);
    Page<DiscountSearchResponseDTO> getAllDiscounts(DiscountSearchRequestDTO discountSearchRequestDTO);
    void addWarning(Long userId);
    UserEntity getUserBySecurityContext();

}

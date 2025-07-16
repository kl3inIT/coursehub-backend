package com.coursehub.service;

import org.springframework.data.domain.Page;

import com.coursehub.dto.request.user.ProfileRequestDTO;
import com.coursehub.dto.response.user.UserDetailDTO;
import com.coursehub.dto.response.user.UserSummaryDTO;
import com.coursehub.enums.ResourceType;
import com.coursehub.enums.UserStatus;

public interface AdminService {
    Page<UserSummaryDTO> getAllUsers(Integer pageSize, Integer pageNo, String role, UserStatus status);
    UserDetailDTO createManager(ProfileRequestDTO request);
    void addWarning(Long userId, ResourceType resourceType, Long resourceId);
    void updateUserStatus(Long userId, UserStatus status, String reason);
    UserDetailDTO getUserDetails(Long userId);
}

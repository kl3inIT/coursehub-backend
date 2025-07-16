package com.coursehub.dto.request.user;

import com.coursehub.enums.UserStatus;

import lombok.Data;

@Data
public class UpdateStatusRequest {
    private UserStatus status;
    private String reason;
} 
package com.coursehub.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequestDTO {
    
    @NotBlank(message = "Current password is required")
    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
    private String currentPassword;

    @NotBlank(message = "New password is required")
    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
    private String newPassword;
} 
package com.coursehub.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OtpRequestDTO {

    @NotBlank(message = "Email is required")
    private String email;
    private String otp;
}

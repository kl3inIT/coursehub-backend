package com.coursehub.dto.request.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OtpRequestDTO {
    private String email;
    private String otp;
}

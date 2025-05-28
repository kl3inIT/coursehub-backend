package com.coursehub.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticationRequestDTO {

    @NotBlank(message = "username is required")
    private String email;

    @NotBlank(message = "password is required")
    private String password;


}
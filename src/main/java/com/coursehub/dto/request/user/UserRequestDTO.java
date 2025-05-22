package com.coursehub.dto.request.user;

import jakarta.validation.constraints.NotBlank;

public class UserRequestDTO {


    @NotBlank(message = "username is required")
    private String name;

    @NotBlank(message = "password is required")
    private String password;

    @NotBlank(message = "confirm password is required")
    private String confirmPassword;

    @NotBlank(message = "email password is required")
    private String email;

}



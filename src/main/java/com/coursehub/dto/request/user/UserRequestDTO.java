package com.coursehub.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDTO {


    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "password is required")
    private String password;

    @NotBlank(message = "confirm password is required")
    private String confirmPassword;

    @NotBlank(message = "email is required")
    private String email;

}



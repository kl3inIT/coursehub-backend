package com.coursehub.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDTO {

    @NotBlank(message = "name is required")
    @Size(min = 2, max = 50, message = "name must be between 2 and 50 characters")
    private String name;

    @NotBlank(message = "password is required")
    @Size(min = 6, max = 20, message = "password must be between 6 and 20 characters")
    private String password;

    @NotBlank(message = "confirm password is required")
    @Size(min = 6, max = 20, message = "confirm password must be between 6 and 20 characters")
    private String confirmPassword;

    @NotBlank(message = "email is required")
    @Size(max = 100, message = "email must be less than 100 characters")
    private String email;

    private String role;

}



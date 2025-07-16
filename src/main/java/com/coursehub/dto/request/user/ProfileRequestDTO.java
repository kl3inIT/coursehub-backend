package com.coursehub.dto.request.user;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileRequestDTO {
    @NotBlank(message = "name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    @NotBlank(message = "email is required")
    @Size(max = 100, message = "Email must be less than 100 characters")
    private String email;

    @Size(max = 10, message = "Phone number must be less than 10 characters")
    @Pattern(regexp = "^\\+?[\\d\\s-]{10,}$", message = "Invalid phone number format")
    private String phone;

    private String dateOfBirth;

    private String gender;

    @Size(max = 200, message = "Address must be less than 200 characters")
    private String address;

    @Size(max = 500, message = "Bio must be less than 500 characters")
    private String bio;

    @JsonIgnore // Để không serialize field này khi chuyển thành JSON
    private MultipartFile avatar;


}

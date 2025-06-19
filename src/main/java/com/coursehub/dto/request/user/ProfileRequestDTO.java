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
    private String name;

    @NotBlank(message = "email is required")
    private String email;

    @Pattern(regexp = "^\\+?[\\d\\s-]{10,}$", message = "Invalid phone number format")
    private String phone;

    private String dateOfBirth;

    private String gender;

    private String address;

    @Size(max = 500, message = "Bio must be less than 500 characters")
    private String bio;

    @JsonIgnore // Để không serialize field này khi chuyển thành JSON
    private MultipartFile avatar;


}

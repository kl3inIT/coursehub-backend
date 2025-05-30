package com.coursehub.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticationRequestDTO {

    @NotBlank(message = "username is required")
    private String email;

    private String password;


    // phan nay danh cho google
    private String googleAccountId;

    private String name;

    private String avatar;

    private String phone;




}
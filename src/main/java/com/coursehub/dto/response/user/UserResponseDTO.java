package com.coursehub.dto.response.user;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDTO {
    private int id;
    private String email;
    private String name;
    private String fullName;
    private String phone;
    private Date dateOfBirth;
    private String gender;
    private String address;
    private String bio;
    private String avatar;
    private List<String> roles;
}

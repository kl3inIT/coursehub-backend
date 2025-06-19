package com.coursehub.dto.response.user;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserManagementDTO {
    private Long id;
    private String name;
    private String email;
    private String avatar;
    private String role;
    private String status;
    private Date joinDate;
    private String bio;
    private List<UserActivityDTO> activities;
}

package com.coursehub.dto.response.user;

import java.util.Date;

import com.coursehub.enums.UserStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSummaryDTO {
    private Long id;
    private String name;
    private String email;
    private String avatar;
    private String role;
    private UserStatus status;
    private Date joinDate;
    private Long enrolledCoursesCount;
    private Long managedCoursesCount;
} 
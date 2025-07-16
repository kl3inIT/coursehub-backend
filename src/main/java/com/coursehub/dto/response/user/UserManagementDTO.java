package com.coursehub.dto.response.user;

import java.util.Date;
import java.util.List;

import com.coursehub.dto.response.course.CourseBasicDTO;
import com.coursehub.enums.UserStatus;

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
    private UserStatus status;
    private Date joinDate;
    private String bio;
    private List<UserActivityDTO> activities;
    private Integer enrolledCoursesCount;
    private Integer managedCoursesCount;
    private List<CourseBasicDTO> enrolledCourses;
    private List<CourseBasicDTO> managedCourses;
}

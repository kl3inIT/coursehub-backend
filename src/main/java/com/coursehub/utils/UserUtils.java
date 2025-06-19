package com.coursehub.utils;

import com.coursehub.entity.CourseEntity;
import com.coursehub.entity.UserEntity;

public class UserUtils {

    private UserUtils() {
        // Private constructor to prevent instantiation
    }

    public static boolean isAdmin(UserEntity user) {
        return user != null &&
                user.getRoleEntity() != null &&
                "ADMIN".equalsIgnoreCase(user.getRoleEntity().getCode());
    }


    public static boolean isManager(UserEntity user) {
        return user != null &&
                user.getRoleEntity() != null &&
                "MANAGER".equalsIgnoreCase(user.getRoleEntity().getCode());
    }

    public static boolean isActive(UserEntity user) {
        return user != null && user.getIsActive() != null && user.getIsActive() == 1L;
    }

    public static boolean isOwner(UserEntity user, CourseEntity course) {
        return user != null &&
                course != null &&
                course.getUserEntity() != null &&
                user.getId().equals(course.getUserEntity().getId());
    }

    public static boolean hasRole(UserEntity user, String roleCode) {
        return user != null &&
                user.getRoleEntity() != null &&
                roleCode.equalsIgnoreCase(user.getRoleEntity().getCode());
    }
}

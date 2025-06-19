package com.coursehub.enums;

import com.coursehub.exceptions.course.InvalidCourseStatusException;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum CourseStatus {

    DRAFT("Draft"),
    PUBLISHED("Published"),
    ARCHIVED("Archived");

    private final String status;

    CourseStatus(String status) {
        this.status = status;
    }

    public String getStatusName() {
        return status;
    }

    public static Map<String, String> getCourseStatuses() {
        return Arrays.stream(CourseStatus.values())
                .collect(Collectors.toMap(CourseStatus::toString, CourseStatus::getStatusName));
    }

    public static CourseStatus fromString(String status) {
        try {
            return CourseStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidCourseStatusException(String.format("Invalid course status: %s", status));
        }
    }

}

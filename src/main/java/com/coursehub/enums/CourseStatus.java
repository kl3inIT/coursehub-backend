package com.coursehub.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum CourseStatus {

    DRAFT("Draft"),
    PUBLISHED("Published"),
    ARCHIVED("Archived"),
    OPEN_FOR_ENROLLMENT("Open for Enrollment"),
    CLOSED_FOR_ENROLLMENT("Closed for Enrollment");

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

}

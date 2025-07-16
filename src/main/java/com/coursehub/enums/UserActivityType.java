package com.coursehub.enums;

import lombok.Getter;

@Getter
public enum UserActivityType {
    COMMENT("comment", "User commented on a lesson"),
    REVIEW("review", "User wrote a review for a course"),
    ENROLLMENT("enrollment", "User enrolled in a course"),
    COURSE_CREATION("course_creation", "Manager created a new course"),
    COURSE_UPDATE("course_update", "Manager updated a course"),
    LESSON_COMPLETION("lesson_completion", "User completed a lesson"),
    COURSE_COMPLETION("course_completion", "User completed a course"),
    QUIZ_ATTEMPT("quiz_attempt", "User attempted a quiz"),
    LESSON_CREATION("lesson_creation", "Manager created a lesson"),
    LESSON_UPDATE("lesson_update", "Manager updated a lesson");

    private final String value;
    private final String description;

    UserActivityType(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public static UserActivityType fromValue(String value) {
        for (UserActivityType type : UserActivityType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown activity type: " + value);
    }
} 
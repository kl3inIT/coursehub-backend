package com.coursehub.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum CourseLevel {
    @JsonProperty("beginner")
    BEGINNER,
    @JsonProperty("intermediate")
    INTERMEDIATE,
    @JsonProperty("advanced")
    ADVANCED
}
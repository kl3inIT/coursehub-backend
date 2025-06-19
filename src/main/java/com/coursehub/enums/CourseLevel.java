package com.coursehub.enums;

import com.coursehub.exceptions.course.InvalidCourseLevelException;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public enum CourseLevel {

    BEGINNER("Beginner"),

    INTERMEDIATE("Intermediate"),

    ADVANCED("Advanced");

    private final String levelName;

    CourseLevel(String levelName) {
        this.levelName = levelName;
    }

    public static Map<String, String> getCourseLevels(){
       return Arrays.stream(CourseLevel.values()).collect(Collectors.toMap(CourseLevel::toString, CourseLevel::getLevelName));
    }

    public static CourseLevel fromString(String level) {
        try {
            return CourseLevel.valueOf(level.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidCourseLevelException(String.format("Invalid course level: %s", level));
        }
    }



}
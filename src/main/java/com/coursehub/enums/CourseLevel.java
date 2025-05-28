package com.coursehub.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum CourseLevel {

    BEGINNER("Beginner"),

    INTERMEDIATE("Intermediate"),

    ADVANCED("Advanced");

    private final String levelName;

    CourseLevel(String levelName) {
        this.levelName = levelName;
    }


    public static Map<String, String> getCourseLevels(){
       return Arrays.stream(CourseLevel.values()).collect(Collectors.toMap(CourseLevel::toString, CourseLevel::getLevelName ));
    }

    public String getLevelName() {
        return levelName;
    }

}
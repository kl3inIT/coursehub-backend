package com.coursehub.enums;

public enum TargetGroup {
    ALL_USERS("All users"),
    LEARNERS_ONLY("Learners only"),
    MANAGERS_ONLY("Managers only");

    private final String description;

    TargetGroup(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }
}

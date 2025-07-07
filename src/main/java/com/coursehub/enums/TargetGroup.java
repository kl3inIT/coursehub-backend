package com.coursehub.enums;

public enum TargetGroup {
    ALL("All users"),
    LEARNER("Learners"),
    MANAGER("Managers");

    private final String description;

    TargetGroup(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }
}

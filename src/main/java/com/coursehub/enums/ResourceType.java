package com.coursehub.enums;

public enum ResourceType {
    REVIEW("review"),
    COMMENT("comment");

    private final String type;

    ResourceType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

}

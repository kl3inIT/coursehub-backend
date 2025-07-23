package com.coursehub.enums;

public enum AnnouncementStatus {
    DRAFT("Draft"),
    SCHEDULED("Scheduled"),
    SENT("Sent"),
    HIDDEN("Hidden"),
    CANCELLED("Cancelled");

    private final String displayName;

    AnnouncementStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
} 
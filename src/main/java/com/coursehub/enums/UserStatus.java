package com.coursehub.enums;

public enum UserStatus {

    ACTIVE("Active"),
    INACTIVE("Inactive"),
    BANNED("Banned");

    private final String status;

    UserStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return status;
    }
}

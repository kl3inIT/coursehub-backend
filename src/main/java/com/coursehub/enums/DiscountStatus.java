package com.coursehub.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum DiscountStatus {
    NOT_STARTED("Not Started"),
    AVAILABLE("Available"),
    OUT_OF_STOCK("Out of Stock"),
    USED_UP("Used Up"),
    EXPIRED("Expired");

    private final String status;

    public String status() {
        return status;
    }

    DiscountStatus(String status) {
        this.status = status;
    }
    public static Map<String, String> getDiscountStatus() {
        return Arrays.stream(DiscountStatus.values())
                .collect(Collectors.toMap(DiscountStatus::toString, DiscountStatus::status));
    }

}

package com.coursehub.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public enum ReportStatus {
    PENDING("Pending"),
    IN_PROGRESS("In Progress"),
    RESOLVED("Resolved"),
    DISMISS ("Dismiss");

    private final String status;

    ReportStatus(String status) {
        this.status = status;
    }

    public static Map<String, String> getReportStatuses() {
        return Arrays.stream(ReportStatus.values())
                .collect(Collectors.toMap(ReportStatus::toString, ReportStatus::getStatus));
    }
}

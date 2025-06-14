package com.coursehub.enums;

public enum ReportSeverity {
    LOW ("Low"),
    MEDIUM ("Medium"),
    HIGH ("High");

    private final String severity;

    ReportSeverity(String severity) {
        this.severity = severity;
    }

    public String getSeverity() {
        return severity;
    }

}


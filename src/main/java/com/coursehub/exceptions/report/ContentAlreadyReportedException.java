package com.coursehub.exceptions.report;

public class ContentAlreadyReportedException extends RuntimeException {
    public ContentAlreadyReportedException(String message) {
        super(message);
    }
}

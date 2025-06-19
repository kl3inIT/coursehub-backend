package com.coursehub.exceptions.analytics;

public class AnalyticsRetrievalException extends RuntimeException {
    public AnalyticsRetrievalException(String message) {
        super(message);
    }

    public AnalyticsRetrievalException(String message, Throwable cause) {
        super(message, cause);
    }
} 
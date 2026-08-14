package com.example.cv.analytics.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.Instant;
import java.util.Map;

public final class AnalyticsRequests {
    private AnalyticsRequests() {
    }

    public record CreateRequest(@NotBlank String event_type, String resource_id, String user_id,
                                String session_id, String ip_address, String user_agent,
                                Map<String, Object> metadata) {
    }

    public record SearchRequest(String event_type, String user_id, String resource_id,
                                Instant start_date, Instant end_date, Integer current, Integer pageSize) {
    }
}

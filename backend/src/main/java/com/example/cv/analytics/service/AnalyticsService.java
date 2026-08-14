package com.example.cv.analytics.service;

import com.example.cv.common.service.PaginationService;
import com.example.cv.analytics.dto.AnalyticsRequests;
import com.example.cv.analytics.entity.AnalyticsDocument;
import com.example.cv.analytics.repository.AnalyticsRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalyticsService {
    private final AnalyticsRepository repository;

    public AnalyticsService(AnalyticsRepository repository) {
        this.repository = repository;
    }

    public AnalyticsDocument create(AnalyticsRequests.CreateRequest request) {
        return repository.save(new AnalyticsDocument(request.event_type(), request.resource_id(), request.user_id(),
                request.session_id(), request.ip_address(), request.user_agent(), request.metadata()));
    }

    public AnalyticsDocument trackEvent(AnalyticsRequests.CreateRequest request) {
        return create(request);
    }

    public Map<String, Object> findAll(AnalyticsRequests.SearchRequest request) {
        int current = request.current() == null || request.current() < 1 ? 1 : request.current();
        int pageSize = request.pageSize() == null || request.pageSize() < 1 ? 10 : request.pageSize();
        var page = repository.search(request.event_type(), request.user_id(), request.resource_id(),
                request.start_date(), request.end_date(),
                PageRequest.of(current - 1, pageSize, Sort.by(Sort.Direction.DESC, "createdAt")));
        return Map.of("meta", Map.of("current", current, "pageSize", pageSize,
                        "pages", page.getTotalPages(), "total", page.getTotalElements()),
                "result", page.getContent());
    }

    public AnalyticsDocument findOne(long id) {
        return repository.findById(id).orElseThrow(() -> com.example.cv.common.api.ApiException.notFound("Analytics not found"));
    }

    public List<Map<String, Object>> eventStats() {
        return repository.statsByEventType().stream().map(row -> Map.of("event_type", row[0], "count", row[1])).toList();
    }

    public List<Map<String, Object>> dailyStats(int days) {
        Instant start = Instant.now().minus(Math.max(days, 1), ChronoUnit.DAYS);
        return repository.dailyStats(start).stream().map(row -> Map.of("date", row[0], "count", row[1])).toList();
    }

    public List<Map<String, Object>> topUsers(int limit) {
        return repository.topUsers(Math.max(limit, 1)).stream().map(row -> Map.of("user_id", row[0], "count", row[1])).toList();
    }

    public void remove(long id) {
        repository.deleteById(id);
    }
}

package com.example.cv.analytics.controller;

import com.example.cv.common.api.PublicEndpoint;
import com.example.cv.common.api.ResponseMessage;
import com.example.cv.analytics.dto.AnalyticsRequests;
import com.example.cv.analytics.entity.AnalyticsDocument;
import com.example.cv.analytics.service.AnalyticsService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {
    private final AnalyticsService service;

    public AnalyticsController(AnalyticsService service) {
        this.service = service;
    }

    @PostMapping
    @PublicEndpoint
    @ResponseMessage("Track analytics event")
    public AnalyticsDocument create(@Valid @RequestBody AnalyticsRequests.CreateRequest request) {
        return service.create(request);
    }

    @PostMapping("/track")
    @PublicEndpoint
    @ResponseMessage("Track event")
    public AnalyticsDocument track(@Valid @RequestBody AnalyticsRequests.CreateRequest request) {
        return service.create(request);
    }

    @GetMapping
    @ResponseMessage("Fetch analytics data")
    public Map<String, Object> findAll(AnalyticsRequests.SearchRequest request) {
        return service.findAll(request);
    }

    @GetMapping("/stats/events")
    @ResponseMessage("Get analytics stats by event type")
    public List<Map<String, Object>> eventStats() {
        return service.eventStats();
    }

    @GetMapping("/stats/daily")
    @ResponseMessage("Get daily analytics stats")
    public List<Map<String, Object>> dailyStats(@RequestParam(required = false, defaultValue = "30") int days) {
        return service.dailyStats(days);
    }

    @GetMapping("/stats/users")
    @ResponseMessage("Get top users")
    public List<Map<String, Object>> topUsers(@RequestParam(required = false, defaultValue = "10") int limit) {
        return service.topUsers(limit);
    }

    @GetMapping("/dashboard")
    @ResponseMessage("Get dashboard analytics data")
    public Map<String, Object> dashboard() {
        return Map.of("eventStats", service.eventStats(), "dailyStats", service.dailyStats(7),
                "topUsers", service.topUsers(5));
    }

    @GetMapping("/{id}")
    @ResponseMessage("Get analytics by id")
    public AnalyticsDocument findOne(@PathVariable long id) {
        return service.findOne(id);
    }

    @DeleteMapping("/{id}")
    @ResponseMessage("Delete analytics record")
    public Map<String, Object> remove(@PathVariable long id) {
        service.remove(id);
        return Map.of("deleted", true);
    }
}

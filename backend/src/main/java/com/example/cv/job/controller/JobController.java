package com.example.cv.job.controller;

import com.example.cv.common.api.PublicEndpoint;
import com.example.cv.common.api.ResponseMessage;
import com.example.cv.common.security.SecurityUtils;
import com.example.cv.job.dto.JobRequests;
import com.example.cv.job.service.JobService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/jobs")
public class JobController {
    private final JobService service;

    public JobController(JobService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseMessage("Create a new job")
    public Map<String, Object> create(@Valid @RequestBody JobRequests.CreateRequest request) {
        return service.create(request, SecurityUtils.currentUser());
    }

    @GetMapping
    @PublicEndpoint
    public Map<String, Object> findAll(@RequestParam(required = false, defaultValue = "1") Integer current,
                                       @RequestParam(required = false) Integer pageSize,
                                       @RequestParam(required = false) String name,
                                       @RequestParam(required = false) String location,
                                       @RequestParam(required = false) String level) {
        return service.findAll(current, pageSize, name, location, level);
    }

    @GetMapping("/{id}")
    @PublicEndpoint
    public Object findOne(@PathVariable String id) {
        return service.findOne(id, SecurityUtils.optionalUser());
    }

    @PatchMapping("/{id}")
    public Object update(@PathVariable String id, @RequestBody JobRequests.UpdateRequest request) {
        return service.update(id, request, SecurityUtils.currentUser());
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> remove(@PathVariable String id) {
        return service.remove(id, SecurityUtils.currentUser());
    }
}

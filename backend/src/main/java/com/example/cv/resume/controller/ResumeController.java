package com.example.cv.resume.controller;

import com.example.cv.common.api.ResponseMessage;
import com.example.cv.common.security.SecurityUtils;
import com.example.cv.resume.dto.ResumeRequests;
import com.example.cv.resume.service.ResumeService;
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

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/resumes")
public class ResumeController {
    private final ResumeService service;

    public ResumeController(ResumeService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseMessage("Create a new resume")
    public Map<String, Object> create(@Valid @RequestBody ResumeRequests.CreateRequest request) {
        return service.create(request, SecurityUtils.currentUser());
    }

    @GetMapping
    @ResponseMessage("Fetch a list resume")
    public Map<String, Object> findAll(@RequestParam(required = false, defaultValue = "1") Integer current,
                                       @RequestParam(required = false) Integer pageSize) {
        return service.findAll(current, pageSize);
    }

    @GetMapping("/{id}")
    @ResponseMessage("Fetch a resume")
    public Object findOne(@PathVariable String id) {
        return service.findOne(id);
    }

    @PatchMapping("/{id}")
    @ResponseMessage("Update a resume")
    public Object update(@PathVariable String id, @Valid @RequestBody ResumeRequests.UpdateRequest request) {
        return service.update(id, request, SecurityUtils.currentUser());
    }

    @DeleteMapping("/{id}")
    @ResponseMessage("Delete a resume")
    public Map<String, Object> remove(@PathVariable String id) {
        return service.remove(id, SecurityUtils.currentUser());
    }

    @PostMapping("/by-user")
    @ResponseMessage("Fetch resume by user")
    public List<?> byUser() {
        return service.byUser(SecurityUtils.currentUser());
    }
}

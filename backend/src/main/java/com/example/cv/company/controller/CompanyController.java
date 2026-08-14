package com.example.cv.company.controller;

import com.example.cv.common.api.PublicEndpoint;
import com.example.cv.common.api.ResponseMessage;
import com.example.cv.common.security.SecurityUtils;
import com.example.cv.company.dto.CompanyRequests;
import com.example.cv.company.service.CompanyService;
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
@RequestMapping("/api/v1/companies")
public class CompanyController {
    private final CompanyService service;

    public CompanyController(CompanyService service) {
        this.service = service;
    }

    @PostMapping
    public Object create(@Valid @RequestBody CompanyRequests.CreateRequest request) {
        return service.create(request, SecurityUtils.currentUser());
    }

    @GetMapping
    @PublicEndpoint
    @ResponseMessage("List Company")
    public Map<String, Object> findAll(@RequestParam(required = false, defaultValue = "1") Integer current,
                                       @RequestParam(required = false) Integer pageSize,
                                       @RequestParam(required = false) String name,
                                       @RequestParam(required = false) String address) {
        return service.findAll(current, pageSize, name, address);
    }

    @GetMapping("/{id}")
    @PublicEndpoint
    public Object findOne(@PathVariable String id) {
        return service.findOne(id);
    }

    @PatchMapping("/{id}")
    public Object update(@PathVariable String id, @RequestBody CompanyRequests.UpdateRequest request) {
        return service.update(id, request, SecurityUtils.currentUser());
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> remove(@PathVariable String id) {
        return service.remove(id, SecurityUtils.currentUser());
    }
}

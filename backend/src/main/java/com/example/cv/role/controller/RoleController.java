package com.example.cv.role.controller;

import com.example.cv.common.api.ResponseMessage;
import com.example.cv.common.security.SecurityUtils;
import com.example.cv.role.dto.RoleRequests;
import com.example.cv.role.service.RoleService;
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
@RequestMapping("/api/v1/roles")
public class RoleController {
    private final RoleService service;

    public RoleController(RoleService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseMessage("Thêm mới một role")
    public Map<String, Object> create(@Valid @RequestBody RoleRequests.CreateRequest request) {
        return service.create(request, SecurityUtils.currentUser());
    }

    @PatchMapping("/{id}")
    public Object update(@PathVariable String id, @RequestBody RoleRequests.UpdateRequest request) {
        return service.update(id, request, SecurityUtils.currentUser());
    }

    @GetMapping
    @ResponseMessage("Lấy danh sách role với phân trang")
    public Map<String, Object> findAll(@RequestParam(required = false, defaultValue = "1") Integer current,
                                       @RequestParam(required = false) Integer pageSize) {
        return service.findAll(current, pageSize);
    }

    @GetMapping("/{id}")
    @ResponseMessage("Lấy thông tin một role")
    public Map<String, Object> findOne(@PathVariable String id) {
        return service.findOne(id);
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> remove(@PathVariable String id) {
        return service.remove(id, SecurityUtils.currentUser());
    }
}

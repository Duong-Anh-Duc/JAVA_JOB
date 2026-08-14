package com.example.cv.user.controller;

import com.example.cv.common.api.PublicEndpoint;
import com.example.cv.common.api.ResponseMessage;
import com.example.cv.common.security.SecurityUtils;
import com.example.cv.user.dto.UserRequests;
import com.example.cv.user.service.UserService;
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
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseMessage("Create a new User")
    public Object create(@Valid @RequestBody UserRequests.CreateRequest request) {
        return service.create(request, SecurityUtils.currentUser());
    }

    @GetMapping
    @ResponseMessage("Fetch user with paginate")
    public Map<String, Object> findAll(@RequestParam(required = false, defaultValue = "1") Integer current,
                                       @RequestParam(required = false) Integer pageSize,
                                       @RequestParam(required = false) String email,
                                       @RequestParam(required = false) String name,
                                       @RequestParam(required = false) String gender) {
        return service.findAll(current, pageSize, email, name, gender);
    }

    @GetMapping("/{id}")
    @PublicEndpoint
    @ResponseMessage("Fetch user by id")
    public Map<String, Object> findOne(@PathVariable String id) {
        return service.findOneView(id);
    }

    @PatchMapping("/{id}")
    @ResponseMessage("Update a User")
    public Object update(@PathVariable String id, @RequestBody UserRequests.UpdateRequest request) {
        return service.update(id, request, SecurityUtils.currentUser());
    }

    @DeleteMapping("/{id}")
    @ResponseMessage("Delete a User")
    public Map<String, Object> remove(@PathVariable String id) {
        return service.remove(id, SecurityUtils.currentUser());
    }
}

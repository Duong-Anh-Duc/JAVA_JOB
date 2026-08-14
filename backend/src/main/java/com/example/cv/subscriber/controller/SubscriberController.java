package com.example.cv.subscriber.controller;

import com.example.cv.common.api.ResponseMessage;
import com.example.cv.common.api.SkipPermission;
import com.example.cv.common.security.SecurityUtils;
import com.example.cv.subscriber.dto.SubscriberRequests;
import com.example.cv.subscriber.service.SubscriberService;
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
@RequestMapping("/api/v1/subscribers")
public class SubscriberController {
    private final SubscriberService service;

    public SubscriberController(SubscriberService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseMessage("Tạo mới người đăng ký thành công")
    public Map<String, Object> create(@Valid @RequestBody SubscriberRequests.CreateRequest request) {
        return service.create(request, SecurityUtils.currentUser());
    }

    @GetMapping
    @ResponseMessage("Lấy danh sách người đăng ký thành công")
    public Map<String, Object> findAll(@RequestParam(required = false, defaultValue = "1") Integer current,
                                       @RequestParam(required = false) Integer pageSize) {
        return service.findAll(current, pageSize);
    }

    @PostMapping("/skills")
    @SkipPermission
    @ResponseMessage("Lấy danh sách skills của subscribers")
    public Map<String, Object> skills() {
        return service.skills(SecurityUtils.currentUser());
    }

    @GetMapping("/{id}")
    @ResponseMessage("Lấy thông tin người đăng ký thành công")
    public Object findOne(@PathVariable String id) {
        return service.findOne(id);
    }

    @PatchMapping("/{id}")
    @ResponseMessage("Cập nhật người đăng ký thành công")
    public Object update(@PathVariable String id, @RequestBody SubscriberRequests.UpdateRequest request) {
        return service.update(id, request, SecurityUtils.currentUser());
    }

    @PatchMapping
    @SkipPermission
    @ResponseMessage("Cập nhật hoặc tạo mới subscriber theo email")
    public Object upsert(@Valid @RequestBody SubscriberRequests.UpsertRequest request) {
        return service.upsert(request, SecurityUtils.currentUser());
    }

    @DeleteMapping("/{id}")
    @ResponseMessage("Xóa người đăng ký thành công")
    public Map<String, Object> remove(@PathVariable String id) {
        return service.remove(id, SecurityUtils.currentUser());
    }
}

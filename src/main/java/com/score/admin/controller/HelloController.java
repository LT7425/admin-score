package com.score.admin.controller;

import com.score.admin.common.ApiResponse;
import com.score.admin.common.BusinessException;
import com.score.admin.dto.GreetingRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hello")
public class HelloController {

    @GetMapping
    public ApiResponse<String> hello() {
        return ApiResponse.ok("Hello, Score Admin");
    }

    @PostMapping
    public ApiResponse<String> greet(@Valid @RequestBody GreetingRequest request) {
        if ("error".equalsIgnoreCase(request.getName())) {
            throw new BusinessException(1001, "名称不能为 error");
        }
        return ApiResponse.ok("你好, " + request.getName());
    }
}


package com.example.VideoService.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import com.example.VideoService.dto.UserDto;

@FeignClient(name = "auth-service", url = "http://localhost:8080")
public interface AuthClient {
    @GetMapping("/user/me")
    UserDto getCurrentUser(@RequestHeader("Authorization") String token);
}

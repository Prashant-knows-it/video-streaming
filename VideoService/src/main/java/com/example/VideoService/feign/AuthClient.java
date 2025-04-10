package com.example.VideoService.feign;

import com.example.VideoService.dto.AuthenticatedUserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "auth-service", url = "http://localhost:8080")
public interface AuthClient {
    @GetMapping("/user/me")
    AuthenticatedUserDto getCurrentUser(@RequestHeader("Authorization") String token);
}

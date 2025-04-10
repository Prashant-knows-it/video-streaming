package com.example.JWT_Learn.controller;

import com.example.JWT_Learn.dto.AuthenticatedUserDto;
import com.example.JWT_Learn.model.Human;
import com.example.JWT_Learn.repository.HumanRepository;
import com.example.JWT_Learn.service.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Me {

    private final HumanRepository humanRepository;

    public Me(HumanRepository humanRepository) {
        this.humanRepository = humanRepository;
    }

    @GetMapping("/user/me")
    public ResponseEntity<AuthenticatedUserDto> getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        Human user = humanRepository.findByUsername(username).orElseThrow();
        AuthenticatedUserDto dto = new AuthenticatedUserDto(user.getId(), user.getUsername(), user.getEmail(), user.getRole());
        return ResponseEntity.ok(dto);
    }


}

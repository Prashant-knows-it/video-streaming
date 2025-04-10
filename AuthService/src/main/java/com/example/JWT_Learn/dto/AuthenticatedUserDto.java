package com.example.JWT_Learn.dto;

public record AuthenticatedUserDto(Long id, String username, String email, String role) {}

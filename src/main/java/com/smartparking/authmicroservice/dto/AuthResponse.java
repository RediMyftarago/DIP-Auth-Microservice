package com.smartparking.authmicroservice.dto;

public record AuthResponse(
        String token,
        String role,
        String email
) {}

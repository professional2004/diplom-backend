package com.textilecad.dto.auth;

public record ResetPasswordRequestDTO(String token, String newPassword) {}

package com.textilecad.dto;

public record ResetPasswordRequest(String token, String newPassword) {
  
}

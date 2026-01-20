package com.textilecad.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.textilecad.dto.ForgotPasswordRequest;
import com.textilecad.dto.ResetPasswordRequest;
import com.textilecad.services.MailService;
import com.textilecad.services.PasswordResetService;


@RestController
@RequestMapping("/api/auth")
public class PasswordResetController {
  private final PasswordResetService passwordResetService;
  private final MailService mailService;
  private final String frontendUrl;

    public PasswordResetController(PasswordResetService passwordResetService, 
                                   MailService mailService, 
                                   @Value("${app.frontend.url}") String frontendUrl
  ) {
    this.passwordResetService = passwordResetService;
    this.mailService = mailService;
    this.frontendUrl = frontendUrl;
  }

  @PostMapping("/forgot-password")
  public ResponseEntity<?> forgot(@RequestBody ForgotPasswordRequest req) {
    passwordResetService.createPasswordResetToken(req.email()).ifPresent(token -> {
      String link = frontendUrl + "/reset-password?token=" + token;
      mailService.sendPasswordResetLink(req.email(), link);
    });
    return ResponseEntity.ok(
    Map.of("message", "If email exists, link sent")
    );
  }


  @PostMapping("/reset-password")
  public ResponseEntity<?> reset(@RequestBody ResetPasswordRequest req) {
    return passwordResetService.validatePasswordResetToken(req.token())
    .map(user -> {
      passwordResetService.resetPassword(user, req.newPassword());
      return ResponseEntity.ok(
        Map.of("message", "Password updated")
      );
    })
    .orElseGet(() ->
      ResponseEntity.badRequest()
        .body(Map.of("error", "Invalid or expired token"))
    );
  }
}

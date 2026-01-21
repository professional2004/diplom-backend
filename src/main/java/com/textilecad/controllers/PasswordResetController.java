package com.textilecad.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.textilecad.dto.MessageResponseDTO;
import com.textilecad.dto.auth.ForgotPasswordRequestDTO;
import com.textilecad.dto.auth.ResetPasswordRequestDTO;
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
  public ResponseEntity<MessageResponseDTO> forgot(@RequestBody ForgotPasswordRequestDTO req) {
    passwordResetService.createPasswordResetToken(req.email()).ifPresent(token -> {
      String link = frontendUrl + "/reset-password?token=" + token;
      mailService.sendPasswordResetLink(req.email(), link);
    });

    MessageResponseDTO response = new MessageResponseDTO("Если почта существует, ссылка отправлена");
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }


  @PostMapping("/reset-password")
  public ResponseEntity<MessageResponseDTO> reset(@RequestBody ResetPasswordRequestDTO req) {
    var userOptional = passwordResetService.validatePasswordResetToken(req.token());
    if (userOptional.isEmpty()) {
      MessageResponseDTO response = new MessageResponseDTO("Невалидный или истекший токен");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    var user = userOptional.get();
    passwordResetService.resetPassword(user, req.newPassword());
    MessageResponseDTO response = new MessageResponseDTO("Пароль успешно обновлен");
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}

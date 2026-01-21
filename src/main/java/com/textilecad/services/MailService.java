package com.textilecad.services;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailService {
  private final JavaMailSender mailSender;

  public void sendPasswordResetLink(String email, String link) {
    SimpleMailMessage msg = new SimpleMailMessage();
    msg.setTo(email);
    msg.setSubject("Восстановление пароля");
    msg.setText("""
        Для восстановления пароля перейдите по ссылке:
        %s

        Если вы не запрашивали восстановление — просто проигнорируйте письмо.
        """.formatted(link));

    mailSender.send(msg);
  }
}

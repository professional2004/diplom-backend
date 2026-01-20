package com.textilecad.services;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.apache.commons.codec.digest.DigestUtils;

import com.textilecad.models.User;
import com.textilecad.repositories.UserRepository;

@Service
public class PasswordResetService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final long passwordResetTokenLifetime;

  public PasswordResetService(UserRepository userRepository, 
                              PasswordEncoder passwordEncoder, 
                              @Value("${app.password-reset.token-lifetime-minutes}") Long passwordResetTokenLifetime
  ) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.passwordResetTokenLifetime = passwordResetTokenLifetime;
  }

  public Optional<String> createPasswordResetToken(String email) {
    return userRepository.findByEmail(email).map(user -> {
      String token = UUID.randomUUID().toString();
      user.setPasswordResetTokenHash(hash(token));
      user.setPasswordResetTokenExpiresAt(LocalDateTime.now().plusMinutes(passwordResetTokenLifetime));
      userRepository.save(user);
      return token;
    });
  }


  public Optional<User> validatePasswordResetToken(String token) {
    String hashed = hash(token);
    return userRepository.findByPasswordResetTokenHash(hashed)
      .filter(u -> u.getPasswordResetTokenExpiresAt() != null)
      .filter(u -> u.getPasswordResetTokenExpiresAt().isAfter(LocalDateTime.now()));
  }

  
  public void resetPassword(User user, String newPassword) {
    user.setPassword(passwordEncoder.encode(newPassword));
    user.setPasswordResetTokenHash(null);
    user.setPasswordResetTokenExpiresAt(null);
    userRepository.save(user);
  }

  private String hash(String token) {
    return DigestUtils.sha256Hex(token);
  }
}

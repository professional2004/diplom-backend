package com.textilecad.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.textilecad.models.User;
import com.textilecad.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  
  public User addUser(User user) {
    return userRepository.save(user);
  }

  public User findByEmail(String email) {
    return userRepository.findByEmail(email)
      .orElseThrow(() -> new RuntimeException("Пользователь с email " + email + " не найден"));
  }

  @Transactional
  public void deleteByEmail(String email) {
    userRepository.deleteByEmail(email);
  }
}

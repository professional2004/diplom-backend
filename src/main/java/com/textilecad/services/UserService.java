package com.textilecad.services;

import org.springframework.stereotype.Service;

import com.textilecad.models.User;
import com.textilecad.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  
  public void addUser(User user) {
    userRepository.save(user);
  }
}

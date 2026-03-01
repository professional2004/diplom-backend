package com.textilecad.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.textilecad.models.User;
import com.textilecad.repositories.UserRepository;
import com.textilecad.repositories.ProjectRepository;
import com.textilecad.repositories.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final CategoryService categoryService;
  private final ProjectRepository projectRepository;
  private final CategoryRepository categoryRepository;
  
  public User addUser(User user) {
    User savedUser = userRepository.save(user);
    categoryService.initializeDefaultCategory(savedUser);
    return savedUser;
  }

  public User findByEmail(String email) {
    return userRepository.findByEmail(email)
      .orElseThrow(() -> new RuntimeException("Пользователь с email " + email + " не найден"));
  }

  @Transactional
  public void deleteByEmail(String email) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("Пользователь с email " + email + " не найден"));
    // Удаляем все связанные проекты и категории, затем пользователя
    projectRepository.deleteByUser(user);
    categoryRepository.deleteByUser(user);
    userRepository.delete(user);
  }
}

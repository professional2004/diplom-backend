package com.textilecad.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.textilecad.dto.project.CategoryDTO;
import com.textilecad.models.Category;
import com.textilecad.models.User;
import com.textilecad.repositories.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {
  private final CategoryRepository categoryRepository;
  private static final String DEFAULT_CATEGORY_NAME = "Проект";

  public void initializeDefaultCategory(User user) {
    if (!categoryRepository.existsByNameAndUser(DEFAULT_CATEGORY_NAME, user)) {
      Category defaultCategory = Category.builder()
          .name(DEFAULT_CATEGORY_NAME)
          .user(user)
          .build();
      categoryRepository.save(defaultCategory);
    }
  }

  public List<CategoryDTO> getUserCategories(User user) {
    return categoryRepository.findByUser(user).stream()
        .map(category -> new CategoryDTO(category.getId(), category.getName()))
        .collect(Collectors.toList());
  }

  public Category getCategoryById(Long categoryId, User user) {
    return categoryRepository.findByIdAndUser(categoryId, user)
        .orElseThrow(() -> new RuntimeException("Категория не найдена"));
  }

  public Category getDefaultCategory(User user) {
    return categoryRepository.findByNameAndUser(DEFAULT_CATEGORY_NAME, user)
        .orElseThrow(() -> new RuntimeException("Категория по умолчанию не найдена"));
  }

  @Transactional
  public Category createCategory(String name, User user) {
    if (categoryRepository.existsByNameAndUser(name, user)) {
      throw new RuntimeException("Категория с таким названием уже существует");
    }
    Category category = Category.builder()
        .name(name)
        .user(user)
        .build();
    return categoryRepository.save(category);
  }

  @Transactional
  public void deleteCategory(Long categoryId, User user) {
    Category category = getCategoryById(categoryId, user);
    if (DEFAULT_CATEGORY_NAME.equals(category.getName())) {
      throw new RuntimeException("Нельзя удалить категорию по умолчанию");
    }
    categoryRepository.delete(category);
  }

  @Transactional
  public Category renameCategory(Long categoryId, String newName, User user) {
    Category category = getCategoryById(categoryId, user);
    if (DEFAULT_CATEGORY_NAME.equals(category.getName())) {
      throw new RuntimeException("Нельзя переименовать категорию по умолчанию");
    }
    if (categoryRepository.existsByNameAndUser(newName, user)) {
      throw new RuntimeException("Категория с таким названием уже существует");
    }
    category.setName(newName);
    return categoryRepository.save(category);
  }
}

package com.textilecad.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.textilecad.dto.MessageResponseDTO;
import com.textilecad.dto.project.CreateCategoryRequestDTO;
import com.textilecad.dto.project.CategoryDTO;
import com.textilecad.models.User;
import com.textilecad.services.CategoryService;
import com.textilecad.services.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
  private final CategoryService categoryService;
  private final UserService userService;

  @PostMapping
  public ResponseEntity<?> createCategory(
      @RequestBody CreateCategoryRequestDTO request,
      @AuthenticationPrincipal UserDetails userDetails) {
    try {
      User user = userService.findByEmail(userDetails.getUsername());
      var category = categoryService.createCategory(request.name(), user);
      var dto = new CategoryDTO(category.getId(), category.getName());
      return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(new MessageResponseDTO(e.getMessage()));
    }
  }

  @DeleteMapping("/{categoryId}")
  public ResponseEntity<?> deleteCategory(
      @PathVariable Long categoryId,
      @AuthenticationPrincipal UserDetails userDetails) {
    try {
      User user = userService.findByEmail(userDetails.getUsername());
      categoryService.deleteCategory(categoryId, user);
      return ResponseEntity.ok(new MessageResponseDTO("Категория успешно удалена"));
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(new MessageResponseDTO(e.getMessage()));
    }
  }
}

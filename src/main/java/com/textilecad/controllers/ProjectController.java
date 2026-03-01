package com.textilecad.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.textilecad.dto.MessageResponseDTO;
import com.textilecad.dto.project.CreateProjectRequestDTO;
import com.textilecad.dto.project.ProjectDetailDTO;
import com.textilecad.dto.project.ProjectDTO;
import com.textilecad.dto.project.ProjectsAndCategoriesDTO;
import com.textilecad.dto.project.SaveProjectChangesRequestDTO;
import com.textilecad.dto.project.UpdateProjectCategoryRequestDTO;
import com.textilecad.dto.project.UpdateProjectNameRequestDTO;
import com.textilecad.models.User;
import com.textilecad.services.ProjectService;
import com.textilecad.services.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {
  private final ProjectService projectService;
  private final UserService userService;

  // Получить все категории и проекты пользователя
  @GetMapping
  public ResponseEntity<ProjectsAndCategoriesDTO> getProjectsAndCategories(
      @AuthenticationPrincipal UserDetails userDetails) {
    User user = userService.findByEmail(userDetails.getUsername());
    ProjectsAndCategoriesDTO response = projectService.getUserProjectsAndCategories(user);
    return ResponseEntity.ok(response);
  }

  // Создать проект
  @PostMapping
  public ResponseEntity<?> createProject(
      @RequestBody CreateProjectRequestDTO request,
      @AuthenticationPrincipal UserDetails userDetails) {
    try {
      User user = userService.findByEmail(userDetails.getUsername());
      ProjectDetailDTO project = projectService.createProject(
          request.name(),
          request.description(),
          user
      );
      return ResponseEntity.status(HttpStatus.CREATED).body(project);
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(new MessageResponseDTO(e.getMessage()));
    }
  }

  //  Удалить проект
  @DeleteMapping("/{projectId}")
  public ResponseEntity<?> deleteProject(
      @PathVariable Long projectId,
      @AuthenticationPrincipal UserDetails userDetails) {
    try {
      User user = userService.findByEmail(userDetails.getUsername());
      projectService.deleteProject(projectId, user);
      return ResponseEntity.ok(new MessageResponseDTO("Проект успешно удален"));
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new MessageResponseDTO(e.getMessage()));
    }
  }

  // Дублировать проект
  @PostMapping("/{projectId}/duplicate")
  public ResponseEntity<?> duplicateProject(
      @PathVariable Long projectId,
      @AuthenticationPrincipal UserDetails userDetails) {
    try {
      User user = userService.findByEmail(userDetails.getUsername());
      ProjectDetailDTO duplicatedProject = projectService.duplicateProject(projectId, user);
      return ResponseEntity.status(HttpStatus.CREATED).body(duplicatedProject);
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new MessageResponseDTO(e.getMessage()));
    }
  }

  // Получить проект
  @GetMapping("/{projectId}")
  public ResponseEntity<?> getProject(
      @PathVariable Long projectId,
      @AuthenticationPrincipal UserDetails userDetails) {
    try {
      User user = userService.findByEmail(userDetails.getUsername());
      ProjectDetailDTO project = projectService.getProject(projectId, user);
      return ResponseEntity.ok(project);
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new MessageResponseDTO(e.getMessage()));
    }
  }

  // Переименовать проект
  @PutMapping("/{projectId}/rename")
  public ResponseEntity<?> renameProject(
      @PathVariable Long projectId,
      @RequestBody UpdateProjectNameRequestDTO request,
      @AuthenticationPrincipal UserDetails userDetails) {
    try {
      User user = userService.findByEmail(userDetails.getUsername());
      ProjectDTO updatedProject = projectService.renameProject(projectId, request.name(), user);
      return ResponseEntity.ok(updatedProject);
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(new MessageResponseDTO(e.getMessage()));
    }
  }

  // Изменить категорию проекта
  @PutMapping("/{projectId}/category")
  public ResponseEntity<?> changeProjectCategory(
      @PathVariable Long projectId,
      @RequestBody UpdateProjectCategoryRequestDTO request,
      @AuthenticationPrincipal UserDetails userDetails) {
    try {
      User user = userService.findByEmail(userDetails.getUsername());
      ProjectDTO updatedProject = projectService.changeProjectCategory(
          projectId,
          request.categoryId(),
          user
      );
      return ResponseEntity.ok(updatedProject);
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(new MessageResponseDTO(e.getMessage()));
    }
  }

  // Сохранить изменения в проекте
  @PutMapping("/{projectId}/save")
  public ResponseEntity<?> saveProjectChanges(
      @PathVariable Long projectId,
      @RequestBody SaveProjectChangesRequestDTO request,
      @AuthenticationPrincipal UserDetails userDetails) {
    try {
      User user = userService.findByEmail(userDetails.getUsername());
      ProjectDetailDTO updatedProject = projectService.saveProjectChanges(
          projectId,
          request.projectData(),
          request.preview(),
          user
      );
      return ResponseEntity.ok(updatedProject);
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new MessageResponseDTO(e.getMessage()));
    }
  }
}

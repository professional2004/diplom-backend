package com.textilecad.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.textilecad.dto.project.ProjectDTO;
import com.textilecad.dto.project.ProjectDetailDTO;
import com.textilecad.dto.project.ProjectsAndCategoriesDTO;
import com.textilecad.dto.project.CategoryDTO;
import com.textilecad.models.Category;
import com.textilecad.models.Project;
import com.textilecad.models.User;
import com.textilecad.repositories.ProjectRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectService {
  private final ProjectRepository projectRepository;
  private final CategoryService categoryService;

  public ProjectsAndCategoriesDTO getUserProjectsAndCategories(User user) {
    List<CategoryDTO> categories = categoryService.getUserCategories(user);
    List<ProjectDTO> projects = projectRepository.findByUser(user).stream()
        .map(this::projectToDTO)
        .collect(Collectors.toList());
    return new ProjectsAndCategoriesDTO(categories, projects);
  }

  @Transactional
  public ProjectDetailDTO createProject(String name, String description, User user) {
    if (projectRepository.existsByNameAndUser(name, user)) {
      throw new RuntimeException("Проект с таким названием уже существует");
    }
    Category category = categoryService.getDefaultCategory(user);
    LocalDateTime now = LocalDateTime.now();
    
    Project project = Project.builder()
        .name(name)
        .description(description)
        .user(user)
        .category(category)
        .createdAt(now)
        .updatedAt(now)
        .projectData("{}")
        .build();
    
    Project savedProject = projectRepository.save(project);
    return projectToDetailDTO(savedProject);
  }

  @Transactional
  public void deleteProject(Long projectId, User user) {
    Project project = getProjectByIdAndUser(projectId, user);
    projectRepository.delete(project);
  }

  @Transactional
  public ProjectDetailDTO duplicateProject(Long projectId, User user) {
    Project originalProject = getProjectByIdAndUser(projectId, user);
    String newName = originalProject.getName() + " copy";
    
    int counter = 1;
    while (projectRepository.existsByNameAndUser(newName, user)) {
      newName = originalProject.getName() + " copy " + counter;
      counter++;
    }
    
    LocalDateTime now = LocalDateTime.now();
    Project duplicatedProject = Project.builder()
        .name(newName)
        .description(originalProject.getDescription())
        .user(user)
        .category(originalProject.getCategory())
        .createdAt(now)
        .updatedAt(now)
        .projectData(originalProject.getProjectData())
        .preview(originalProject.getPreview())
        .build();
    
    Project savedProject = projectRepository.save(duplicatedProject);
    return projectToDetailDTO(savedProject);
  }

  public ProjectDetailDTO getProject(Long projectId, User user) {
    Project project = getProjectByIdAndUser(projectId, user);
    return projectToDetailDTO(project);
  }

  @Transactional
  public ProjectDTO renameProject(Long projectId, String newName, User user) {
    Project project = getProjectByIdAndUser(projectId, user);
    if (projectRepository.existsByNameAndUser(newName, user) && !project.getName().equals(newName)) {
      throw new RuntimeException("Проект с таким названием уже существует");
    }
    project.setName(newName);
    project.setUpdatedAt(LocalDateTime.now());
    Project updatedProject = projectRepository.save(project);
    return projectToDTO(updatedProject);
  }

  @Transactional
  public ProjectDTO changeProjectCategory(Long projectId, Long categoryId, User user) {
    Project project = getProjectByIdAndUser(projectId, user);
    Category category = categoryService.getCategoryById(categoryId, user);
    project.setCategory(category);
    project.setUpdatedAt(LocalDateTime.now());
    Project updatedProject = projectRepository.save(project);
    return projectToDTO(updatedProject);
  }

  @Transactional
  public ProjectDetailDTO saveProjectChanges(Long projectId, String projectData, byte[] preview, User user) {
    Project project = getProjectByIdAndUser(projectId, user);
    project.setProjectData(projectData);
    project.setPreview(preview);
    project.setUpdatedAt(LocalDateTime.now());
    Project updatedProject = projectRepository.save(project);
    return projectToDetailDTO(updatedProject);
  }

  private Project getProjectByIdAndUser(Long projectId, User user) {
    return projectRepository.findByIdAndUser(projectId, user)
        .orElseThrow(() -> new RuntimeException("Проект не найден"));
  }

  private ProjectDTO projectToDTO(Project project) {
    return new ProjectDTO(
        project.getId(),
        project.getName(),
        project.getDescription(),
        project.getCreatedAt(),
        project.getUpdatedAt(),
        project.getCategory().getId(),
        project.getCategory().getName(),
        project.getPreview()
    );
  }

  private ProjectDetailDTO projectToDetailDTO(Project project) {
    return new ProjectDetailDTO(
        project.getId(),
        project.getName(),
        project.getDescription(),
        project.getCreatedAt(),
        project.getUpdatedAt(),
        project.getCategory().getId(),
        project.getCategory().getName(),
        project.getPreview(),
        project.getProjectData()
    );
  }
}

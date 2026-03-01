package com.textilecad.dto.project;

import java.util.List;

public record ProjectsAndCategoriesDTO(
    List<CategoryDTO> categories,
    List<ProjectDTO> projects
) {}

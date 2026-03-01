package com.textilecad.dto.project;

import java.time.LocalDateTime;

public record ProjectDTO(
    Long id,
    String name,
    String description,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    Long categoryId,
    String categoryName,
    byte[] preview
) {}

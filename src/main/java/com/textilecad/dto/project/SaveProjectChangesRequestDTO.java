package com.textilecad.dto.project;

public record SaveProjectChangesRequestDTO(
    String projectData,
    byte[] preview
) {}

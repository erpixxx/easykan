package dev.erpix.easykan.server.domain.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProjectCreateDto(@NotBlank @Size(min = 1, max = 255) String name) {
}

package dev.erpix.easykan.server.domain.board.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BoardCreateDto(@NotBlank @Size(min = 1, max = 64) String name) {
}

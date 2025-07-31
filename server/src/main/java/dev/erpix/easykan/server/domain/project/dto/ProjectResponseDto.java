package dev.erpix.easykan.server.domain.project.dto;

import dev.erpix.easykan.server.domain.project.model.EKProject;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record ProjectResponseDto(
        UUID id,
        String name,
        UUID owner
) {

    public static @NotNull ProjectResponseDto fromProject(@NotNull EKProject project) {
        return new ProjectResponseDto(
                project.getId(),
                project.getName(),
                project.getOwner().getId()
        );
    }

}

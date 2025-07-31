package dev.erpix.easykan.server.domain.project.dto;

import dev.erpix.easykan.server.domain.project.model.EKProject;
import dev.erpix.easykan.server.domain.user.model.EKUser;
import jakarta.validation.constraints.Size;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ProjectCreateRequestDto(
        @Size(min = 3, max = 255) String name
) {

    public @NotNull EKProject toProject(@Nullable EKUser owner) {
        return EKProject.builder()
                .name(name)
                .owner(owner)
                .build();
    }

}

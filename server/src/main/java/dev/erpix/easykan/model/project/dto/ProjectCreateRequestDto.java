package dev.erpix.easykan.model.project.dto;

import dev.erpix.easykan.model.project.EKProject;
import dev.erpix.easykan.model.user.EKUser;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@ToString
@RequiredArgsConstructor
public class ProjectCreateRequestDto {

    private final String name;

    public @NotNull EKProject toProject(@Nullable EKUser owner) {
        return EKProject.builder()
                .name(name)
                .owner(owner)
                .build();
    }

}

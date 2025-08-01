package dev.erpix.easykan.server.domain.user.dto;

import dev.erpix.easykan.server.domain.user.model.EKUser;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record UserResponseDto(
        UUID id,
        String login,
        String displayName,
        long permissions
) {

    public static UserResponseDto fromUser(@NotNull @NonNull EKUser user) {
        return new UserResponseDto(
                user.getId(),
                user.getLogin(),
                user.getDisplayName(),
                user.getPermissions()
        );
    }

}

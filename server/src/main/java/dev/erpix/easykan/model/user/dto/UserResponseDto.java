package dev.erpix.easykan.model.user.dto;

import dev.erpix.easykan.model.user.EKUser;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record UserResponseDto(
        UUID id,
        String login,
        String displayName,
        boolean isAdmin
) {

    public static UserResponseDto fromUser(@NotNull @NonNull EKUser user) {
        return new UserResponseDto(
                user.getId(),
                user.getLogin(),
                user.getDisplayName(),
                user.isAdmin()
        );
    }

}

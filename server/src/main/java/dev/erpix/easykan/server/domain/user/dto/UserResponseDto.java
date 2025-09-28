package dev.erpix.easykan.server.domain.user.dto;

import dev.erpix.easykan.server.domain.user.model.User;
import java.util.UUID;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

public record UserResponseDto(UUID id, String login, String displayName, long permissions) {

	public static UserResponseDto fromUser(@NotNull @NonNull User user) {
		return new UserResponseDto(user.getId(), user.getLogin(), user.getDisplayName(), user.getPermissions());
	}
}

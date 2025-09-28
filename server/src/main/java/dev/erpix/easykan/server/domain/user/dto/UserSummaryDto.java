package dev.erpix.easykan.server.domain.user.dto;

import dev.erpix.easykan.server.domain.user.model.User;

import java.util.UUID;

public record UserSummaryDto(UUID id, String login, String displayName) {

	public static UserSummaryDto fromUser(User user) {
		return new UserSummaryDto(user.getId(), user.getLogin(), user.getDisplayName());
	}

}

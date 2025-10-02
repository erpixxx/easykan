package dev.erpix.easykan.server.domain.user.dto;

import dev.erpix.easykan.server.domain.user.constraint.annotation.DisplayName;
import dev.erpix.easykan.server.domain.user.constraint.annotation.Login;
import dev.erpix.easykan.server.domain.user.constraint.annotation.Password;
import dev.erpix.easykan.server.domain.user.model.User;
import jakarta.validation.constraints.Email;

public record UserCreateRequestDto(@Login String login, @DisplayName String displayName, @Email String email,
		@Password String password) {

	public User toUser() {
		return User.builder().login(login).displayName(displayName).email(email).passwordHash(password).build();
	}
}

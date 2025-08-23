package dev.erpix.easykan.server.domain.user.dto;

import dev.erpix.easykan.server.domain.user.model.User;
import dev.erpix.easykan.server.domain.user.validator.DisplayName;
import dev.erpix.easykan.server.domain.user.validator.Login;
import dev.erpix.easykan.server.domain.user.validator.Password;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.jetbrains.annotations.NotNull;

public record UserCreateRequestDto(
        @Login
        String login,

        @DisplayName
        String displayName,

        @Email
        String email,

        @Password
        String password
) {

    public @NotNull User toUser() {
        return User.builder()
                .login(login)
                .displayName(displayName)
                .email(email)
                .passwordHash(password)
                .build();
    }

}

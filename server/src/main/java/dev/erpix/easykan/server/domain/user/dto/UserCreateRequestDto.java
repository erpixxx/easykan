package dev.erpix.easykan.server.domain.user.dto;

import dev.erpix.easykan.server.domain.user.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.jetbrains.annotations.NotNull;

public record UserCreateRequestDto(
        @NotBlank(message = "Login cannot be blank")
        @Size(min = 1, max = 64, message = "Login must be between 1 and 64 characters")
        String login,

        @NotBlank(message = "Display name cannot be blank")
        @Size(min = 1, max = 64, message = "Display name must be between 1 and 64 characters")
        String displayName,

        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Email must be valid")
        String email,

        @NotBlank(message = "Password cannot be blank")
        @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters")
        String password,

        boolean canAuthWithPassword
) {

    public @NotNull User toUser() {
        return User.builder()
                .login(login)
                .displayName(displayName)
                .email(email)
                .canAuthWithPassword(canAuthWithPassword)
                .passwordHash(password)
                .build();
    }

}

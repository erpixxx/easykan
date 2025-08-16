package dev.erpix.easykan.server.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record CurrentUserUpdateRequestDto(
        @Size(max = 64, message = "Login must be at most 64 characters")
        String login,

        @Size(max = 64, message = "Display name must be at most 64 characters")
        String displayName,

        @Email(message = "Email must be valid")
        @Size(max = 64, message = "Display name must be at most 64 characters")
        String email
) { }

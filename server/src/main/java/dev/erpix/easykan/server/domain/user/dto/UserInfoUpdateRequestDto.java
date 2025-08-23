package dev.erpix.easykan.server.domain.user.dto;

import dev.erpix.easykan.server.domain.user.constraint.annotation.DisplayName;
import dev.erpix.easykan.server.domain.user.constraint.annotation.Login;
import dev.erpix.easykan.server.domain.user.constraint.annotation.OptionalEmail;

import java.util.Optional;

public record UserInfoUpdateRequestDto(
        @Login
        Optional<String> login,

        @DisplayName
        Optional<String> displayName,

        @OptionalEmail
        Optional<String> email
) { }

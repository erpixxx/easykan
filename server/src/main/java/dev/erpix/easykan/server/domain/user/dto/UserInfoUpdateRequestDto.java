package dev.erpix.easykan.server.domain.user.dto;

import dev.erpix.easykan.server.domain.user.validator.DisplayName;
import dev.erpix.easykan.server.domain.user.validator.Login;
import dev.erpix.easykan.server.domain.user.validator.OptionalEmail;

import java.util.Optional;

public record UserInfoUpdateRequestDto(
        @Login
        Optional<String> login,

        @DisplayName
        Optional<String> displayName,

        @OptionalEmail
        Optional<String> email
) { }

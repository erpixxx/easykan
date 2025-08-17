package dev.erpix.easykan.server.domain.user.dto;

import java.util.Optional;

public record CurrentUserUpdateRequestDto(
        Optional<String> login,
        Optional<String> displayName,
        Optional<String> email
) { }

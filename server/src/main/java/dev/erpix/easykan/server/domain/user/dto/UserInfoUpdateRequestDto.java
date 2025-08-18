package dev.erpix.easykan.server.domain.user.dto;

import java.util.Optional;

public record UserInfoUpdateRequestDto(
        Optional<String> login,
        Optional<String> displayName,
        Optional<String> email
) { }

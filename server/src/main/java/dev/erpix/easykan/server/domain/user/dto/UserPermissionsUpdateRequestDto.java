package dev.erpix.easykan.server.domain.user.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UserPermissionsUpdateRequestDto(
        @NotNull(message = "Permissions value cannot be null")
        @Min(value = 0, message = "Permissions value cannot be negative")
        Long permissions
) { }

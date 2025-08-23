package dev.erpix.easykan.server.domain.user.dto;

import dev.erpix.easykan.server.domain.user.constraint.annotation.UserPermissionMask;

public record UserPermissionsUpdateRequestDto(
        @UserPermissionMask
        Long permissions
) { }

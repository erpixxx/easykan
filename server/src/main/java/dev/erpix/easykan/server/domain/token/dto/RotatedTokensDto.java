package dev.erpix.easykan.server.domain.token.dto;

import java.time.LocalDateTime;

public record RotatedTokensDto(
        String newAccessToken,
        String newRawRefreshToken,
        LocalDateTime newRefreshTokenExpiration
) { }

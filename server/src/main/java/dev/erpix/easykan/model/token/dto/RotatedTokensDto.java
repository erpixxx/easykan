package dev.erpix.easykan.model.token.dto;

import java.time.LocalDateTime;

public record RotatedTokensDto(
        String newAccessToken,
        String newRawRefreshToken,
        LocalDateTime newRefreshTokenExpiration
) { }

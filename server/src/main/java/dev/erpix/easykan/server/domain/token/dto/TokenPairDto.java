package dev.erpix.easykan.server.domain.token.dto;

import java.time.Duration;

public record TokenPairDto(
        String newAccessToken,
        Duration newAccessTokenDuration,
        String newRawRefreshToken,
        Duration newRefreshTokenDuration
) { }

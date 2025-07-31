package dev.erpix.easykan.server.domain.token.dto;

import java.time.LocalDateTime;

public record CreateRefreshTokenDto(String rawToken, LocalDateTime expiresAt) { }

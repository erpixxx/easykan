package dev.erpix.easykan.model.token.dto;

import java.time.LocalDateTime;

public record CreateRefreshTokenDto(String rawToken, LocalDateTime expiresAt) { }

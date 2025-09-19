package dev.erpix.easykan.server.domain.token.dto;

import java.time.Duration;

public record CreateTokenDto(String rawToken,Duration duration){}

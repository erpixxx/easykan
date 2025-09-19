package dev.erpix.easykan.server.domain.token;

import java.time.Duration;

public record AccessToken(String rawToken,Duration duration){}

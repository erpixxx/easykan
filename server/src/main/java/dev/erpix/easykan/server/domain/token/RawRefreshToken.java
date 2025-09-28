package dev.erpix.easykan.server.domain.token;

import dev.erpix.easykan.server.domain.token.security.TokenParts;
import java.time.Duration;

public record RawRefreshToken(TokenParts parts, Duration duration) {

	public String combine() {
		return parts().combine();
	}
}

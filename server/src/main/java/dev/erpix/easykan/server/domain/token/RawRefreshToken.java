package dev.erpix.easykan.server.domain.token;

import dev.erpix.easykan.server.domain.token.security.RefreshTokenParts;
import java.time.Duration;

public record RawRefreshToken(RefreshTokenParts parts, Duration duration) {

	public String combine() {
		return parts().combine();
	}
}

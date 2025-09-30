package dev.erpix.easykan.server.domain.token.security;

public record RefreshTokenParts(String selector, String validator) {

	public String combine() {
		return selector + ':' + validator;
	}
}

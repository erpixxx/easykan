package dev.erpix.easykan.server.exception.auth;

import org.springframework.security.core.AuthenticationException;

public class UnsupportedAuthenticationMethodException extends AuthenticationException {

	public UnsupportedAuthenticationMethodException(String message) {
		super(message);
	}

}

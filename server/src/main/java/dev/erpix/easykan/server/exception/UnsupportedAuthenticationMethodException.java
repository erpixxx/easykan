package dev.erpix.easykan.server.exception;

import org.springframework.security.core.AuthenticationException;

public class UnsupportedAuthenticationMethodException extends AuthenticationException {

    public UnsupportedAuthenticationMethodException(String message) {
        super(message);
    }

}

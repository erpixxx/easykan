package dev.erpix.easykan.server.exception;

import org.springframework.http.HttpStatus;

public class InvalidTokenException extends RestException {

    private InvalidTokenException(String message, HttpStatus status) {
        super(message, HttpStatus.UNAUTHORIZED);
    }

    public InvalidTokenException() {
        this("Invalid or expired token", HttpStatus.UNAUTHORIZED);
    }

}

package dev.erpix.easykan.server.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends RestException {

    public ResourceNotFoundException() {
        super(HttpStatus.NOT_FOUND);
    }

    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

}

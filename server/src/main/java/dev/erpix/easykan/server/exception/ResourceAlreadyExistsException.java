package dev.erpix.easykan.server.exception;

import org.springframework.http.HttpStatus;

public class ResourceAlreadyExistsException extends RestException {

    public ResourceAlreadyExistsException() {
        super(HttpStatus.CONFLICT);
    }

    public ResourceAlreadyExistsException(String message) {
        super(message, HttpStatus.CONFLICT);
    }

}

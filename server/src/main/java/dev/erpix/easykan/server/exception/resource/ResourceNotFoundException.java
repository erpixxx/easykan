package dev.erpix.easykan.server.exception.resource;

import dev.erpix.easykan.server.exception.common.RestException;
import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends RestException {

    public ResourceNotFoundException() {
        super(HttpStatus.NOT_FOUND);
    }

    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}

package dev.erpix.easykan.server.exception.resource;

import dev.erpix.easykan.server.exception.common.RestException;
import org.springframework.http.HttpStatus;

public class ResourceAlreadyExistsException extends RestException {

	public ResourceAlreadyExistsException() {
		super(HttpStatus.CONFLICT);
	}

	public ResourceAlreadyExistsException(String message) {
		super(message, HttpStatus.CONFLICT);
	}

}

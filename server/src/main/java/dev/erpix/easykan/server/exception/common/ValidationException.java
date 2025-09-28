package dev.erpix.easykan.server.exception.common;

import org.springframework.http.HttpStatus;

public class ValidationException extends RestException {

	public ValidationException() {
		super(HttpStatus.BAD_REQUEST);
	}

	public ValidationException(String message) {
		super(message, HttpStatus.BAD_REQUEST);
	}

}

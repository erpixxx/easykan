package dev.erpix.easykan.server.exception.user;

import dev.erpix.easykan.server.exception.common.ValidationException;

public class PermissionMaskValidationException extends ValidationException {

	public PermissionMaskValidationException(String message) {
		super(message);
	}

}

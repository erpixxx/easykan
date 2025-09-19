package dev.erpix.easykan.server.exception.user;

import dev.erpix.easykan.server.exception.resource.ResourceNotFoundException;
import java.util.UUID;

public class UserNotFoundException extends ResourceNotFoundException {

    private UserNotFoundException(String message) {
        super(message);
    }

    public static UserNotFoundException byId(UUID userId) {
        return new UserNotFoundException("User with ID '" + userId + "' not found");
    }

    public static UserNotFoundException byLogin(String login) {
        return new UserNotFoundException("User with login '" + login + "' not found");
    }

    public static UserNotFoundException byEmail(String email) {
        return new UserNotFoundException("User with email '" + email + "' not found");
    }
}

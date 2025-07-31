package dev.erpix.easykan.server.exception;

import java.util.UUID;

public class ProjectNotFoundException extends ResourceNotFoundException {

    private ProjectNotFoundException(String message) {
        super(message);
    }

    public static ProjectNotFoundException byId(UUID projectId) {
        return new ProjectNotFoundException("Project with ID '" + projectId + "' not found");
    }

}

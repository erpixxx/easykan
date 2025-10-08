package dev.erpix.easykan.server.exception.project;

import dev.erpix.easykan.server.exception.common.RestException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class UserIsNotProjectMember extends RestException {

	private UserIsNotProjectMember(String message) {
		super(message, HttpStatus.FORBIDDEN);
	}

	public static UserIsNotProjectMember byUserIdAndProjectId(UUID userId, UUID projectId) {
		return new UserIsNotProjectMember("User (" + userId + ") is not a member of project (" + projectId + ").");
	}

}

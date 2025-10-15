package dev.erpix.easykan.server.exception.board;

import dev.erpix.easykan.server.exception.resource.ResourceNotFoundException;

import java.util.UUID;

public class BoardNotFoundException extends ResourceNotFoundException {

	private BoardNotFoundException(String message) {
		super(message);
	}

	public static BoardNotFoundException byIdInProject(UUID boardId, UUID projectId) {
		return new BoardNotFoundException("Board " + boardId + " in project " + projectId + " not found.");
	}

}

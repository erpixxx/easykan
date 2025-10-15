package dev.erpix.easykan.server.domain.board.dto;

import dev.erpix.easykan.server.domain.board.model.Board;
import dev.erpix.easykan.server.domain.user.model.User;

import java.time.Instant;
import java.util.UUID;

public record BoardSummaryDto(UUID id, User owner, String name, Instant createdAt) {

	public static BoardSummaryDto fromBoard(Board board) {
		return new BoardSummaryDto(board.getId(), board.getOwner(), board.getName(), board.getCreatedAt());
	}

}

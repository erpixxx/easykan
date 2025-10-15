package dev.erpix.easykan.server.controller;

import dev.erpix.easykan.server.domain.board.dto.BoardCreateDto;
import dev.erpix.easykan.server.domain.board.dto.BoardSummaryDto;
import dev.erpix.easykan.server.domain.board.model.Board;
import dev.erpix.easykan.server.domain.board.service.BoardService;
import dev.erpix.easykan.server.domain.user.security.JpaUserDetails;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/boards")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class BoardController {

	private final BoardService boardService;

	@PostMapping
	public ResponseEntity<BoardSummaryDto> createBoard(@AuthenticationPrincipal JpaUserDetails userDetails,
			@RequestBody @Valid BoardCreateDto boardCreateDto, @RequestParam UUID projectId) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(boardService.createBoard(boardCreateDto, userDetails.user().getId(), projectId));
	}

	@DeleteMapping("/{boardId}")
	public ResponseEntity<Void> deleteBoard(@AuthenticationPrincipal JpaUserDetails userDetails,
			@PathVariable UUID boardId, @RequestParam UUID projectId) {
		boardService.deleteBoard(boardId, userDetails.user().getId(), projectId);
		return ResponseEntity.noContent().build();
	}

	@GetMapping
	public ResponseEntity<List<BoardSummaryDto>> getBoardsByProject(@RequestParam UUID projectId) {
		List<BoardSummaryDto> boards = boardService.getBoardsByProjectId(projectId);
		return ResponseEntity.ok(boards);
	}

	@GetMapping("/{boardId}")
	public ResponseEntity<Board> getBoardById(@PathVariable UUID boardId, @RequestParam UUID projectId) {
		Board board = boardService.getBoardById(boardId, projectId);
		return ResponseEntity.ok(board);
	}

}

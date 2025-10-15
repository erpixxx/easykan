package dev.erpix.easykan.server.domain.board.service;

import dev.erpix.easykan.server.domain.board.dto.BoardCreateDto;
import dev.erpix.easykan.server.domain.board.dto.BoardSummaryDto;
import dev.erpix.easykan.server.domain.board.factory.BoardFactory;
import dev.erpix.easykan.server.domain.board.model.Board;
import dev.erpix.easykan.server.domain.board.repository.BoardRepository;
import dev.erpix.easykan.server.domain.project.model.Project;
import dev.erpix.easykan.server.domain.project.model.ProjectPermission;
import dev.erpix.easykan.server.domain.project.security.ProjectId;
import dev.erpix.easykan.server.domain.project.security.RequireProjectPermission;
import dev.erpix.easykan.server.domain.project.service.ProjectService;
import dev.erpix.easykan.server.domain.user.model.User;
import dev.erpix.easykan.server.domain.user.service.UserService;
import dev.erpix.easykan.server.exception.board.BoardNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BoardService {

	private final BoardRepository boardRepository;

	private final BoardFactory boardFactory;

	private final ProjectService projectService;

	private final UserService userService;

	@Transactional
	@RequireProjectPermission(ProjectPermission.MANAGE_BOARDS)
	public BoardSummaryDto createBoard(BoardCreateDto dto, UUID userId, @ProjectId UUID projectId) {
		User user = userService.getById(userId);
		Project project = projectService.getProjectById(projectId);

		Integer nextPosition = boardRepository.findNextPositionByProjectId(projectId);

		Board board = boardFactory.create(project, user, dto.name(), nextPosition);
		Board savedBoard = boardRepository.save(board);

		return BoardSummaryDto.fromBoard(savedBoard);
	}

	@Transactional
	@RequireProjectPermission(ProjectPermission.MANAGE_BOARDS)
	public void deleteBoard(UUID boardId, @SuppressWarnings("unused") /* to be used */ UUID userId,
			@ProjectId UUID projectId) {
		Board board = boardRepository.findByIdAndProjectId(boardId, projectId)
			.orElseThrow(() -> BoardNotFoundException.byIdInProject(boardId, projectId));

		board.getProject().getBoards().remove(board);

		boardRepository.delete(board);
		boardRepository.decrementPositionsGreaterThan(board.getPosition(), projectId);
	}

	@RequireProjectPermission(value = ProjectPermission.VIEWER,
			message = "You do not have permission to view boards in this project")
	public List<BoardSummaryDto> getBoardsByProjectId(@ProjectId UUID projectId) {
		return boardRepository.findAllByProjectIdOrderByPositionAsc(projectId)
			.stream()
			.map(BoardSummaryDto::fromBoard)
			.toList();
	}

	@RequireProjectPermission(value = ProjectPermission.VIEWER,
			message = "You do not have permission to view this board")
	public Board getBoardById(UUID boardId, @ProjectId UUID projectId) {
		return boardRepository.findByIdAndProjectId(boardId, projectId)
			.orElseThrow(() -> BoardNotFoundException.byIdInProject(boardId, projectId));
	}

}

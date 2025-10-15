package dev.erpix.easykan.server.domain.board.service;

import dev.erpix.easykan.server.domain.board.dto.BoardCreateDto;
import dev.erpix.easykan.server.domain.board.dto.BoardSummaryDto;
import dev.erpix.easykan.server.domain.board.factory.BoardFactory;
import dev.erpix.easykan.server.domain.board.model.Board;
import dev.erpix.easykan.server.domain.board.repository.BoardRepository;
import dev.erpix.easykan.server.domain.project.model.Project;
import dev.erpix.easykan.server.domain.project.service.ProjectService;
import dev.erpix.easykan.server.domain.user.model.User;
import dev.erpix.easykan.server.domain.user.service.UserService;
import dev.erpix.easykan.server.exception.project.ProjectNotFoundException;
import dev.erpix.easykan.server.exception.resource.ResourceNotFoundException;
import dev.erpix.easykan.server.exception.user.UserNotFoundException;
import dev.erpix.easykan.server.testsupport.Category;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@Tag(Category.UNIT_TEST)
@ExtendWith(MockitoExtension.class)
public class BoardServiceTest {

	@InjectMocks
	private BoardService boardService;

	@Mock
	private BoardRepository boardRepository;

	@Mock
	private BoardFactory boardFactory;

	@Mock
	private ProjectService projectService;

	@Mock
	private UserService userService;

	@Test
	void createBoard_shouldCreateBoardAndReturnDto() {
		UUID ownerId = UUID.randomUUID();
		User owner = User.builder().id(ownerId).build();
		UUID projectId = UUID.randomUUID();
		Project project = Project.builder().id(projectId).build();

		String boardName = "New Board";
		Board board = Board.builder().project(project).owner(owner).name(boardName).build();
		int nextPosition = 1;

		BoardCreateDto dto = new BoardCreateDto(boardName);

		when(userService.getById(ownerId)).thenReturn(owner);
		when(projectService.getProjectById(projectId)).thenReturn(project);
		when(boardRepository.findNextPositionByProjectId(projectId)).thenReturn(nextPosition);
		when(boardFactory.create(project, owner, boardName, nextPosition)).thenReturn(board);
		when(boardRepository.save(board)).thenReturn(board);

		BoardSummaryDto resultDto = boardService.createBoard(dto, ownerId, projectId);

		verify(userService).getById(ownerId);
		verify(projectService).getProjectById(projectId);
		verify(boardRepository).findNextPositionByProjectId(projectId);
		verify(boardFactory).create(project, owner, boardName, nextPosition);
		verify(boardRepository).save(board);

		assertThat(resultDto).isNotNull();
		assertThat(resultDto.name()).isEqualTo(boardName);
	}

	@Test
	void createBoard_shouldThrowUserNotFoundException_whenUserDoesNotExist() {
		UUID ownerId = UUID.randomUUID();
		UUID projectId = UUID.randomUUID();

		String boardName = "New Board";
		BoardCreateDto dto = new BoardCreateDto(boardName);

		when(userService.getById(ownerId)).thenThrow(UserNotFoundException.byId(ownerId));

		assertThrows(UserNotFoundException.class, () -> boardService.createBoard(dto, ownerId, projectId));

		verify(userService).getById(ownerId);
		verify(projectService, never()).getProjectById(projectId);
		verify(boardRepository, never()).findNextPositionByProjectId(any());
		verify(boardFactory, never()).create(any(), any(), any(), anyInt());
		verify(boardRepository, never()).save(any());
	}

	@Test
	void createBoard_shouldThrowProjectNotFoundException_whenProjectDoesNotExist() {
		UUID ownerId = UUID.randomUUID();
		User owner = User.builder().id(ownerId).build();
		UUID projectId = UUID.randomUUID();

		String boardName = "New Board";
		BoardCreateDto dto = new BoardCreateDto(boardName);

		when(userService.getById(ownerId)).thenReturn(owner);
		when(projectService.getProjectById(projectId)).thenThrow(ProjectNotFoundException.byId(projectId));

		assertThrows(ProjectNotFoundException.class, () -> boardService.createBoard(dto, ownerId, projectId));

		verify(userService).getById(ownerId);
		verify(projectService).getProjectById(projectId);
		verify(boardRepository, never()).findNextPositionByProjectId(any());
		verify(boardFactory, never()).create(any(), any(), any(), anyInt());
		verify(boardRepository, never()).save(any());
	}

	@Test
	void deleteBoard_shouldDeleteBoardAndUpdatePositions() {
		UUID boardId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		UUID projectId = UUID.randomUUID();
		int boardPosition = 2;

		Project project = mock(Project.class);
		Set<Board> boardsInProject = spy(new HashSet<>());
		Board board = Board.builder().id(boardId).position(boardPosition).project(project).build();
		boardsInProject.add(board);

		when(project.getBoards()).thenReturn(boardsInProject);
		when(boardRepository.findByIdAndProjectId(boardId, projectId)).thenReturn(Optional.of(board));

		boardService.deleteBoard(boardId, userId, projectId);

		verify(boardsInProject).remove(board);
		verify(boardRepository).delete(board);
		verify(boardRepository).decrementPositionsGreaterThan(boardPosition, projectId);
	}

	@Test
	void deleteBoard_shouldThrowResourceNotFoundException_whenBoardDoesNotExist() {
		UUID boardId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		UUID projectId = UUID.randomUUID();

		when(boardRepository.findByIdAndProjectId(boardId, projectId)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> boardService.deleteBoard(boardId, userId, projectId));

		verify(boardRepository, never()).delete(any());
		verify(boardRepository, never()).decrementPositionsGreaterThan(anyInt(), any(UUID.class));
	}

	@Test
	void getBoardsByProjectId_shouldReturnBoardDtos() {
		UUID projectId = UUID.randomUUID();
		UUID ownerId = UUID.randomUUID();
		User owner = User.builder().id(ownerId).build();

		Board board1 = Board.builder().id(UUID.randomUUID()).name("Board 1").owner(owner).build();
		Board board2 = Board.builder().id(UUID.randomUUID()).name("Board 2").owner(owner).build();
		List<Board> boards = List.of(board1, board2);

		when(boardRepository.findAllByProjectIdOrderByPositionAsc(projectId)).thenReturn(boards);

		List<BoardSummaryDto> result = boardService.getBoardsByProjectId(projectId);

		assertThat(result).hasSize(2);
		assertThat(result.get(0).name()).isEqualTo("Board 1");
		assertThat(result.get(1).name()).isEqualTo("Board 2");
		verify(boardRepository).findAllByProjectIdOrderByPositionAsc(projectId);
	}

	@Test
	void getBoardsByProjectId_shouldReturnEmptyList_whenNoBoardsExists() {
		UUID projectId = UUID.randomUUID();

		when(boardRepository.findAllByProjectIdOrderByPositionAsc(projectId)).thenReturn(Collections.emptyList());

		List<BoardSummaryDto> result = boardService.getBoardsByProjectId(projectId);

		assertThat(result).isEmpty();
		verify(boardRepository).findAllByProjectIdOrderByPositionAsc(projectId);
	}

}

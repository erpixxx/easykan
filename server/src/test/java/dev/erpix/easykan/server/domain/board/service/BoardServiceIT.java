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
import dev.erpix.easykan.server.exception.board.BoardNotFoundException;
import dev.erpix.easykan.server.testsupport.Category;
import dev.erpix.easykan.server.testsupport.PersistedDataProvider;
import dev.erpix.easykan.server.testsupport.annotation.*;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Tag(Category.INTEGRATION_TEST)
@IntegrationTest
public class BoardServiceIT {

	@Autowired
	private BoardService boardService;

	@Autowired
	private BoardRepository boardRepository;

	@SuppressWarnings("unused")
	@Autowired
	private BoardFactory boardFactory;

	@SuppressWarnings("unused")
	@Autowired
	private ProjectService projectService;

	@SuppressWarnings("unused")
	@Autowired
	private UserService userService;

	@Autowired
	private PersistedDataProvider persistedDataProvider;

	@Test
	@WithUser
	@WithProject
	void createBoard_shouldCreateBoard_whenUserHasPermission() {
		User currentUser = persistedDataProvider.getCurrentUser();
		Project project = persistedDataProvider.getLastCreatedProject().orElseThrow();

		BoardCreateDto dto = new BoardCreateDto("New Board");
		BoardSummaryDto resultDto = boardService.createBoard(dto, currentUser.getId(), project.getId());

		assertThat(resultDto).isNotNull();
		assertThat(resultDto.id()).isNotNull();
		assertThat(resultDto.name()).isEqualTo(dto.name());
		assertThat(resultDto.owner()).isEqualTo(currentUser);
		assertThat(resultDto.createdAt()).isNotNull();

		Board createdBoard = boardRepository.findById(resultDto.id()).orElseThrow();
		assertThat(createdBoard.getProject()).isEqualTo(project);
	}

	@Test
	@WithUser
	@WithProject
	void createBoard_shouldCreateBoardAndSetPositionToZero_whenNoOtherBoardsExist() {
		User currentUser = persistedDataProvider.getCurrentUser();
		Project project = persistedDataProvider.getLastCreatedProject().orElseThrow();

		BoardCreateDto dto = new BoardCreateDto("First Board");
		BoardSummaryDto resultDto = boardService.createBoard(dto, currentUser.getId(), project.getId());

		assertThat(resultDto).isNotNull();
		assertThat(resultDto.id()).isNotNull();

		Board createdBoard = boardRepository.findById(resultDto.id()).orElseThrow();
		assertThat(createdBoard.getPosition()).isEqualTo(0);
	}

	@Test
	@WithUser
	@WithProject(boards = @BoardSpec(name = "Existing Board"))
	void createBoard_shouldCreateBoardAndSetPositionToNextAvailable_whenOtherBoardsExist() {
		User currentUser = persistedDataProvider.getCurrentUser();
		Project project = persistedDataProvider.getLastCreatedProject().orElseThrow();

		BoardCreateDto dto = new BoardCreateDto("Second Board");
		BoardSummaryDto resultDto = boardService.createBoard(dto, currentUser.getId(), project.getId());

		assertThat(resultDto).isNotNull();
		assertThat(resultDto.id()).isNotNull();

		Board createdBoard = boardRepository.findById(resultDto.id()).orElseThrow();
		assertThat(createdBoard.getPosition()).isEqualTo(1);
	}

	@Test
	@WithUser
	@WithProject(boards = @BoardSpec)
	void deleteBoard_shouldDeleteBoard_whenBoardExists() {
		User currentUser = persistedDataProvider.getCurrentUser();
		Project project = persistedDataProvider.getLastCreatedProject().orElseThrow();
		Board board = project.getBoards().iterator().next();

		boardService.deleteBoard(board.getId(), currentUser.getId(), project.getId());

		assertThat(boardRepository.findById(board.getId())).isEmpty();
	}

	@Test
	@WithUser
	@WithProject(boards = { @BoardSpec(name = "Board 1", position = 0), @BoardSpec(name = "Board 2", position = 1),
			@BoardSpec(name = "Board 3", position = 2) })
	void deleteBoard_shouldUpdatePositions_whenBoardIsDeleted() {
		User currentUser = persistedDataProvider.getCurrentUser();
		Project project = persistedDataProvider.getLastCreatedProject().orElseThrow();

		Board middleBoard = project.getBoards().stream().filter(b -> b.getPosition() == 1).findFirst().orElseThrow();

		Board lastBoard = project.getBoards().stream().filter(b -> b.getPosition() == 2).findFirst().orElseThrow();

		boardService.deleteBoard(middleBoard.getId(), currentUser.getId(), project.getId());

		Board updatedLastBoard = boardRepository.findById(lastBoard.getId()).orElseThrow();
		assertThat(updatedLastBoard.getPosition()).isEqualTo(1);
	}

	@Test
	@WithUser
	@WithProject
	void deleteBoard_shouldThrowBoardNotFoundException_whenBoardDoesNotExist() {
		User currentUser = persistedDataProvider.getCurrentUser();
		Project project = persistedDataProvider.getLastCreatedProject().orElseThrow();
		UUID nonExistentBoardId = UUID.randomUUID();

		assertThrows(BoardNotFoundException.class,
				() -> boardService.deleteBoard(nonExistentBoardId, currentUser.getId(), project.getId()));
	}

	@Test
	@WithUser
	@WithProject(boards = @BoardSpec(name = "Only Board"))
	void deleteBoard_shouldLeaveProjectWithNoBoards_whenDeletingOnlyBoard() {
		User currentUser = persistedDataProvider.getCurrentUser();
		Project project = persistedDataProvider.getLastCreatedProject().orElseThrow();
		Board onlyBoard = project.getBoards().iterator().next();

		boardService.deleteBoard(onlyBoard.getId(), currentUser.getId(), project.getId());

		Project updatedProject = persistedDataProvider.getProjectById(project.getId()).orElseThrow();
		assertThat(updatedProject.getBoards()).isEmpty();
	}

	@Test
	@WithUser
	@WithProject(
			boards = { @BoardSpec(name = "First Board", position = 0), @BoardSpec(name = "Last Board", position = 1) })
	void deleteBoard_shouldNotAffectOtherBoardPositions_whenDeletingLastBoard() {
		User currentUser = persistedDataProvider.getCurrentUser();
		Project project = persistedDataProvider.getLastCreatedProject().orElseThrow();

		Board lastBoard = project.getBoards().stream().filter(b -> b.getPosition() == 1).findFirst().orElseThrow();

		Board firstBoard = project.getBoards().stream().filter(b -> b.getPosition() == 0).findFirst().orElseThrow();

		boardService.deleteBoard(lastBoard.getId(), currentUser.getId(), project.getId());

		Board updatedFirstBoard = boardRepository.findById(firstBoard.getId()).orElseThrow();
		assertThat(updatedFirstBoard.getPosition()).isEqualTo(0);
	}

	@Test
	@WithUser
	@WithProject
	void getBoardsByProjectId_shouldReturnEmptyList_whenProjectHasNoBoards() {
		Project project = persistedDataProvider.getLastCreatedProject().orElseThrow();

		List<BoardSummaryDto> boards = boardService.getBoardsByProjectId(project.getId());

		assertThat(boards).isEmpty();
	}

	@Test
	@WithUser
	@WithProject(boards = { @BoardSpec(name = "Board 1", position = 0), @BoardSpec(name = "Board 2", position = 1),
			@BoardSpec(name = "Board 3", position = 2) })
	void getBoardsByProjectId_shouldReturnBoardsInPositionOrder_whenProjectHasMultipleBoards() {
		Project project = persistedDataProvider.getLastCreatedProject().orElseThrow();

		List<BoardSummaryDto> boards = boardService.getBoardsByProjectId(project.getId());

		assertThat(boards).hasSize(3);
		assertThat(boards.get(0).name()).isEqualTo("Board 1");
		assertThat(boards.get(1).name()).isEqualTo("Board 2");
		assertThat(boards.get(2).name()).isEqualTo("Board 3");
	}

	@Test
	@WithUser
	@WithProjects({ @WithProject(name = "Project 1", boards = @BoardSpec(name = "Project 1 Board")),
			@WithProject(name = "Project 2", boards = @BoardSpec(name = "Project 2 Board")) })
	void getBoardsByProjectId_shouldReturnBoardsOnlyFromSpecifiedProject() {
		Project firstProject = persistedDataProvider.getFirstCreatedProject().orElseThrow();
		Project secondProject = persistedDataProvider.getLastCreatedProject().orElseThrow();

		List<BoardSummaryDto> firstProjectBoards = boardService.getBoardsByProjectId(firstProject.getId());
		List<BoardSummaryDto> secondProjectBoards = boardService.getBoardsByProjectId(secondProject.getId());

		assertThat(firstProjectBoards).hasSize(1);
		assertThat(firstProjectBoards.getFirst().name()).isEqualTo("Project 1 Board");

		assertThat(secondProjectBoards).hasSize(1);
		assertThat(secondProjectBoards.getFirst().name()).isEqualTo("Project 2 Board");
	}

	@Test
	@WithUser
	@WithProject(boards = { @BoardSpec(name = "First Board"), @BoardSpec(name = "Second Board") })
	void getBoardsByProjectId_shouldMapBoardFieldsCorrectly() {
		Project project = persistedDataProvider.getLastCreatedProject().orElseThrow();
		User owner = persistedDataProvider.getCurrentUser();

		List<BoardSummaryDto> boards = boardService.getBoardsByProjectId(project.getId());

		assertThat(boards).hasSize(2);

		BoardSummaryDto firstBoard = boards.getFirst();
		assertThat(firstBoard.id()).isNotNull();
		assertThat(firstBoard.name()).isEqualTo("First Board");
		assertThat(firstBoard.owner()).isEqualTo(owner);
		assertThat(firstBoard.createdAt()).isNotNull();
	}

	@Test
	@WithUser
	@WithProject(boards = @BoardSpec)
	void getBoardById_shouldReturnBoard_whenBoardExists() {
		Project project = persistedDataProvider.getLastCreatedProject().orElseThrow();
		Board expectedBoard = project.getBoards().iterator().next();

		Board actualBoard = boardService.getBoardById(expectedBoard.getId(), project.getId());

		assertThat(actualBoard).isNotNull();
		assertThat(actualBoard.getId()).isEqualTo(expectedBoard.getId());
		assertThat(actualBoard.getName()).isEqualTo("Test Board");
		assertThat(actualBoard.getProject().getId()).isEqualTo(project.getId());
	}

	@Test
	@WithUser
	@WithProject
	void getBoardById_shouldThrowBoardNotFoundException_whenBoardDoesNotExist() {
		Project project = persistedDataProvider.getLastCreatedProject().orElseThrow();
		UUID nonExistentBoardId = UUID.randomUUID();

		assertThrows(BoardNotFoundException.class,
				() -> boardService.getBoardById(nonExistentBoardId, project.getId()));
	}

	@Test
	@WithUser
	@WithProjects({ @WithProject(boards = @BoardSpec(name = "Project 1 Board")),
			@WithProject(boards = @BoardSpec(name = "Project 2 Board")) })
	void getBoardById_shouldThrowBoardNotFoundException_whenBoardExistsInDifferentProject() {
		Project firstProject = persistedDataProvider.getFirstCreatedProject().orElseThrow();
		Project secondProject = persistedDataProvider.getLastCreatedProject().orElseThrow();

		Board secondProjectBoard = secondProject.getBoards().iterator().next();

		assertThrows(BoardNotFoundException.class,
				() -> boardService.getBoardById(secondProjectBoard.getId(), firstProject.getId()));
	}

	@Test
	@WithUser
	@WithProject(boards = { @BoardSpec(name = "First Board"), @BoardSpec(name = "Second Board") })
	void getBoardById_shouldReturnCorrectBoard_whenMultipleBoardsExist() {
		Project project = persistedDataProvider.getLastCreatedProject().orElseThrow();

		Board firstBoard = project.getBoards()
			.stream()
			.filter(b -> b.getName().equals("First Board"))
			.findFirst()
			.orElseThrow();

		Board secondBoard = project.getBoards()
			.stream()
			.filter(b -> b.getName().equals("Second Board"))
			.findFirst()
			.orElseThrow();

		Board retrievedFirstBoard = boardService.getBoardById(firstBoard.getId(), project.getId());
		assertThat(retrievedFirstBoard).isNotNull();
		assertThat(retrievedFirstBoard.getId()).isEqualTo(firstBoard.getId());
		assertThat(retrievedFirstBoard.getName()).isEqualTo("First Board");

		Board retrievedSecondBoard = boardService.getBoardById(secondBoard.getId(), project.getId());
		assertThat(retrievedSecondBoard).isNotNull();
		assertThat(retrievedSecondBoard.getId()).isEqualTo(secondBoard.getId());
		assertThat(retrievedSecondBoard.getName()).isEqualTo("Second Board");
	}

}

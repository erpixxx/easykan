package dev.erpix.easykan.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.erpix.easykan.server.domain.board.dto.BoardCreateDto;
import dev.erpix.easykan.server.domain.board.dto.BoardSummaryDto;
import dev.erpix.easykan.server.domain.board.model.Board;
import dev.erpix.easykan.server.domain.board.service.BoardService;
import dev.erpix.easykan.server.domain.project.model.Project;
import dev.erpix.easykan.server.domain.user.model.User;
import dev.erpix.easykan.server.exception.board.BoardNotFoundException;
import dev.erpix.easykan.server.testsupport.Category;
import dev.erpix.easykan.server.testsupport.annotation.WebMvcBundle;
import dev.erpix.easykan.server.testsupport.annotation.WithSecurityContextUser;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Tag(Category.INTEGRATION_TEST)
@WebMvcBundle(BoardController.class)
public class BoardControllerIT extends AbstractControllerSecurityIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@SuppressWarnings("unused")
	@MockitoBean
	private BoardService boardService;

	@Override
	protected Stream<Arguments> provideProtectedEndpoints() {
		return Stream.of(Arguments.of("POST", "/api/boards"),
				Arguments.of("DELETE", "/api/boards/00000000-0000-0000-0000-000000000000"),
				Arguments.of("GET", "/api/boards"),
				Arguments.of("GET", "/api/boards/00000000-0000-0000-0000-000000000000"));
	}

	@Test
	@WithSecurityContextUser
	void createBoard_shouldReturnCreated_whenDataIsValid() throws Exception {
		UUID projectId = UUID.randomUUID();
		UUID currentUserId = UUID.fromString(WithSecurityContextUser.Default.ID);
		BoardCreateDto createDto = new BoardCreateDto("Test Board");
		User owner = User.builder().id(currentUserId).build();
		BoardSummaryDto resultDto = new BoardSummaryDto(UUID.randomUUID(), owner, "Test Board", Instant.now());

		when(boardService.createBoard(any(BoardCreateDto.class), eq(currentUserId), eq(projectId)))
			.thenReturn(resultDto);

		mockMvc
			.perform(post("/api/boards").param("projectId", projectId.toString())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDto)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.name").value("Test Board"));

		verify(boardService).createBoard(any(BoardCreateDto.class), eq(currentUserId), eq(projectId));
	}

	@Test
	@WithSecurityContextUser
	void createBoard_shouldReturnBadRequest_whenDataIsInvalid() throws Exception {
		UUID projectId = UUID.randomUUID();
		BoardCreateDto createDto = new BoardCreateDto("");

		mockMvc
			.perform(post("/api/boards").param("projectId", projectId.toString())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDto)))
			.andExpect(status().isBadRequest());

		verify(boardService, never()).createBoard(any(), any(), any());
	}

	@Test
	@WithSecurityContextUser
	void deleteBoard_shouldReturnNoContent_whenSuccessful() throws Exception {
		UUID boardId = UUID.randomUUID();
		UUID projectId = UUID.randomUUID();
		UUID currentUserId = UUID.fromString(WithSecurityContextUser.Default.ID);

		mockMvc.perform(delete("/api/boards/{boardId}", boardId).param("projectId", projectId.toString()))
			.andExpect(status().isNoContent());

		verify(boardService).deleteBoard(eq(boardId), eq(currentUserId), eq(projectId));
	}

	@Test
	@WithSecurityContextUser
	void deleteBoard_shouldReturnForbidden_whenUserLacksPermission() throws Exception {
		UUID boardId = UUID.randomUUID();
		UUID projectId = UUID.randomUUID();
		UUID currentUserId = UUID.fromString(WithSecurityContextUser.Default.ID);

		doThrow(new AccessDeniedException("Access Denied")).when(boardService)
			.deleteBoard(eq(boardId), eq(currentUserId), eq(projectId));

		mockMvc.perform(delete("/api/boards/{boardId}", boardId).param("projectId", projectId.toString()))
			.andExpect(status().isForbidden());
	}

	@Test
	@WithSecurityContextUser
	void getBoardsByProject_shouldReturnBoardList() throws Exception {
		UUID projectId = UUID.randomUUID();
		User owner = User.builder().id(UUID.randomUUID()).build();

		List<BoardSummaryDto> boards = Arrays.asList(
				new BoardSummaryDto(UUID.randomUUID(), owner, "Board 1", Instant.now()),
				new BoardSummaryDto(UUID.randomUUID(), owner, "Board 2", Instant.now()));

		when(boardService.getBoardsByProjectId(eq(projectId))).thenReturn(boards);

		mockMvc.perform(get("/api/boards").param("projectId", projectId.toString()))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$.length()").value(2))
			.andExpect(jsonPath("$[0].name").value("Board 1"))
			.andExpect(jsonPath("$[1].name").value("Board 2"));

		verify(boardService).getBoardsByProjectId(eq(projectId));
	}

	@Test
	@WithSecurityContextUser
	void getBoardById_shouldReturnBoard_whenBoardExists() throws Exception {
		UUID boardId = UUID.randomUUID();
		UUID projectId = UUID.randomUUID();

		Project project = Project.builder().id(projectId).build();
		User owner = User.builder().id(UUID.randomUUID()).build();
		Board board = Board.builder().id(boardId).name("Test Board").project(project).owner(owner).build();

		when(boardService.getBoardById(eq(boardId), eq(projectId))).thenReturn(board);

		mockMvc.perform(get("/api/boards/{boardId}", boardId).param("projectId", projectId.toString()))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.id").value(boardId.toString()))
			.andExpect(jsonPath("$.name").value("Test Board"));

		verify(boardService).getBoardById(eq(boardId), eq(projectId));
	}

	@Test
	@WithSecurityContextUser
	void getBoardById_shouldReturnNotFound_whenBoardDoesNotExist() throws Exception {
		UUID boardId = UUID.randomUUID();
		UUID projectId = UUID.randomUUID();

		when(boardService.getBoardById(eq(boardId), eq(projectId)))
			.thenThrow(BoardNotFoundException.byIdInProject(boardId, projectId));

		mockMvc.perform(get("/api/boards/{boardId}", boardId).param("projectId", projectId.toString()))
			.andExpect(status().isNotFound());

		verify(boardService).getBoardById(eq(boardId), eq(projectId));
	}

}

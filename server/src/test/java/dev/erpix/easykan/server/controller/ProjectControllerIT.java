package dev.erpix.easykan.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.erpix.easykan.server.domain.project.dto.ProjectCreateDto;
import dev.erpix.easykan.server.domain.project.dto.ProjectSummaryDto;
import dev.erpix.easykan.server.domain.project.service.ProjectService;
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

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Tag(Category.INTEGRATION_TEST)
@WebMvcBundle(ProjectController.class)
public class ProjectControllerIT extends AbstractControllerSecurityIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@SuppressWarnings("unused")
	@MockitoBean
	private ProjectService projectService;

	@Override
	protected Stream<Arguments> provideProtectedEndpoints() {
		return Stream.of(Arguments.of("POST", "/api/projects"), Arguments.of("DELETE", "/api/projects"),
				Arguments.of("GET", "/api/projects"));
	}

	@Test
	@WithSecurityContextUser
	void createProject_shouldReturnCreated_whenDataIsValid() throws Exception {
		var createDto = new ProjectCreateDto("Test Project");
		var resultDto = new ProjectSummaryDto(UUID.randomUUID(), "Test Project", Collections.emptySet());
		UUID currentUserId = UUID.fromString(WithSecurityContextUser.Default.ID);

		when(projectService.createProject(any(ProjectCreateDto.class), eq(currentUserId))).thenReturn(resultDto);

		mockMvc
			.perform(post("/api/projects").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDto)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.name").value("Test Project"));

		verify(projectService).createProject(any(ProjectCreateDto.class), eq(currentUserId));
	}

	@Test
	@WithSecurityContextUser
	void createProject_shouldReturnBadRequest_whenDataIsInvalid() throws Exception {
		var createDto = new ProjectCreateDto("");

		mockMvc
			.perform(post("/api/projects").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDto)))
			.andExpect(status().isBadRequest());

		verify(projectService, never()).createProject(any(), any());
	}

	@Test
	@WithSecurityContextUser
	void deleteProject_shouldReturnNoContent_whenSuccessful() throws Exception {
		UUID projectId = UUID.randomUUID();
		UUID currentUserId = UUID.fromString(WithSecurityContextUser.Default.ID);

		mockMvc.perform(delete("/api/projects").param("projectId", projectId.toString()))
			.andExpect(status().isNoContent());

		verify(projectService).deleteProject(eq(projectId), eq(currentUserId));
	}

	@Test
	@WithSecurityContextUser
	void deleteProject_shouldReturnForbidden_whenUserLacksPermission() throws Exception {
		UUID projectId = UUID.randomUUID();
		UUID currentUserId = UUID.fromString(WithSecurityContextUser.Default.ID);

		doThrow(new AccessDeniedException("Access Denied")).when(projectService)
			.deleteProject(eq(projectId), eq(currentUserId));

		mockMvc.perform(delete("/api/projects").param("projectId", projectId.toString()))
			.andExpect(status().isForbidden());
	}

	@Test
	@WithSecurityContextUser
	void getProjectsForCurrentUser_shouldReturnProjectList_whenUserIsAuthenticated() throws Exception {
		UUID currentUserId = UUID.fromString(WithSecurityContextUser.Default.ID);
		var projectSummary = new ProjectSummaryDto(UUID.randomUUID(), "My Project", Collections.emptySet());

		when(projectService.getProjectsForUser(eq(currentUserId))).thenReturn(List.of(projectSummary));

		mockMvc.perform(get("/api/projects"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$.length()").value(1))
			.andExpect(jsonPath("$[0].name").value("My Project"));
	}

}

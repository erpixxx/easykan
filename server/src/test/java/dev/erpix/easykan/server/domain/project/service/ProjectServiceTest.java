package dev.erpix.easykan.server.domain.project.service;

import dev.erpix.easykan.server.domain.project.dto.PositionedProjectDto;
import dev.erpix.easykan.server.domain.project.dto.ProjectCreateDto;
import dev.erpix.easykan.server.domain.project.dto.ProjectSummaryDto;
import dev.erpix.easykan.server.domain.project.factory.ProjectFactory;
import dev.erpix.easykan.server.domain.project.model.Project;
import dev.erpix.easykan.server.domain.project.model.ProjectUserView;
import dev.erpix.easykan.server.domain.project.repository.ProjectMemberRepository;
import dev.erpix.easykan.server.domain.project.repository.ProjectRepository;
import dev.erpix.easykan.server.domain.project.repository.ProjectUserViewRepository;
import dev.erpix.easykan.server.domain.user.model.User;
import dev.erpix.easykan.server.domain.user.model.UserPermission;
import dev.erpix.easykan.server.domain.user.service.UserService;
import dev.erpix.easykan.server.exception.project.ProjectNotFoundException;
import dev.erpix.easykan.server.exception.user.UserNotFoundException;
import dev.erpix.easykan.server.testsupport.Category;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Tag(Category.UNIT_TEST)
@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

	@InjectMocks
	private ProjectService projectService;

	@Mock
	private ProjectFactory projectFactory;

	@Mock
	private ProjectMemberRepository projectMemberRepository;

	@Mock
	private ProjectRepository projectRepository;

	@Mock
	private ProjectUserViewRepository projectUserViewRepository;

	@Mock
	private UserService userService;

	@Test
	void createProject_shouldCreateProjectAndReturnDto() {
		UUID ownerId = UUID.randomUUID();
		User owner = User.builder().id(ownerId).build();

		String projectName = "New Project";
		Project project = Project.builder().name(projectName).owner(owner).build();

		ProjectCreateDto createDto = new ProjectCreateDto(projectName);
		int expectedNextPosition = 3;

		when(userService.getById(ownerId)).thenReturn(owner);
		when(projectUserViewRepository.findNextPositionByUserId(ownerId)).thenReturn(expectedNextPosition);
		when(projectFactory.create(createDto.name(), owner, expectedNextPosition)).thenReturn(project);
		when(projectRepository.save(project)).thenReturn(project);

		PositionedProjectDto resultDto = projectService.createProject(createDto, ownerId);

		verify(userService).getById(ownerId);
		verify(projectUserViewRepository).findNextPositionByUserId(ownerId);
		verify(projectFactory).create(projectName, owner, 3);
		verify(projectRepository).save(project);

		assertThat(resultDto).isNotNull();
		assertThat(resultDto.name()).isEqualTo(projectName);
	}

	@Test
	void createProject_shouldThrowUserNotFoundException_whenUserDoesNotExist() {
		UUID nonExistentUserId = UUID.randomUUID();
		ProjectCreateDto createDto = new ProjectCreateDto("Test Project");

		when(userService.getById(nonExistentUserId)).thenThrow(UserNotFoundException.byId(nonExistentUserId));

		assertThrows(UserNotFoundException.class, () -> projectService.createProject(createDto, nonExistentUserId));

		verify(projectUserViewRepository, never()).findNextPositionByUserId(any());
		verify(projectFactory, never()).create(any(), any(), anyInt());
		verify(projectRepository, never()).save(any());
	}

	@Test
	void createProject_shouldSetPositionToZero_whenUserHasNoProjects() {
		UUID ownerId = UUID.randomUUID();
		User owner = User.builder().id(ownerId).build();

		ProjectCreateDto createDto = new ProjectCreateDto("First Project");
		Project mockProject = Project.builder().build();

		when(projectUserViewRepository.findNextPositionByUserId(ownerId)).thenReturn(0);
		when(userService.getById(ownerId)).thenReturn(owner);
		when(projectFactory.create(anyString(), any(User.class), eq(0))).thenReturn(mockProject);
		when(projectRepository.save(any(Project.class))).thenReturn(mockProject);

		projectService.createProject(createDto, ownerId);

		verify(projectFactory).create(createDto.name(), owner, 0);
	}

	@Test
	void deleteProject_shouldDeleteProject_whenUserIsOwner() {
		UUID ownerId = UUID.randomUUID();
		User owner = User.builder().id(ownerId).permissions(UserPermission.DEFAULT_PERMISSIONS.getValue()).build();

		UUID projectId = UUID.randomUUID();
		Project project = Project.builder().id(projectId).owner(owner).build();

		when(projectRepository.findById(projectId)).thenReturn(java.util.Optional.of(project));
		when(userService.getById(ownerId)).thenReturn(owner);

		projectService.deleteProject(projectId, ownerId);

		verify(projectRepository, times(1)).delete(project);
	}

	@Test
	void deleteProject_shouldDeleteProject_whenUserHasManageProjectsPermission() {
		UUID adminId = UUID.randomUUID();
		User admin = User.builder().id(adminId).permissions(UserPermission.MANAGE_PROJECTS.getValue()).build();

		UUID projectId = UUID.randomUUID();
		User owner = User.builder().id(UUID.randomUUID()).build();
		Project project = Project.builder().id(projectId).owner(owner).build();

		when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
		when(userService.getById(adminId)).thenReturn(admin);

		projectService.deleteProject(projectId, adminId);

		verify(projectRepository, times(1)).delete(project);
	}

	@Test
	void deleteProject_shouldDeleteProject_whenUserIsAdmin() {
		UUID adminId = UUID.randomUUID();
		User admin = User.builder().id(adminId).permissions(UserPermission.ADMIN.getValue()).build();

		UUID projectId = UUID.randomUUID();
		User owner = User.builder().id(UUID.randomUUID()).build();
		Project project = Project.builder().id(projectId).owner(owner).build();

		when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
		when(userService.getById(adminId)).thenReturn(admin);

		projectService.deleteProject(projectId, adminId);

		verify(projectRepository, times(1)).delete(project);
	}

	@Test
	void deleteProject_shouldThrowAccessDeniedException_whenUserIsNotOwnerOrAdmin() {
		UUID userId = UUID.randomUUID();
		User user = User.builder().id(userId).permissions(UserPermission.DEFAULT_PERMISSIONS.getValue()).build();

		UUID projectId = UUID.randomUUID();
		User owner = User.builder().id(UUID.randomUUID()).build();
		Project project = Project.builder().id(projectId).owner(owner).build();

		when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
		when(userService.getById(userId)).thenReturn(user);

		assertThrows(AccessDeniedException.class, () -> projectService.deleteProject(projectId, userId));

		verify(projectRepository, never()).delete(any());
	}

	@Test
	void deleteProject_shouldThrowProjectNotFoundException_whenProjectDoesNotExist() {
		UUID userId = UUID.randomUUID();
		UUID nonExistentProjectId = UUID.randomUUID();

		when(projectRepository.findById(nonExistentProjectId)).thenReturn(Optional.empty());

		assertThrows(ProjectNotFoundException.class, () -> projectService.deleteProject(nonExistentProjectId, userId));
		verify(projectRepository, never()).delete(any());
	}

	@Test
	void deleteProject_shouldThrowUserNotFoundException_whenUserDoesNotExist() {
		UUID nonExistentUserId = UUID.randomUUID();
		UUID projectId = UUID.randomUUID();
		User owner = User.builder().id(UUID.randomUUID()).build();
		Project project = Project.builder().id(projectId).owner(owner).build();

		when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
		when(userService.getById(nonExistentUserId)).thenThrow(UserNotFoundException.byId(nonExistentUserId));

		assertThrows(UserNotFoundException.class, () -> projectService.deleteProject(projectId, nonExistentUserId));
		verify(projectRepository, never()).delete(any());
	}

	@Test
	void getProjectsForUser_shouldReturnProjectSummaries_whenUserHasProjects() {
		UUID ownerId = UUID.randomUUID();
		User owner = User.builder().id(ownerId).build();

		String projectName1 = "First";
		String projectName2 = "Second";
		Project project1 = Project.builder().id(UUID.randomUUID()).name(projectName1).owner(owner).build();
		Project project2 = Project.builder().id(UUID.randomUUID()).name(projectName2).owner(owner).build();

		ProjectUserView puv1 = ProjectUserView.builder().project(project1).user(owner).position(0).build();
		ProjectUserView puv2 = ProjectUserView.builder().project(project2).user(owner).position(1).build();

		List<ProjectUserView> userViews = List.of(puv1, puv2);

		when(projectUserViewRepository.findAllByUserWithDetails(ownerId)).thenReturn(userViews);

		List<ProjectSummaryDto> result = projectService.getProjectsForUser(ownerId);

		verify(projectUserViewRepository, times(1)).findAllByUserWithDetails(ownerId);

		assertThat(result).isNotNull();
		assertThat(result).hasSize(2);

		assertThat(result.get(0).name()).isEqualTo(projectName1);
		assertThat(result.get(0).position()).isZero();

		assertThat(result.get(1).name()).isEqualTo(projectName2);
		assertThat(result.get(1).position()).isEqualTo(1);
	}

	@Test
	void getProjectsForUser_shouldReturnEmptyList_whenUserHasNoProjects() {
		UUID userId = UUID.randomUUID();

		when(projectUserViewRepository.findAllByUserWithDetails(userId)).thenReturn(Collections.emptyList());

		List<ProjectSummaryDto> result = projectService.getProjectsForUser(userId);

		assertThat(result).isNotNull();
		assertThat(result).isEmpty();

		verify(projectUserViewRepository).findAllByUserWithDetails(userId);
	}

}

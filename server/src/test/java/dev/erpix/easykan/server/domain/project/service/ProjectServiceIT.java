package dev.erpix.easykan.server.domain.project.service;

import dev.erpix.easykan.server.domain.project.dto.ProjectCreateDto;
import dev.erpix.easykan.server.domain.project.dto.ProjectSummaryDto;
import dev.erpix.easykan.server.domain.project.factory.ProjectFactory;
import dev.erpix.easykan.server.domain.project.model.Project;
import dev.erpix.easykan.server.domain.project.model.ProjectMember;
import dev.erpix.easykan.server.domain.project.model.ProjectUserView;
import dev.erpix.easykan.server.domain.project.repository.ProjectMemberRepository;
import dev.erpix.easykan.server.domain.project.repository.ProjectRepository;
import dev.erpix.easykan.server.domain.project.repository.ProjectUserViewRepository;
import dev.erpix.easykan.server.domain.user.dto.UserCreateRequestDto;
import dev.erpix.easykan.server.domain.user.model.User;
import dev.erpix.easykan.server.domain.user.model.UserPermission;
import dev.erpix.easykan.server.domain.user.service.UserService;
import dev.erpix.easykan.server.exception.project.ProjectNotFoundException;
import dev.erpix.easykan.server.exception.user.UserNotFoundException;
import dev.erpix.easykan.server.testsupport.Category;
import dev.erpix.easykan.server.testsupport.annotation.IntegrationTest;
import dev.erpix.easykan.server.testsupport.annotation.WithPersistedUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Tag(Category.INTEGRATION_TEST)
@IntegrationTest
public class ProjectServiceIT {

	@Autowired
	private ProjectService projectService;

	@Autowired
	private ProjectFactory projectFactory;

	@Autowired
	private ProjectMemberRepository projectMemberRepository;

	@Autowired
	private ProjectRepository projectRepository;

	@Autowired
	private ProjectUserViewRepository projectUserViewRepository;

	@Autowired
	private UserService userService;

	private User user;

	@BeforeEach
	void setUp() {
		user = userService.getByLogin(WithPersistedUser.Default.LOGIN);
	}

	@Test
	@WithPersistedUser(permissions = UserPermission.CREATE_PROJECTS)
	void createProject_shouldCreateProjectAndSetOwnerAsFirstMember_whenUserHasPermission() {
		assertThat(projectRepository.countByOwner_Login(WithPersistedUser.Default.LOGIN)).isEqualTo(0L);

		var dto = new ProjectCreateDto("New Project");
		projectService.createProject(dto, this.user.getId());

		assertThat(projectRepository.count()).isEqualTo(1L);

		Project savedProject = projectRepository.findAll().getFirst();
		assertThat(savedProject.getName()).isEqualTo(dto.name());
		assertThat(savedProject.getOwner().getId()).isEqualTo(this.user.getId());

		// Owner should be the first member of the project
		assertThat(projectMemberRepository.count()).isEqualTo(1L);
		ProjectMember member = projectMemberRepository.findAll().getFirst();
		assertThat(member.getProject()).isEqualTo(savedProject);
		assertThat(member.getUser()).isEqualTo(this.user);

		// Owner should have a user view with position 0
		assertThat(projectUserViewRepository.count()).isEqualTo(1L);
		ProjectUserView userView = projectUserViewRepository.findAll().getFirst();
		assertThat(userView.getPosition()).isEqualTo(0);
	}

	@Test
	@WithPersistedUser(permissions = UserPermission.CREATE_PROJECTS)
	void createProject_shouldSetCorrectIncrementedPosition_whenUserHasExistingProjects() {
		var firstDto = new ProjectCreateDto("First Project");
		var firstProjectResult = projectService.createProject(firstDto, this.user.getId());

		assertThat(firstProjectResult.position()).isEqualTo(0);

		var secondDto = new ProjectCreateDto("Second Project");
		var secondProjectResult = projectService.createProject(secondDto, this.user.getId());

		assertThat(secondProjectResult.position()).isEqualTo(1);

		List<ProjectUserView> userViews = projectUserViewRepository.findAllByUserWithDetails(this.user.getId());
		assertThat(userViews).hasSize(2);
		assertThat(userViews.get(0).getPosition()).isEqualTo(0);
		assertThat(userViews.get(1).getPosition()).isEqualTo(1);
	}

	@Test
	@WithPersistedUser
	void createProject_shouldThrowAccessDenied_whenUserDoesNotHavePermission() {
		assertThat(projectRepository.countByOwner_Login(WithPersistedUser.Default.LOGIN)).isEqualTo(0L);

		var dto = new ProjectCreateDto("New Project");
		assertThrows(AccessDeniedException.class, () -> projectService.createProject(dto, this.user.getId()));

		assertThat(projectRepository.countByOwner_Login(WithPersistedUser.Default.LOGIN)).isEqualTo(0L);
	}

	@Test
	@WithPersistedUser(permissions = UserPermission.CREATE_PROJECTS)
	void createProject_shouldThrowUserNotFound_whenUserDoesNotExist() {
		var dto = new ProjectCreateDto("New Project");
		assertThrows(UserNotFoundException.class, () -> projectService.createProject(dto, UUID.randomUUID()));
	}

	@Test
	@WithPersistedUser
	void getProjectsForUser_shouldReturnEmptyList_whenUserHasNoProjects() {
		var projects = projectService.getProjectsForUser(this.user.getId());
		assertThat(projects).isEmpty();
	}

	@Test
	@WithPersistedUser(permissions = UserPermission.CREATE_PROJECTS)
	void deleteProject_shouldDeleteProjectAndAllRelatedData_whenUserIsOwner() {
		var createDto = new ProjectCreateDto("Project to Delete");
		projectService.createProject(createDto, this.user.getId());

		Project savedProject = projectRepository.findAll().getFirst();
		UUID projectId = savedProject.getId();

		assertThat(projectRepository.count()).isEqualTo(1L);
		assertThat(projectMemberRepository.count()).isEqualTo(1L);
		assertThat(projectUserViewRepository.count()).isEqualTo(1L);

		projectService.deleteProject(projectId, this.user.getId());

		assertThat(projectRepository.count()).isEqualTo(0L);
		assertThat(projectMemberRepository.count()).isEqualTo(0L);
		assertThat(projectUserViewRepository.count()).isEqualTo(0L);
	}

	@Test
	@WithPersistedUser(permissions = { UserPermission.MANAGE_PROJECTS, UserPermission.CREATE_PROJECTS,
			UserPermission.MANAGE_USERS })
	void deleteProject_shouldDeleteProject_whenUserHasPermission() {
		UserCreateRequestDto requestDto = new UserCreateRequestDto("otheruser", "Other User", "password",
				"otheruser@easykan.dev");
		User owner = userService.create(requestDto);
		owner.setPermissions(UserPermission.CREATE_PROJECTS.getValue());

		var createDto = new ProjectCreateDto("Another User's Project");
		projectService.createProject(createDto, owner.getId());

		Project savedProject = projectRepository.findAll().getFirst();
		UUID projectId = savedProject.getId();

		assertThat(projectRepository.count()).isEqualTo(1L);

		projectService.deleteProject(projectId, this.user.getId());

		assertThat(projectRepository.count()).isEqualTo(0L);
	}

	@Test
	@WithPersistedUser(permissions = { UserPermission.MANAGE_USERS, UserPermission.CREATE_PROJECTS })
	void deleteProject_shouldThrowAccessDeniedException_whenUserIsNotOwnerOrAdmin() {
		UserCreateRequestDto requestDto = new UserCreateRequestDto("owner", "Owner User", "owner@easykan.dev",
				"password");
		User owner = userService.create(requestDto);
		owner.setPermissions(UserPermission.CREATE_PROJECTS.getValue());

		var createDto = new ProjectCreateDto("Protected Project");
		projectService.createProject(createDto, owner.getId());
		Project savedProject = projectRepository.findAll().getFirst();
		UUID projectId = savedProject.getId();

		UserCreateRequestDto attackerDto = new UserCreateRequestDto("attacker", "Attacker User", "attacker@easykan.dev",
				"password");
		User attacker = userService.create(attackerDto);

		assertThat(projectRepository.count()).isEqualTo(1L);

		assertThrows(AccessDeniedException.class, () -> projectService.deleteProject(projectId, attacker.getId()));

		assertThat(projectRepository.count()).isEqualTo(1L);
	}

	@Test
	@WithPersistedUser
	void deleteProject_shouldThrowProjectNotFoundException_whenProjectDoesNotExist() {
		UUID nonExistentProjectId = UUID.randomUUID();

		assertThrows(ProjectNotFoundException.class,
				() -> projectService.deleteProject(nonExistentProjectId, this.user.getId()));
	}

	@Test
	@WithPersistedUser(permissions = UserPermission.CREATE_PROJECTS)
	void getProjectsForUser_shouldReturnListOfProjects_whenUserHasProjects() {
		String projectName1 = "Project 1";
		String projectName2 = "Project 2";

		var dto1 = new ProjectCreateDto(projectName1);
		var dto2 = new ProjectCreateDto(projectName2);
		projectService.createProject(dto1, this.user.getId());
		projectService.createProject(dto2, this.user.getId());

		var projects = projectService.getProjectsForUser(this.user.getId());
		assertThat(projects).hasSize(2);
		assertThat(projects).map(ProjectSummaryDto::name).contains(projectName1, projectName2);
	}

	@Test
	@WithPersistedUser
	void getProjectsForUser_shouldReturnEmptyList_whenUserDoesNotExist() {
		var projects = projectService.getProjectsForUser(UUID.randomUUID());
		assertThat(projects).isEmpty();
	}

}

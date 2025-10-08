package dev.erpix.easykan.server.domain.project.service;

import dev.erpix.easykan.server.constant.CacheKey;
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
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Tag(Category.INTEGRATION_TEST)
@IntegrationTest
public class ProjectServiceIT {

	@Autowired
	private ProjectService projectService;

	@Autowired
	private ProjectFactory projectFactory;

	@Autowired
	private ProjectMemberRepository projectMemberRepository;

	@MockitoSpyBean
	private ProjectRepository projectRepository;

	@MockitoSpyBean
	private ProjectUserViewRepository projectUserViewRepository;

	@Autowired
	private CacheManager cacheManager;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private UserService userService;

	private User user;

	@BeforeEach
	void setUp() {
		Objects.requireNonNull(cacheManager.getCache(CacheKey.PROJECTS_ID)).clear();
		Objects.requireNonNull(cacheManager.getCache(CacheKey.PROJECTS_FOR_USER_ID)).clear();
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
		assertThat(projectUserViewRepository.findNextPositionByUserId(user.getId())).isEqualTo(0);

		String projectName1 = "First Project";
		var firstDto = new ProjectCreateDto(projectName1);
		var firstProjectResult = projectService.createProject(firstDto, this.user.getId());

		assertThat(projectUserViewRepository.findNextPositionByUserId(user.getId())).isEqualTo(1);

		String projectName2 = "Second Project";
		var secondDto = new ProjectCreateDto(projectName2);
		var secondProjectResult = projectService.createProject(secondDto, this.user.getId());

		assertThat(projectUserViewRepository.findNextPositionByUserId(user.getId())).isEqualTo(2);

		List<ProjectUserView> userViews = projectUserViewRepository.findAllByUserWithDetails(this.user.getId());
		assertThat(userViews).hasSize(2);
		assertThat(userViews.get(0).getPosition()).isEqualTo(0);
		assertThat(userViews.get(1).getPosition()).isEqualTo(1);

		assertThat(firstProjectResult.name()).isEqualTo(projectName1);
		assertThat(secondProjectResult.name()).isEqualTo(projectName2);
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
	@WithPersistedUser(permissions = UserPermission.CREATE_PROJECTS)
	void createProject_shouldEvictCache_whenProjectIsCreated() {
		var dto = new ProjectCreateDto("Cached Project");

		// Load user's projects to cache them
		var userProjects = projectService.getProjectsForUser(this.user.getId());
		assertThat(userProjects).isEmpty();

		// Create a new project, which should evict the user's projects cache
		projectService.createProject(dto, this.user.getId());

		// Verify caches are evicted by checking the cache directly
		var userProjectsCache = cacheManager.getCache(CacheKey.PROJECTS_FOR_USER_ID);
		assertThat(userProjectsCache).isNotNull();
		assertThat(userProjectsCache.get(this.user.getId())).isNull();
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
	void deleteProject_shouldEvictCache_whenProjectIsDeleted() {
		var createDto = new ProjectCreateDto("Project to Cache");
		projectService.createProject(createDto, this.user.getId());

		Project savedProject = projectRepository.findAll().getFirst();
		UUID projectId = savedProject.getId();

		// Load project to cache it
		Project cachedProject = projectService.getProjectById(projectId);
		assertThat(cachedProject).isNotNull();

		// Load user's projects to cache them
		var userProjects = projectService.getProjectsForUser(this.user.getId());
		assertThat(userProjects).hasSize(1);

		// Delete the project, which should evict the caches
		projectService.deleteProject(projectId, this.user.getId());

		// Verify caches are evicted by checking the cache directly
		var projectCache = cacheManager.getCache(CacheKey.PROJECTS_ID);
		var userProjectsCache = cacheManager.getCache(CacheKey.PROJECTS_FOR_USER_ID);

		assertThat(projectCache).isNotNull();
		assertThat(userProjectsCache).isNotNull();

		assertThat(projectCache.get(projectId)).isNull();
		assertThat(userProjectsCache.get(this.user.getId())).isNull();
	}

	@Test
	@WithPersistedUser(permissions = UserPermission.CREATE_PROJECTS)
	void getProjectById_shouldReturnProject_whenProjectExists() {
		var createDto = new ProjectCreateDto("Existing Project");
		projectService.createProject(createDto, this.user.getId());

		Project savedProject = projectRepository.findAll().getFirst();
		UUID projectId = savedProject.getId();

		Project fetchedProject = projectService.getProjectById(projectId);
		assertThat(fetchedProject).isNotNull();
		assertThat(fetchedProject.getId()).isEqualTo(projectId);
		assertThat(fetchedProject.getName()).isEqualTo(createDto.name());
		assertThat(fetchedProject.getOwner().getId()).isEqualTo(this.user.getId());
	}

	@Test
	@WithPersistedUser
	void getProjectById_shouldThrowProjectNotFoundException_whenProjectDoesNotExist() {
		UUID nonExistentProjectId = UUID.randomUUID();
		assertThrows(ProjectNotFoundException.class, () -> projectService.getProjectById(nonExistentProjectId));
	}

	@Test
	@WithPersistedUser(permissions = UserPermission.CREATE_PROJECTS)
	void getProjectById_shouldUseCache_whenCalledSecondTime() {
		var createDto = new ProjectCreateDto("Cached Project");
		projectService.createProject(createDto, this.user.getId());

		Project savedProject = projectRepository.findAll().getFirst();
		UUID projectId = savedProject.getId();

		// First call - should hit the database
		Project firstCallProject = projectService.getProjectById(projectId);
		assertThat(firstCallProject).isNotNull();
		assertThat(firstCallProject.getId()).isEqualTo(projectId);

		// Clear the persistence context to ensure we are not getting a cached entity from
		// Hibernate
		entityManager.clear();

		// Second call - should use the cache
		Project secondCallProject = projectService.getProjectById(projectId);
		assertThat(secondCallProject).isNotNull();
		assertThat(secondCallProject.getId()).isEqualTo(projectId);

		// Verify that the repository method was only called once
		verify(projectRepository, times(1)).findById(projectId);
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

	@Test
	@WithPersistedUser
	void getProjectsForUser_shouldReturnEmptyList_whenUserHasNoProjects() {
		var projects = projectService.getProjectsForUser(this.user.getId());
		assertThat(projects).isEmpty();
	}

	@Test
	@WithPersistedUser(permissions = UserPermission.CREATE_PROJECTS)
	void getProjectsForUser_shouldUseCache_whenCalledSecondTime() {
		var createDto = new ProjectCreateDto("Cached User Projects");
		projectService.createProject(createDto, this.user.getId());

		// First call - should hit the database
		var firstCallProjects = projectService.getProjectsForUser(this.user.getId());
		assertThat(firstCallProjects).hasSize(1);

		// Clear the persistence context to ensure we are not getting cached entities from
		// Hibernate
		entityManager.clear();

		// Second call - should use the cache
		var secondCallProjects = projectService.getProjectsForUser(this.user.getId());
		assertThat(secondCallProjects).hasSize(1);

		// Verify that the repository method was only called once
		verify(projectUserViewRepository, times(1)).findAllByUserWithDetails(this.user.getId());
	}

}

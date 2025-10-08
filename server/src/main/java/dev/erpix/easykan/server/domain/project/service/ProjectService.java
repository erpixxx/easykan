package dev.erpix.easykan.server.domain.project.service;

import dev.erpix.easykan.server.constant.CacheKey;
import dev.erpix.easykan.server.domain.PermissionUtils;
import dev.erpix.easykan.server.domain.project.dto.ProjectCreateDto;
import dev.erpix.easykan.server.domain.project.dto.ProjectSummaryDto;
import dev.erpix.easykan.server.domain.project.factory.ProjectFactory;
import dev.erpix.easykan.server.domain.project.model.*;
import dev.erpix.easykan.server.domain.project.repository.ProjectRepository;
import dev.erpix.easykan.server.domain.project.repository.ProjectUserViewRepository;
import dev.erpix.easykan.server.domain.user.model.User;
import dev.erpix.easykan.server.domain.user.model.UserPermission;
import dev.erpix.easykan.server.domain.user.security.RequireUserPermission;
import dev.erpix.easykan.server.domain.user.service.UserService;
import dev.erpix.easykan.server.exception.project.ProjectNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectService {

	private final ProjectFactory projectFactory;

	private final ProjectRepository projectRepository;

	private final ProjectUserViewRepository projectUserViewRepository;

	private final CacheManager cacheManager;

	private final EntityManager entityManager;

	private final UserService userService;

	@Transactional
	@CacheEvict(cacheNames = CacheKey.PROJECTS_FOR_USER_ID, key = "#ownerId")
	@RequireUserPermission(UserPermission.CREATE_PROJECTS)
	public ProjectSummaryDto createProject(ProjectCreateDto dto, UUID ownerId) {
		User owner = entityManager.merge(userService.getById(ownerId));

		int nextPosition = projectUserViewRepository.findNextPositionByUserId(owner.getId());
		Project project = projectFactory.create(dto.name(), owner, nextPosition);

		Project savedProject = projectRepository.save(project);

		return ProjectSummaryDto.fromProject(savedProject);
	}

	public void deleteProject(UUID projectId, UUID userId) {
		Project project = projectRepository.findById(projectId)
			.orElseThrow(() -> ProjectNotFoundException.byId(projectId));
		User user = userService.getById(userId);

		boolean isAdmin = PermissionUtils.hasAnyPermission(user.getPermissions(), UserPermission.ADMIN,
				UserPermission.MANAGE_PROJECTS);
		boolean isOwner = project.getOwner().getId().equals(userId);

		if (!isAdmin && !isOwner) {
			throw new AccessDeniedException("You do not have permission to delete this project");
		}

		// Evict caches
		Cache projectCache = cacheManager.getCache(CacheKey.PROJECTS_ID);
		if (projectCache != null) {
			projectCache.evict(projectId);
		}

		Cache userProjectsCache = cacheManager.getCache(CacheKey.PROJECTS_FOR_USER_ID);
		if (userProjectsCache != null) {
			userProjectsCache.evict(project.getOwner().getId());
		}

		projectRepository.delete(project);
	}

	@Cacheable(cacheNames = CacheKey.PROJECTS_ID, key = "#projectId")
	public Project getProjectById(UUID projectId) {
		return projectRepository.findById(projectId).orElseThrow(() -> ProjectNotFoundException.byId(projectId));
	}

	@Cacheable(cacheNames = CacheKey.PROJECTS_FOR_USER_ID, key = "#userId")
	public List<ProjectSummaryDto> getProjectsForUser(UUID userId) {
		return projectUserViewRepository.findAllByUserWithDetails(userId)
			.stream()
			.map(puv -> ProjectSummaryDto.fromProject(puv.getProject()))
			.toList();
	}

	@RequireUserPermission(UserPermission.MANAGE_PROJECTS)
	public Page<ProjectSummaryDto> getAllProjects(Pageable pageable) {
		return projectRepository.findAll(pageable).map(ProjectSummaryDto::fromProject);
	}

}

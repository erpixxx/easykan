package dev.erpix.easykan.server.domain.project.service;

import dev.erpix.easykan.server.domain.project.dto.ProjectCreateDto;
import dev.erpix.easykan.server.domain.project.dto.ProjectSummaryDto;
import dev.erpix.easykan.server.domain.project.factory.ProjectFactory;
import dev.erpix.easykan.server.domain.project.model.*;
import dev.erpix.easykan.server.domain.project.repository.ProjectMemberRepository;
import dev.erpix.easykan.server.domain.project.repository.ProjectRepository;
import dev.erpix.easykan.server.domain.project.repository.ProjectUserViewRepository;
import dev.erpix.easykan.server.domain.user.model.User;
import dev.erpix.easykan.server.domain.user.model.UserPermission;
import dev.erpix.easykan.server.domain.user.security.RequireUserPermission;
import dev.erpix.easykan.server.domain.user.service.UserService;
import dev.erpix.easykan.server.exception.project.ProjectNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectService {

	private final ProjectFactory projectFactory;

	private final ProjectMemberRepository projectMemberRepository;

	private final ProjectRepository projectRepository;

	private final ProjectUserViewRepository projectUserViewRepository;

	private final UserService userService;

	@Transactional
	@RequireUserPermission(UserPermission.CREATE_PROJECTS)
	public ProjectSummaryDto createProject(ProjectCreateDto dto, UUID ownerId) {
		User owner = userService.getById(ownerId);

		int nextPosition = projectUserViewRepository.findNextPositionByUserId(owner.getId());
		Project project = projectFactory.create(dto.name(), owner, nextPosition);

		Project savedProject = projectRepository.save(project);

		return ProjectSummaryDto.fromProject(savedProject);
	}

	public void deleteProject(UUID projectId, UUID userId) {
		Project project = projectRepository.findById(projectId)
			.orElseThrow(() -> ProjectNotFoundException.byId(projectId));
		User user = userService.getById(userId);

		boolean isAdmin = UserPermission.hasPermissionOrAdmin(user, UserPermission.MANAGE_PROJECTS);
		boolean isOwner = project.getOwner().getId().equals(userId);

		if (!isAdmin && !isOwner) {
			throw new AccessDeniedException("You do not have permission to delete this project");
		}

		projectRepository.delete(project);
	}

	public List<ProjectSummaryDto> getProjectsForUser(UUID userId) {
		return projectUserViewRepository.findAllByUserWithDetails(userId)
			.stream()
			.map(puv -> ProjectSummaryDto.fromProject(puv.getProject()))
			.toList();
	}

}

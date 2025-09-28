package dev.erpix.easykan.server.domain.project.service;

import dev.erpix.easykan.server.domain.project.dto.ProjectCreateDto;
import dev.erpix.easykan.server.domain.project.dto.ProjectSummaryDto;
import dev.erpix.easykan.server.domain.project.model.Project;
import dev.erpix.easykan.server.domain.project.model.ProjectMember;
import dev.erpix.easykan.server.domain.project.model.ProjectUserView;
import dev.erpix.easykan.server.domain.project.repository.ProjectMemberRepository;
import dev.erpix.easykan.server.domain.project.repository.ProjectRepository;
import dev.erpix.easykan.server.domain.project.repository.ProjectUserViewRepository;
import dev.erpix.easykan.server.domain.user.model.User;
import dev.erpix.easykan.server.domain.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectService {

	private final ProjectMemberRepository projectMemberRepository;

	private final ProjectRepository projectRepository;

	private final ProjectUserViewRepository projectUserViewRepository;

	private final UserService userService;

	@Transactional
	public ProjectSummaryDto createProject(ProjectCreateDto dto, UUID ownerId) {
		User owner = userService.getById(ownerId);
		Project project = Project.builder().name(dto.name()).owner(owner).build();
		ProjectMember member = ProjectMember.builder().project(project).user(owner).permissions(0L).build();

		int nextPosition = (int) projectUserViewRepository.countByUserId(ownerId);
		ProjectUserView userView = ProjectUserView.builder()
			.project(project)
			.user(owner)
			.position(nextPosition)
			.build();

		project.getMembers().add(member);
		project.getUserViews().add(userView);

		Project createdProject = projectRepository.save(project);
		return ProjectSummaryDto.fromProject(createdProject, nextPosition);
	}

	public List<ProjectSummaryDto> getProjectsForUser(UUID userId) {
		return projectUserViewRepository.findAllByUserWithDetails(userId)
			.stream()
			.map(puv -> ProjectSummaryDto.fromProject(puv.getProject(), puv.getPosition()))
			.toList();
	}

}

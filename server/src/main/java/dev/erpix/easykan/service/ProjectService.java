package dev.erpix.easykan.service;

import dev.erpix.easykan.CurrentUser;
import dev.erpix.easykan.model.project.dto.ProjectResponseDTO;
import dev.erpix.easykan.model.project.EKProject;
import dev.erpix.easykan.model.project.dto.ProjectCreateRequestDTO;
import dev.erpix.easykan.model.user.EKUser;
import dev.erpix.easykan.model.project.EKUserProject;
import dev.erpix.easykan.repository.ProjectRepository;
import dev.erpix.easykan.repository.UserProjectsRepository;
import dev.erpix.easykan.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final UserProjectsRepository userProjectsRepository;
    private final CurrentUser currentUser;

    public ProjectResponseDTO toDto(@NotNull EKProject project) {
        return new ProjectResponseDTO(
                project.getId(),
                project.getName(),
                project.getOwner().getId()
        );
    }

    public @NotNull EKProject create(@NotNull UUID userId, @NotNull ProjectCreateRequestDTO dto) {
        EKUser user = userService.getById(userId).orElseThrow(
                () -> new IllegalArgumentException("User not found with ID: " + userId));
        EKProject project = dto.toProject(user);
        projectRepository.save(project);

        EKUserProject userProject = EKUserProject.builder()
                .id(new EKUserProject.Id(userId, project.getId()))
                .permissions(1)
                .project(project)
                .user(user)
                .build();
        userProjectsRepository.save(userProject);
        return project;
    }

    public boolean deleteProject(@NotNull UUID projectId) {
        EKProject project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found. ID: " + projectId));

        UUID userId = currentUser.getId();
        if (!project.getOwner().getId().equals(userId)) {
            if (userRepository.findById(userId).stream().noneMatch(EKUser::isAdmin))
                return false;
        }

        // Delete the project itself
        projectRepository.delete(project);
        return true;
    }

    public @NotNull List<EKProject> getUserProjects(@NotNull UUID userId) {
        return projectRepository.findByOwner_Id(userId);
    }

}

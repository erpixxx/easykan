package dev.erpix.easykan.server.domain.project.service;

import dev.erpix.easykan.server.constant.CacheKey;
import dev.erpix.easykan.server.domain.user.util.CurrentUser;
import dev.erpix.easykan.server.exception.ProjectNotFoundException;
import dev.erpix.easykan.server.domain.project.dto.ProjectCreateRequestDto;
import dev.erpix.easykan.server.domain.project.model.EKProject;
import dev.erpix.easykan.server.domain.user.model.EKUser;
import dev.erpix.easykan.server.domain.project.model.EKUserProject;
import dev.erpix.easykan.server.domain.project.repository.ProjectRepository;
import dev.erpix.easykan.server.domain.project.repository.UserProjectsRepository;
import dev.erpix.easykan.server.domain.user.repository.UserRepository;
import dev.erpix.easykan.server.domain.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final UserProjectsRepository userProjectsRepository;
    private final CurrentUser currentUser;

    @Cacheable(value = CacheKey.PROJECTS, key = "#projectId")
    public @NotNull EKProject getById(@NotNull UUID projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> ProjectNotFoundException.byId(projectId));
    }

    public @NotNull Set<EKProject> getUserProjects(@NotNull UUID userId) {
        return projectRepository.findByOwner_Id(userId);
    }

    @Transactional
    public @NotNull EKProject createProject(@NotNull UUID userId, @NotNull ProjectCreateRequestDto dto) {
        EKUser user = userService.getById(userId);

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

    @Transactional
    @CacheEvict(value = CacheKey.PROJECTS, key = "#projectId")
    @PreAuthorize("hasRole('ADMIN') or @projectPermissionEvaluator.isOwner(#projectId)")
    public void deleteProject(@NotNull UUID projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw ProjectNotFoundException.byId(projectId);
        }
        projectRepository.deleteById(projectId);
    }

}

package dev.erpix.easykan.service;

import dev.erpix.easykan.constant.CacheKey;
import dev.erpix.easykan.CurrentUser;
import dev.erpix.easykan.exception.ProjectNotFoundException;
import dev.erpix.easykan.model.project.dto.ProjectCreateRequestDto;
import dev.erpix.easykan.model.project.EKProject;
import dev.erpix.easykan.model.user.EKUser;
import dev.erpix.easykan.model.project.EKUserProject;
import dev.erpix.easykan.repository.ProjectRepository;
import dev.erpix.easykan.repository.UserProjectsRepository;
import dev.erpix.easykan.repository.UserRepository;
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
    @PreAuthorize("hasRole('ADMIN') or @projectSecurity.isOwner(#projectId)")
    public void deleteProject(@NotNull UUID projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw ProjectNotFoundException.byId(projectId);
        }
        projectRepository.deleteById(projectId);
    }



    public void e() {
        Set<EKUserProject> byIdUserId = userProjectsRepository.findById_UserId(null);
        byIdUserId.forEach(proj -> {

        });
    }

}

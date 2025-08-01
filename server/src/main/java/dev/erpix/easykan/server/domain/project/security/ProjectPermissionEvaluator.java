package dev.erpix.easykan.server.domain.project.security;

import dev.erpix.easykan.server.domain.user.util.CurrentUser;
import dev.erpix.easykan.server.domain.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("projectPermissionEvaluator")
@RequiredArgsConstructor
public class ProjectPermissionEvaluator {

    private final ProjectRepository projectRepository;
    private final CurrentUser currentUser;

    public boolean isOwner(UUID projectId) {
        UUID userId = currentUser.getId();
        return projectRepository.findById(projectId)
                .map(proj -> proj.getOwner().getId().equals(userId))
                .orElse(false);
    }

}

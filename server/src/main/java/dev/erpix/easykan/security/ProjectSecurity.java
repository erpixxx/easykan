package dev.erpix.easykan.security;

import dev.erpix.easykan.CurrentUser;
import dev.erpix.easykan.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("projectSecurity")
@RequiredArgsConstructor
public class ProjectSecurity {

    private final ProjectRepository projectRepository;
    private final CurrentUser currentUser;

    public boolean isOwner(UUID projectId) {
        UUID userId = currentUser.getId();
        return projectRepository.findById(projectId)
                .map(proj -> proj.getOwner().getId().equals(userId))
                .orElse(false);
    }

}

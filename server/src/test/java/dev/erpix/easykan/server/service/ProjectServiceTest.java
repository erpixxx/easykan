package dev.erpix.easykan.server.service;

import dev.erpix.easykan.server.domain.project.model.Project;
import dev.erpix.easykan.server.domain.project.repository.ProjectMemberRepository;
import dev.erpix.easykan.server.domain.project.repository.ProjectRepository;
import dev.erpix.easykan.server.domain.project.service.ProjectService;
import dev.erpix.easykan.server.domain.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMemberRepository projectMemberRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ProjectService projectService;

    @Test
    void getById_shouldReturnProject_whenProjectExists() {
        UUID projectId = UUID.randomUUID();
        Project project = Project.builder()
                .id(projectId)
                .build();

        when(projectRepository.findById(projectId))
                .thenReturn(Optional.of(project));

        Project result = projectService.getById(projectId);

        assertThat(result).isEqualTo(project);
        verify(projectRepository, times(1)).findById(projectId);
    }

    @Test
    void getById_shouldThrowException_whenProjectDoesNotExist() {
        //
    }

}

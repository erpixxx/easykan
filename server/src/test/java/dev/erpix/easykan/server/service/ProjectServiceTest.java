package dev.erpix.easykan.server.service;

import dev.erpix.easykan.server.domain.project.dto.ProjectCreateDto;
import dev.erpix.easykan.server.domain.project.dto.ProjectSummaryDto;
import dev.erpix.easykan.server.domain.project.model.Project;
import dev.erpix.easykan.server.domain.project.model.ProjectUserView;
import dev.erpix.easykan.server.domain.project.repository.ProjectMemberRepository;
import dev.erpix.easykan.server.domain.project.repository.ProjectRepository;
import dev.erpix.easykan.server.domain.project.repository.ProjectUserViewRepository;
import dev.erpix.easykan.server.domain.project.service.ProjectService;
import dev.erpix.easykan.server.domain.user.model.User;
import dev.erpix.easykan.server.domain.user.service.UserService;
import dev.erpix.easykan.server.exception.user.UserNotFoundException;
import dev.erpix.easykan.server.testsupport.Category;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Tag(Category.UNIT_TEST)
@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @InjectMocks
    private ProjectService projectService;

    @Mock
    private ProjectMemberRepository projectMemberRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectUserViewRepository projectUserViewRepository;

    @Mock
    private UserService userService;

    ArgumentCaptor<Project> projectCaptor = ArgumentCaptor.forClass(Project.class);

    @Test
    void createProject_shouldCreateProjectAndSetOwnerAsFirstMember() {
        String projectName = "New Project";
        ProjectCreateDto dto = new ProjectCreateDto(projectName);
        UUID ownerId = UUID.randomUUID();

        User owner = User.builder()
                .id(ownerId)
                .login("owner")
                .build();
        Project project = Project.builder()
                .id(UUID.randomUUID())
                .name(dto.name())
                .owner(owner)
                .build();

        when(userService.getById(ownerId))
                .thenReturn(owner);
        when(projectUserViewRepository.countByUserId(ownerId))
                .thenReturn(0L);
        when(projectRepository.save(any(Project.class)))
                .thenReturn(project);

        ProjectSummaryDto resultDto = projectService.createProject(dto, ownerId);

        verify(projectRepository).save(projectCaptor.capture());
        Project savedProject = projectCaptor.getValue();

        assertThat(savedProject.getName()).isEqualTo(projectName);
        assertThat(savedProject.getOwner()).isEqualTo(owner);

        assertThat(savedProject.getMembers()).hasSize(1);
        assertThat(savedProject.getMembers().iterator().next().getUser()).isEqualTo(owner);

        assertThat(savedProject.getUserViews()).hasSize(1);
        assertThat(savedProject.getUserViews().iterator().next().getUser()).isEqualTo(owner);
        assertThat(savedProject.getUserViews().iterator().next().getPosition()).isZero();

        assertThat(resultDto.name()).isEqualTo(projectName);
        assertThat(resultDto.position()).isZero();
    }

    @Test
    void createProject_shouldSetCorrectPosition_whenUserHasExistingProjects() {
        String projectName = "Another Project";
        ProjectCreateDto dto = new ProjectCreateDto(projectName);
        UUID ownerId = UUID.randomUUID();

        User owner = User.builder()
                .id(ownerId)
                .login("owner")
                .build();
        Project project = Project.builder()
                .id(UUID.randomUUID())
                .name(dto.name())
                .owner(owner)
                .build();

        when(userService.getById(ownerId))
                .thenReturn(owner);
        long projectCount = 3L;
        when(projectUserViewRepository.countByUserId(ownerId))
                .thenReturn(projectCount);
        when(projectRepository.save(any(Project.class)))
                .thenReturn(project);

        ProjectSummaryDto resultDto = projectService.createProject(dto, ownerId);

        verify(projectRepository).save(projectCaptor.capture());
        Project savedProject = projectCaptor.getValue();

        assertThat(savedProject.getUserViews()).hasSize(1);
        assertThat(savedProject.getUserViews().iterator().next().getUser()).isEqualTo(owner);
        assertThat(savedProject.getUserViews().iterator().next().getPosition()).isEqualTo(3);

        assertThat(resultDto.name()).isEqualTo(projectName);
        assertThat(resultDto.position()).isEqualTo(projectCount);
    }

    @Test
    void createProject_shouldThrowException_whenUserDoesNotExist() {
        ProjectCreateDto dto = new ProjectCreateDto("New Project");
        UUID ownerId = UUID.randomUUID();

        when(userService.getById(ownerId))
                .thenThrow(UserNotFoundException.class);

        assertThrows(UserNotFoundException.class, () -> projectService.createProject(dto, ownerId));
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void getProjectsForUser_shouldReturnProjectSummaries_whenUserHasProjects() {
        UUID ownerId = UUID.randomUUID();
        User owner = User.builder()
                .id(ownerId)
                .build();

        String projectName1 = "First";
        String projectName2 = "Second";
        Project project1 = Project.builder().id(UUID.randomUUID()).name(projectName1).owner(owner).build();
        Project project2 = Project.builder().id(UUID.randomUUID()).name(projectName2).owner(owner).build();

        ProjectUserView puv1 = ProjectUserView.builder().project(project1).user(owner).position(0).build();
        ProjectUserView puv2 = ProjectUserView.builder().project(project2).user(owner).position(1).build();

        List<ProjectUserView> userViews = List.of(puv1, puv2);

        when(projectUserViewRepository.findAllByUserWithDetails(ownerId)).thenReturn(userViews);

        List<ProjectSummaryDto> result = projectService.getProjectsForUser(ownerId);

        verify(projectUserViewRepository, times(1)).findAllByUserWithDetails(ownerId);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);

        assertThat(result.get(0).name()).isEqualTo(projectName1);
        assertThat(result.get(0).position()).isZero();

        assertThat(result.get(1).name()).isEqualTo(projectName2);
        assertThat(result.get(1).position()).isEqualTo(1);
    }

    @Test
    void getProjectsForUser_shouldReturnEmptyList_whenUserHasNoProjects() {
        UUID userId = UUID.randomUUID();

        when(projectUserViewRepository.findAllByUserWithDetails(userId))
                .thenReturn(Collections.emptyList());

        List<ProjectSummaryDto> result = projectService.getProjectsForUser(userId);

        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        verify(projectUserViewRepository).findAllByUserWithDetails(userId);
    }

}

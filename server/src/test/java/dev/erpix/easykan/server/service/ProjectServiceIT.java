package dev.erpix.easykan.server.service;

import dev.erpix.easykan.server.domain.project.dto.ProjectCreateDto;
import dev.erpix.easykan.server.domain.project.repository.ProjectMemberRepository;
import dev.erpix.easykan.server.domain.project.repository.ProjectRepository;
import dev.erpix.easykan.server.domain.project.repository.ProjectUserViewRepository;
import dev.erpix.easykan.server.domain.project.service.ProjectService;
import dev.erpix.easykan.server.domain.user.model.User;
import dev.erpix.easykan.server.domain.user.service.UserService;
import dev.erpix.easykan.server.testsupport.Category;
import dev.erpix.easykan.server.testsupport.annotation.IntegrationTest;
import dev.erpix.easykan.server.testsupport.annotation.WithPersistedUser;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@Tag(Category.INTEGRATION_TEST)
@IntegrationTest
public class ProjectServiceIT {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectUserViewRepository projectUserViewRepository;

    @Autowired
    private UserService userService;

    @Test
    @WithPersistedUser
    void createProject_shouldCreateProjectAndSetOwnerAsFirstMember() {
        assertThat(projectRepository.countByOwner_Login(WithPersistedUser.Default.LOGIN))
                .isEqualTo(0L);

        User user = userService.getByLogin(WithPersistedUser.Default.LOGIN);

        var dto = new ProjectCreateDto("New Project");
        var createdProject = projectService.createProject(dto, user.getId());

        assertThat(createdProject.name()).isEqualTo(dto.name());
        assertThat(createdProject.position()).isEqualTo(0);
        assertThat(createdProject.owner().login()).isEqualTo(WithPersistedUser.Default.LOGIN);
    }

}

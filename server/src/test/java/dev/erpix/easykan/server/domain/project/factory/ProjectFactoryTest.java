package dev.erpix.easykan.server.domain.project.factory;

import dev.erpix.easykan.server.domain.project.model.Project;
import dev.erpix.easykan.server.domain.project.model.ProjectMember;
import dev.erpix.easykan.server.domain.project.model.ProjectUserView;
import dev.erpix.easykan.server.domain.user.model.User;
import dev.erpix.easykan.server.testsupport.Category;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Tag(Category.UNIT_TEST)
public class ProjectFactoryTest {

	private final ProjectFactory projectFactory = new ProjectFactory();

	@Test
	void create_shouldReturnCorrectlyConfiguredProject() {
		String projectName = "New Project";
		int position = 10;
		User owner = User.builder().build();

		Project createdProject = projectFactory.create(projectName, owner, position);

		// Verify the created project
		assertThat(createdProject).isNotNull();
		assertThat(createdProject.getName()).isEqualTo(projectName);
		assertThat(createdProject.getOwner()).isEqualTo(owner);

		// Verify the project member
		assertThat(createdProject.getMembers()).hasSize(1);
		ProjectMember member = createdProject.getMembers().iterator().next();
		assertThat(member.getUser()).isEqualTo(owner);
		assertThat(member.getProject()).isEqualTo(createdProject);
		assertThat(member.getPermissions()).isEqualTo(0L);

		// Verify the project user view
		assertThat(createdProject.getUserViews()).hasSize(1);
		ProjectUserView userView = createdProject.getUserViews().iterator().next();
		assertThat(userView.getUser()).isEqualTo(owner);
		assertThat(userView.getProject()).isEqualTo(createdProject);
		assertThat(userView.getPosition()).isEqualTo(position);
	}

}

package dev.erpix.easykan.server.domain.project.factory;

import dev.erpix.easykan.server.domain.project.model.Project;
import dev.erpix.easykan.server.domain.project.model.ProjectMember;
import dev.erpix.easykan.server.domain.project.model.ProjectUserView;
import dev.erpix.easykan.server.domain.user.model.User;
import org.springframework.stereotype.Component;

@Component
public class ProjectFactory {

	public Project create(String name, User owner, int nextPositionForOwner) {
		Project project = Project.builder().name(name).owner(owner).build();

		ProjectMember member = ProjectMember.builder().project(project).user(owner).permissions(0L).build();
		project.getMembers().add(member);

		ProjectUserView userView = ProjectUserView.builder()
			.project(project)
			.user(owner)
			.position(nextPositionForOwner)
			.build();
		project.getUserViews().add(userView);

		return project;
	}

}

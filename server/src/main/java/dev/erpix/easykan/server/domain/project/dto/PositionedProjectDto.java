package dev.erpix.easykan.server.domain.project.dto;

import dev.erpix.easykan.server.domain.project.model.Project;
import dev.erpix.easykan.server.domain.project.model.ProjectUserView;
import dev.erpix.easykan.server.domain.user.dto.UserSummaryDto;
import dev.erpix.easykan.server.domain.user.model.User;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * A DTO representing a Project with its members and the position of the project in a
 * specific User's view.
 *
 * @param name the name of the project.
 * @param members the members of the project.
 * @param position the position of the project in the user's view.
 */
public record PositionedProjectDto(String name, Set<UserSummaryDto> members, int position) {

	/**
	 * Creates a DTO from a Project for a specific User's view.
	 * @param project the project entity.
	 * @param user the user for whom the project is being viewed.
	 * @return the DTO.
	 */
	public static PositionedProjectDto fromProjectFor(Project project, User user) {
		int position = project.getUserViews()
			.stream()
			.filter(puv -> puv.getUser().equals(user))
			.findFirst()
			.map(ProjectUserView::getPosition)
			.orElse(0);

		Set<UserSummaryDto> memberDtos = project.getMembers()
			.stream()
			.map(pm -> UserSummaryDto.fromUser(pm.getUser()))
			.collect(Collectors.toSet());

		return new PositionedProjectDto(project.getName(), memberDtos, position);
	}

}

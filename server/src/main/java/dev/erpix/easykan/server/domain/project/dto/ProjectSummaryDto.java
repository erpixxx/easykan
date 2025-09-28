package dev.erpix.easykan.server.domain.project.dto;

import dev.erpix.easykan.server.domain.project.model.Project;
import dev.erpix.easykan.server.domain.user.dto.UserSummaryDto;

import java.util.UUID;

public record ProjectSummaryDto(UUID id, String name, int position, UserSummaryDto owner) {

	public static ProjectSummaryDto fromProject(Project project, int position) {
		return new ProjectSummaryDto(project.getId(), project.getName(), position,
				UserSummaryDto.fromUser(project.getOwner()));

	}

}

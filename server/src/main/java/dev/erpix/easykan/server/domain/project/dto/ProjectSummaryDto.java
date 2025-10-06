package dev.erpix.easykan.server.domain.project.dto;

import dev.erpix.easykan.server.domain.project.model.Project;
import dev.erpix.easykan.server.domain.user.dto.UserSummaryDto;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record ProjectSummaryDto(UUID id, String name, Set<UserSummaryDto> members) {

	public static ProjectSummaryDto fromProject(Project project) {
		return new ProjectSummaryDto(project.getId(), project.getName(),
				project.getMembers()
					.stream()
					.map(pm -> UserSummaryDto.fromUser(pm.getUser()))
					.collect(Collectors.toSet()));
	}

}

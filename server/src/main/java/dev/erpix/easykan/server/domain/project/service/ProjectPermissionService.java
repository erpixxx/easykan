package dev.erpix.easykan.server.domain.project.service;

import dev.erpix.easykan.server.domain.project.model.ProjectPermission;
import dev.erpix.easykan.server.domain.project.repository.ProjectMemberRepository;
import dev.erpix.easykan.server.exception.project.UserIsNotProjectMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectPermissionService {

	private final ProjectMemberRepository projectMemberRepository;

	public long getPermissionMaskForUserInProject(UUID projectId, UUID userId) {
		return getPermissionByUserIdAndProjectId(userId, projectId);
	}

	public List<ProjectPermission> getPermissionsForUserInProject(UUID projectId, UUID userId) {
		long permissionMask = getPermissionByUserIdAndProjectId(userId, projectId);

		return ProjectPermission.fromValue(permissionMask);
	}

	private long getPermissionByUserIdAndProjectId(UUID userId, UUID projectId) {
		return projectMemberRepository.findPermissionByUserIdAndProjectId(userId, projectId)
			.orElseThrow(() -> UserIsNotProjectMember.byUserIdAndProjectId(userId, projectId));
	}

}

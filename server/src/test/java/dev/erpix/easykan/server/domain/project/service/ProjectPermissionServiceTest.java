package dev.erpix.easykan.server.domain.project.service;

import dev.erpix.easykan.server.domain.PermissionUtils;
import dev.erpix.easykan.server.domain.project.model.ProjectPermission;
import dev.erpix.easykan.server.domain.project.repository.ProjectMemberRepository;
import dev.erpix.easykan.server.exception.project.UserIsNotProjectMember;
import dev.erpix.easykan.server.testsupport.Category;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@Tag(Category.UNIT_TEST)
@ExtendWith(MockitoExtension.class)
public class ProjectPermissionServiceTest {

	@InjectMocks
	private ProjectPermissionService projectPermissionService;

	@Mock
	private ProjectMemberRepository projectMemberRepository;

	@Test
	void getPermissionMaskForUserInProject_shouldReturnCorrectPermissionMask() {
		UUID projectId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();

		long expectedPermissionMask = PermissionUtils.toValue(ProjectPermission.VIEWER, ProjectPermission.MANAGE_LABELS,
				ProjectPermission.MANAGE_CARDS);

		when(projectMemberRepository.findPermissionByUserIdAndProjectId(userId, projectId))
			.thenReturn(Optional.of(expectedPermissionMask));

		long actualMask = projectPermissionService.getPermissionMaskForUserInProject(projectId, userId);

		assertThat(actualMask).isEqualTo(expectedPermissionMask);
	}

	@Test
	void getPermissionMaskForUserInProject_shouldThrowUserIsNotProjectMember_whenUserIsNotProjectMember() {
		UUID projectId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();

		when(projectMemberRepository.findPermissionByUserIdAndProjectId(userId, projectId))
			.thenReturn(Optional.empty());

		assertThrows(UserIsNotProjectMember.class,
				() -> projectPermissionService.getPermissionMaskForUserInProject(projectId, userId));
	}

	@Test
	void getPermissionsForUserInProject_shouldReturnCorrectPermissions() {
		UUID projectId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();

		long expectedPermissionMask = PermissionUtils.toValue(ProjectPermission.VIEWER, ProjectPermission.MANAGE_LABELS,
				ProjectPermission.MANAGE_CARDS);

		when(projectMemberRepository.findPermissionByUserIdAndProjectId(userId, projectId))
			.thenReturn(Optional.of(expectedPermissionMask));

		var actualPermissions = projectPermissionService.getPermissionsForUserInProject(projectId, userId);

		assertThat(actualPermissions).containsExactlyInAnyOrder(ProjectPermission.VIEWER,
				ProjectPermission.MANAGE_LABELS, ProjectPermission.MANAGE_CARDS);
	}

	@Test
	void getPermissionsForUserInProject_shouldThrowUserIsNotProjectMember_whenUserIsNotProjectMember() {
		UUID projectId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();

		when(projectMemberRepository.findPermissionByUserIdAndProjectId(userId, projectId))
			.thenReturn(Optional.empty());

		assertThrows(UserIsNotProjectMember.class,
				() -> projectPermissionService.getPermissionsForUserInProject(projectId, userId));
	}

}

package dev.erpix.easykan.server.domain.project.model;

import dev.erpix.easykan.server.domain.PermissionMask;
import dev.erpix.easykan.server.domain.PermissionUtils;
import lombok.Getter;

import java.util.List;

@Getter
public enum ProjectPermission implements PermissionMask {

	VIEWER(1L), //
	OWNER(1L << 1), //
	FULL_ACCESS(1L << 2), //
	MANAGE_MEMBERS(1L << 3), //
	MANAGE_BOARDS(1L << 4), //
	MANAGE_LABELS(1L << 5), //
	MANAGE_COLUMNS(1L << 6), //
	MANAGE_CARDS(1L << 7); //

	private final long value;

	ProjectPermission(long value) {
		this.value = value;
	}

	public static List<ProjectPermission> fromValue(long value) {
		return PermissionUtils.fromValue(value, ProjectPermission.class);
	}

}

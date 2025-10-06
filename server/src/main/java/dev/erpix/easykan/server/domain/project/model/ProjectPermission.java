package dev.erpix.easykan.server.domain.project.model;

import dev.erpix.easykan.server.domain.user.model.User;
import dev.erpix.easykan.server.exception.user.PermissionMaskValidationException;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Getter
public enum ProjectPermission {

	BLANK(0L), //
	VIEWER(1L), //
	OWNER(1L << 1), //
	FULL_ACCESS(1L << 2), //
	MANAGE_MEMBERS(1L << 3), //
	MANAGE_LABELS(1L << 4), //
	MANAGE_COLUMNS(1L << 5), //
	MANAGE_CARDS(1L << 6); //

	public static final long ALL_PERMISSIONS_MASK = Arrays.stream(values())
		.mapToLong(ProjectPermission::getValue)
		.reduce(0, (a, b) -> a | b);

	private final long value;

	ProjectPermission(long value) {
		this.value = value;
	}

	public static List<ProjectPermission> fromValue(long value) {
		return Stream.of(ProjectPermission.values())
			.filter(permission -> (permission.value & value) == permission.value)
			.toList();
	}

	public static long toValue(ProjectPermission... permissions) {
		return Arrays.stream(permissions).mapToLong(ProjectPermission::getValue).reduce(0, (a, b) -> a | b);
	}

	public static boolean hasPermission(User user, ProjectPermission permission) {
		return (user.getPermissions() & permission.getValue()) == permission.getValue();
	}

	public static void validate(long permissions) throws PermissionMaskValidationException {
		if (permissions < 0) {
			throw new PermissionMaskValidationException("Permissions value cannot be negative");
		}
		if ((permissions & ~ProjectPermission.ALL_PERMISSIONS_MASK) != 0) {
			throw new PermissionMaskValidationException("Invalid permissions value: " + permissions);
		}
	}

}

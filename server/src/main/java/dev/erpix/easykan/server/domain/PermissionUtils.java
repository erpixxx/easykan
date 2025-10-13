package dev.erpix.easykan.server.domain;

import dev.erpix.easykan.server.exception.user.PermissionMaskValidationException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public final class PermissionUtils {

	private PermissionUtils() {
	}

	public static <T extends Enum<T> & PermissionMask> List<T> fromValue(long value, Class<T> enumType) {
		return Stream.of(enumType.getEnumConstants())
			.filter(permission -> (value & permission.getValue()) == permission.getValue())
			.toList();
	}

	@SafeVarargs
	public static <T extends PermissionMask> boolean hasPermission(long permissionsMask, T... requiredPermissions) {
		long requiredMask = toValue(requiredPermissions);
		return (permissionsMask & requiredMask) == requiredMask;
	}

	@SafeVarargs
	public static <T extends PermissionMask> boolean hasAnyPermission(long permissionsMask, T... requiredPermissions) {
		long requiredMask = toValue(requiredPermissions);
		return (permissionsMask & requiredMask) != 0;
	}

	@SafeVarargs
	public static <T extends PermissionMask> long toValue(T... permissions) {
		return Arrays.stream(permissions).mapToLong(PermissionMask::getValue).reduce(0, (a, b) -> a | b);
	}

	public static <T extends Enum<T> & PermissionMask> void validate(long permissions, Class<T> enumType)
			throws PermissionMaskValidationException {
		if (permissions < 0) {
			throw new PermissionMaskValidationException("Permissions value cannot be negative: " + permissions);
		}

		long allPermissionsMask = Arrays.stream(enumType.getEnumConstants())
			.mapToLong(PermissionMask::getValue)
			.reduce(0, (a, b) -> a | b);

		if ((permissions & ~allPermissionsMask) != 0) {
			throw new PermissionMaskValidationException("Invalid permissions value: " + permissions);
		}
	}

}

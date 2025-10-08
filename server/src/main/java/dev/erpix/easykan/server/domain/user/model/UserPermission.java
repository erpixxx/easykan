package dev.erpix.easykan.server.domain.user.model;

import dev.erpix.easykan.server.domain.PermissionMask;
import dev.erpix.easykan.server.domain.PermissionUtils;

import java.util.List;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

@Getter
public enum UserPermission implements PermissionMask, GrantedAuthority {

	DEFAULT_PERMISSIONS(0L), //
	ADMIN(1L), //
	MANAGE_PROJECTS(1L << 1), //
	CREATE_PROJECTS(1L << 2), //
	MANAGE_USERS(1L << 3); //

	private final long value;

	UserPermission(long value) {
		this.value = value;
	}

	@Override
	public String getAuthority() {
		return name();
	}

	public static List<UserPermission> fromValue(long value) {
		return PermissionUtils.fromValue(value, UserPermission.class);
	}

}

package dev.erpix.easykan.server.domain.user.model;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Getter
public enum UserPermission implements GrantedAuthority {

    DEFAULT_PERMISSIONS(0L),
    ADMIN(1L),
    MANAGE_USERS(1L << 1),
    MANAGE_PROJECTS(1L << 2),
    ;

    private final long value;

    UserPermission(long value) {
        this.value = value;
    }

    @Override
    public String getAuthority() {
        return name();
    }

    public static List<UserPermission> fromValue(long value) {
        return Stream.of(UserPermission.values())
                .filter(permission -> (permission.value & value) == permission.value)
                .toList();
    }

    public static long toValue(List<UserPermission> permissions) {
        return permissions.stream()
                .mapToLong(UserPermission::getValue)
                .reduce(0, (a, b) -> a | b);
    }

    public static long toValue(UserPermission... permissions) {
        return Arrays.stream(permissions)
                .mapToLong(UserPermission::getValue)
                .reduce(0, (a, b) -> a | b);
    }

    public static boolean hasPermission(long userPermissions, UserPermission permission) {
        return (userPermissions & permission.getValue()) == permission.getValue();
    }

    public static boolean hasAnyPermission(long userPermissions, UserPermission... permissions) {
        return Arrays.stream(permissions)
                .anyMatch(permission -> hasPermission(userPermissions, permission));
    }

}

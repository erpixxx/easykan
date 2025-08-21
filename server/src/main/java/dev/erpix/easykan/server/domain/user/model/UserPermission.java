package dev.erpix.easykan.server.domain.user.model;

import dev.erpix.easykan.server.exception.common.ValidationException;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Getter
public enum UserPermission implements GrantedAuthority {

    DEFAULT_PERMISSIONS(0L),
    ADMIN(1L),
    MANAGE_PROJECTS(1L << 1),
    MANAGE_USERS(1L << 2);

    public static final long ALL_PERMISSIONS_MASK = Arrays.stream(values())
            .mapToLong(UserPermission::getValue)
            .reduce(0, (a, b) -> a | b);

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

    public static long toValue(UserPermission... permissions) {
        return Arrays.stream(permissions)
                .mapToLong(UserPermission::getValue)
                .reduce(0, (a, b) -> a | b);
    }

    public static boolean hasPermission(User user, UserPermission permission) {
        return (user.getPermissions() & permission.getValue()) == permission.getValue();
    }

    public static boolean hasAnyPermission(User user, UserPermission... permissions) {
        return Arrays.stream(permissions)
                .anyMatch(permission -> hasPermission(user, permission));
    }

    public static void validatePermissions(long permissions) {
        if (permissions < 0) {
            throw new ValidationException("Permissions value cannot be negative");
        }
        if ((permissions & ~UserPermission.ALL_PERMISSIONS_MASK) != 0) {
            throw new ValidationException("Invalid permissions value: " + permissions);
        }
    }

}

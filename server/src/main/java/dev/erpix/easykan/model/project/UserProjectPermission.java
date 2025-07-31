package dev.erpix.easykan.model.project;

import lombok.Getter;

@Getter
public enum UserProjectPermission {

    ADMIN(1L),
    CREATE_PROJECT(1L << 1),
    DELETE_PROJECT(1L << 2),
    EDIT_PROJECT(1L << 3),
    CREATE_USER(1L << 4),
    DELETE_USER(1L << 5),
    EDIT_USER(1L << 6),
    ;

    private final long value;

    UserProjectPermission(long value) {
        this.value = value;
    }

    public static UserProjectPermission fromValue(long value) {
        for (UserProjectPermission permission : values()) {
            if (permission.value == value) {
                return permission;
            }
        }
        throw new IllegalArgumentException("No UserPermission found for value: " + value);
    }

    public static long combine(UserProjectPermission... permissions) {
        long combined = 0;
        for (UserProjectPermission permission : permissions) {
            combined |= permission.value;
        }
        return combined;
    }
}

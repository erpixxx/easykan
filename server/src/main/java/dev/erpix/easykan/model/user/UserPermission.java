package dev.erpix.easykan.model.user;

import lombok.Getter;

@Getter
public enum UserPermission {

    ADMIN(1L),
    CREATE_PROJECT(1L << 1),
    DELETE_PROJECT(1L << 2),
    EDIT_PROJECT(1L << 3),
    CREATE_USER(1L << 4),
    DELETE_USER(1L << 5),
    EDIT_USER(1L << 6),
    ;

    private final long value;

    UserPermission(long value) {
        this.value = value;
    }

    public static UserPermission fromValue(long value) {
        for (UserPermission permission : values()) {
            if (permission.value == value) {
                return permission;
            }
        }
        throw new IllegalArgumentException("No UserPermission found for value: " + value);
    }

    public static long combine(UserPermission... permissions) {
        long combined = 0;
        for (UserPermission permission : permissions) {
            combined |= permission.value;
        }
        return combined;
    }
}

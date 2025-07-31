package dev.erpix.easykan.server.domain.project.model;

import lombok.Getter;

// The enum and its values may change in the final version

@Getter
public enum UserProjectPermission {

    OWNER(1L),
    MANAGE_PROJECT(1L << 1),
    MANAGE_MEMBERS(1L << 2),
    CREATE_BOARD(1L << 3),
    DELETE_BOARD(1L << 4),
    RENAME_BOARD(1L << 5),
    CREATE_COLUMN(1L << 6),
    DELETE_COLUMN(1L << 7),
    RENAME_COLUMN(1L << 8),
    MOVE_COLUMN(1L << 9),
    CREATE_CARD(1L << 10),
    DELETE_CARD(1L << 11),
    RENAME_CARD(1L << 12),
    MOVE_CARD(1L << 13),
    ASSIGN_CARD(1L << 14),
    COMMENT_CARD(1L << 15),
    VIEW_ACTIVITY(1L << 16),
    MANAGE_TAG(1L << 17);

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

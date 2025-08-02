package dev.erpix.easykan.server.constant;

import java.util.Arrays;

public interface CacheKey {

    String PROJECTS = "projects";
    String USERS = "users";

    static String[] getCacheKeys() {
        return Arrays.stream(CacheKey.class.getDeclaredFields())
                .filter(field -> field.getType().equals(String.class))
                .map(field -> {
                    try {
                        return (String) field.get(null);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Failed to access cache key: " + field.getName(), e);
                    }
                })
                .toArray(String[]::new);
    }

}

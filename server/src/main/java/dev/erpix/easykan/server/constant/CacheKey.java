package dev.erpix.easykan.server.constant;

import java.util.Arrays;

public interface CacheKey {

	String PROJECTS = "projects";

	String USERS_ID = "users_id";

	String USERS_LOGIN = "users_login";

	static String[] getCacheKeys() {
		return Arrays.stream(CacheKey.class.getDeclaredFields())
			.filter(field -> field.getType().equals(String.class))
			.map(field -> {
				try {
					return (String) field.get(null);
				}
				catch (IllegalAccessException e) {
					throw new RuntimeException("Failed to access cache key: " + field.getName(), e);
				}
			})
			.toArray(String[]::new);
	}

}

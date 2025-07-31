package dev.erpix.easykan.server.constant;


public interface CacheKey {

    String PROJECTS = "projects";
    String USERS = "users";

    static String[] getCacheKeys() {
        return new String[]{ PROJECTS, USERS };
    }

}

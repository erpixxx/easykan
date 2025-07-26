package dev.erpix.easykan;

public final class EasyKanConstants {

    public static final String APP_NAME = "@app-name@";
    public static final String APP_VERSION = "@app-version@";

    public static final String DATABASE_NAME = System.getenv("EASYKAN_DATABASE_NAME");
    public static final String DATABASE_TABLE_PREFIX = System.getenv("EASYKAN_DATABASE_TABLE_PREFIX");

}

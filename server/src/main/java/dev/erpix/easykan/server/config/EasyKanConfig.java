package dev.erpix.easykan.server.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="easykan")public record EasyKanConfig(String serverUrl,String clientUrl,boolean useHttps,boolean createDefaultAdminAccount,Jwt jwt,Password password,Oidc oidc){

public record Jwt(String secret,int accessTokenExpire,int refreshTokenExpire){}

public record Password(boolean enabled,int minLength,boolean requireUppercase,boolean requireLowercase,boolean requireDigit,boolean requireSpecialCharacter){}

public record Oidc(boolean enabled,String clientId,String clientSecret,String issuerUri,String[]scopes,String emailAttribute,String nameAttribute,String rolesAttribute,String[]adminRoles){}}

package dev.erpix.easykan.server.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "easykan")
public record EasyKanConfig(
        String serverUrl,
        String clientUrl,
        boolean useHttps,
        Jwt jwt,
        Password password,
        Oidc oidc
) {

    public record Jwt(
            String secret,
            int accessTokenExpire,
            int refreshTokenExpire
    ) { }

    public record Password(
            boolean enabled,
            int minLength,
            boolean requireUppercase,
            boolean requireLowercase,
            boolean requireDigit,
            boolean requireSpecialCharacter
    ) { }

    public record Oidc(
            boolean enabled,
            String issuerUri,
            String clientId,
            String clientSecret,
            String[] scopes,
            String usernameClaim
    ) { }

}

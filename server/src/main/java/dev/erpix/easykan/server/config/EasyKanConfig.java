package dev.erpix.easykan.server.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "easykan")
public record EasyKanConfig(
        String serverUrl,
        String clientUrl,
        boolean useHttps,
        JwtProperties jwt,
        PasswordProperties password,
        OidcProperties oidc
) {

    public record JwtProperties(
            String secret,
            int accessTokenExpire,
            int refreshTokenExpire
    ) { }

    public record PasswordProperties(
            boolean enabled,
            int minLength,
            boolean requireUppercase,
            boolean requireLowercase,
            boolean requireDigit,
            boolean requireSpecialCharacter
    ) { }

    public record OidcProperties(
            boolean enabled,
            String issuerUri,
            String clientId,
            String clientSecret,
            String[] scopes,
            String usernameClaim
    ) { }

}

package dev.erpix.easykan.server.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "easykan")
public record EasyKanConfig(
        boolean useHttps,
        JwtProperties jwt,
        PasswordProperties password
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

}

package dev.erpix.easykan.server.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "easykan")
public record EasyKanConfig(
        boolean useHttps,
        JwtProperties jwt
) {

    public record JwtProperties(
            String secret,
            int accessTokenExpire,
            int refreshTokenExpire
    ) { }

}

package dev.erpix.easykan.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "easykan")
public record EasyKanProperties(
        boolean useHttps,
        JwtProperties jwt
) {

    public record JwtProperties(
            String secret,
            long accessTokenExpire,
            long refreshTokenExpire
    ) { }

}

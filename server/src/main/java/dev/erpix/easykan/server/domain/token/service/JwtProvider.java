package dev.erpix.easykan.server.domain.token.service;

import dev.erpix.easykan.server.config.EasyKanConfig;
import dev.erpix.easykan.server.exception.InvalidTokenException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final EasyKanConfig config;
    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(config.jwt().secret().getBytes(StandardCharsets.UTF_8));
    }

    public @NotNull String generate(@NotNull String subject) {
        Instant now = Instant.now();
        long expirationSeconds = config.jwt().accessTokenExpire();
        return Jwts.builder()
                .subject(subject)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expirationSeconds)))
                .signWith(key)
                .compact();
    }

    public @NotNull String validate(@NotNull String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (JwtException e) {
            throw new InvalidTokenException();
        }
    }

}

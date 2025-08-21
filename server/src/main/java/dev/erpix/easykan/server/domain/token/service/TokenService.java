package dev.erpix.easykan.server.domain.token.service;

import dev.erpix.easykan.server.config.EasyKanConfig;
import dev.erpix.easykan.server.domain.token.dto.TokenPairDto;
import dev.erpix.easykan.server.domain.token.dto.CreateTokenDto;
import dev.erpix.easykan.server.domain.token.model.RefreshToken;
import dev.erpix.easykan.server.domain.user.model.User;
import dev.erpix.easykan.server.domain.token.repository.TokenRepository;
import dev.erpix.easykan.server.domain.token.security.TokenGenerator;
import dev.erpix.easykan.server.domain.token.security.TokenParts;
import dev.erpix.easykan.server.domain.user.security.JpaUserDetails;
import dev.erpix.easykan.server.domain.user.service.UserService;
import dev.erpix.easykan.server.exception.auth.InvalidTokenException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final EasyKanConfig config;
    private final TokenRepository tokenRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    private final TokenGenerator tokenGenerator;
    private final JwtProvider jwtProvider;

    public @NotNull CreateTokenDto createAccessToken(@NotNull UUID userId) {
        String rawToken = jwtProvider.generate(userId.toString());
        Duration duration = Duration.ofSeconds(config.jwt().accessTokenExpire());
        return new CreateTokenDto(rawToken, duration);
    }

    public @NotNull CreateTokenDto createRefreshToken(@NotNull UUID userId) {
        User user = userService.getById(userId);

        TokenParts tokenParts = tokenGenerator.generate();
        String validatorHash = passwordEncoder.encode(tokenParts.validator());

        Duration duration = Duration.ofSeconds(config.jwt().refreshTokenExpire());
        RefreshToken refreshToken = RefreshToken.builder()
                .selector(tokenParts.selector())
                .validator(validatorHash)
                .user(user)
                .expiresAt(Instant.now().plus(duration))
                .build();

        tokenRepository.save(refreshToken);

        return new CreateTokenDto(tokenParts.combine(), duration);
    }

    public void logout(String rawRefreshToken) {
        findAndVerifyToken(rawRefreshToken).ifPresent(this::revokeToken);
    }

    @Transactional
    public void logoutAll(@NotNull JpaUserDetails userDetails) {
        tokenRepository.revokeAllByUserAndExpiresAtAfter(userDetails.user(), Instant.now());
    }

    /**
     * Removes all expired tokens from the repository. To be used in CRON jobs.
     */
    public void removeExpiredTokens() {
        tokenRepository.deleteAllByExpiresAtBefore(Instant.now());
    }

    public TokenPairDto rotateRefreshToken(String rawOldToken) {
        return findAndVerifyToken(rawOldToken)
                .map(oldToken -> {
                    revokeToken(oldToken);

                    UUID userId = oldToken.getUser().getId();
                    CreateTokenDto newAccessToken = createAccessToken(userId);
                    CreateTokenDto newRefreshToken = createRefreshToken(userId);

                    return new TokenPairDto(newAccessToken.rawToken(),
                            newAccessToken.duration(),
                            newRefreshToken.rawToken(),
                            newRefreshToken.duration());
                }).orElseThrow(InvalidTokenException::new);
    }

    private void revokeToken(RefreshToken token) {
        token.setRevoked(true);
        tokenRepository.save(token);
    }

    private Optional<RefreshToken> findAndVerifyToken(String combinedToken) {
        if (combinedToken == null || combinedToken.indexOf(':') == -1) {
            return Optional.empty();
        }
        String[] parts = combinedToken.split(":", 2);
        String selector = parts[0];
        String validator = parts[1];

        return tokenRepository.findBySelectorAndRevokedFalseAndExpiresAtAfter(selector, Instant.now())
                .filter(tokenEntity ->
                        passwordEncoder.matches(validator, tokenEntity.getValidator()));
    }

}

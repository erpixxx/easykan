package dev.erpix.easykan.server.domain.token.service;

import dev.erpix.easykan.server.domain.token.model.RefreshToken;
import dev.erpix.easykan.server.domain.token.dto.CreateRefreshTokenDto;
import dev.erpix.easykan.server.domain.token.dto.RotatedTokensDto;
import dev.erpix.easykan.server.domain.user.model.User;
import dev.erpix.easykan.server.domain.token.repository.TokenRepository;
import dev.erpix.easykan.server.domain.auth.service.JwtProvider;
import dev.erpix.easykan.server.domain.auth.security.TokenGenerator;
import dev.erpix.easykan.server.domain.auth.security.TokenParts;
import dev.erpix.easykan.server.domain.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenRepository tokenRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    private final TokenGenerator tokenGenerator;
    private final JwtProvider jwtProvider;

    public @NotNull String createAccessToken(@NotNull UUID userId) {
        return jwtProvider.generate(userId.toString());
    }

    public @NotNull CreateRefreshTokenDto createRefreshToken(@NotNull UUID userId) {
        User user = userService.getById(userId);

        TokenParts tokenParts = tokenGenerator.generate();
        String validatorHash = passwordEncoder.encode(tokenParts.validator());

        LocalDateTime expiresAt = LocalDateTime.now().plus(Duration.ofDays(7));
        RefreshToken refreshToken = RefreshToken.builder()
                .selector(tokenParts.selector())
                .validator(validatorHash)
                .user(user)
                .expiresAt(expiresAt)
                .build();

        tokenRepository.save(refreshToken);

        return new CreateRefreshTokenDto(tokenParts.combine(), expiresAt);
    }

    public void logout(String rawRefreshToken) {
        findAndVerifyToken(rawRefreshToken).ifPresent(this::revokeToken);
    }

    @Transactional
    public void logoutAll(String rawRefreshToken) {
        findAndVerifyToken(rawRefreshToken).ifPresent(token -> {
            User user = token.getUser();
            tokenRepository.findByUserAndRevokedFalse(user)
                    .forEach(this::revokeToken);
        });
    }

    public Optional<RotatedTokensDto> rotateRefreshToken(String rawOldToken) {
        return findAndVerifyToken(rawOldToken).map(oldToken -> {
            revokeToken(oldToken);

            UUID userId = oldToken.getUser().getId();
            String newAccessToken = createAccessToken(userId);
            CreateRefreshTokenDto newRefreshToken = createRefreshToken(userId);

            return new RotatedTokensDto(
                    newAccessToken,
                    newRefreshToken.rawToken(),
                    newRefreshToken.expiresAt());
        });
    }

    /**
     * Removes all expired tokens from the repository. To be used in CRON jobs.
     */
    public void removeExpiredTokens() {
        tokenRepository.deleteAllByExpiresAtBefore(LocalDateTime.now());
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

        return tokenRepository.findBySelectorAndRevokedFalseAndExpiresAtAfter(selector, LocalDateTime.now())
                .filter(tokenEntity -> passwordEncoder.matches(validator, tokenEntity.getValidator()));
    }

}

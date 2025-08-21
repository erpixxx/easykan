package dev.erpix.easykan.server.service;

import dev.erpix.easykan.server.config.EasyKanConfig;
import dev.erpix.easykan.server.domain.token.dto.TokenPairDto;
import dev.erpix.easykan.server.domain.token.model.RefreshToken;
import dev.erpix.easykan.server.domain.token.service.TokenService;
import dev.erpix.easykan.server.domain.user.model.User;
import dev.erpix.easykan.server.domain.user.security.JpaUserDetails;
import dev.erpix.easykan.server.domain.user.service.UserService;
import dev.erpix.easykan.server.domain.user.util.UserDetailsProvider;
import dev.erpix.easykan.server.domain.token.repository.TokenRepository;
import dev.erpix.easykan.server.domain.token.service.JwtProvider;
import dev.erpix.easykan.server.domain.token.security.TokenGenerator;
import dev.erpix.easykan.server.domain.token.security.TokenParts;
import dev.erpix.easykan.server.exception.UserNotFoundException;
import dev.erpix.easykan.server.testsupport.Category;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Tag(Category.UNIT_TEST)
@ExtendWith(MockitoExtension.class)
public class TokenServiceTest {

    @InjectMocks
    private TokenService tokenService;

    @Mock
    private EasyKanConfig config;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenGenerator tokenGenerator;

    @Mock
    private JwtProvider jwtProvider;

    @Captor
    private ArgumentCaptor<RefreshToken> refreshTokenCaptor;

    private static final String JWT_SECRET = "secret";
    private static final int ACCESS_TOKEN_EXPIRE = 180;
    private static final int REFRESH_TOKEN_EXPIRE = 3600;

    private void mockConfig() {
        var jwt = new EasyKanConfig.JwtProperties(JWT_SECRET, ACCESS_TOKEN_EXPIRE, REFRESH_TOKEN_EXPIRE);
        when(config.jwt())
                .thenReturn(jwt);
    }

    @Test
    void createAccessToken_shouldReturnToken_forAnyId() {
        mockConfig();

        UUID userId = UUID.randomUUID();
        when(jwtProvider.generate(userId.toString()))
                .thenReturn("accessToken");

        var result = tokenService.createAccessToken(userId);
        assertThat(result.rawToken()).isEqualTo("accessToken");
        assertThat(result.duration()).isEqualTo(Duration.ofSeconds(ACCESS_TOKEN_EXPIRE));
    }

    @Test
    void createRefreshToken_shouldReturnNewToken_whenUserExists() {
        mockConfig();

        UUID userId = UUID.randomUUID();
        User user = User.builder().id(userId).build();
        String selector = "selector";
        String validator = "validator";
        TokenParts parts = new TokenParts(selector, validator);

        when(userService.getById(userId))
                .thenReturn(user);
        when(tokenGenerator.generate())
                .thenReturn(parts);

        var result = tokenService.createRefreshToken(userId);

        assertThat(result.rawToken()).isEqualTo(parts.combine());
        assertThat(result.duration()).isEqualTo(Duration.ofSeconds(REFRESH_TOKEN_EXPIRE));

        verify(tokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void createRefreshToken_shouldThrowException_whenUserDoesNotExist() {
        UUID userId = UUID.randomUUID();

        when(userService.getById(userId))
                .thenThrow(UserNotFoundException.byId(userId));

        assertThrows(UserNotFoundException.class,
                () -> tokenService.createRefreshToken(userId));

        verify(tokenRepository, never()).save(any(RefreshToken.class));
    }

    @Test
    void logout_shouldRevoke_whenTokenExists() {
        String rawRefreshToken = "selector:validator";
        RefreshToken tokenToRevoke = RefreshToken.builder()
                .revoked(false)
                .validator("hashedValidator")
                .build();

        when(tokenRepository.findBySelectorAndRevokedFalseAndExpiresAtAfter(any(), any()))
                .thenReturn(Optional.of(tokenToRevoke));
        when(passwordEncoder.matches("validator", "hashedValidator"))
                .thenReturn(true);

        tokenService.logout(rawRefreshToken);

        verify(tokenRepository).save(refreshTokenCaptor.capture());

        RefreshToken captured = refreshTokenCaptor.getValue();
        assertThat(captured.isRevoked()).isTrue();
    }

    @Test
    void logout_shouldDoNothing_whenTokenIsInvalid() {
        String rawRefreshToken = "invalid-token";

        tokenService.logout(rawRefreshToken);

        verify(tokenRepository, never()).save(any(RefreshToken.class));
    }

    @Test
    void logoutAll_shouldRevokeAllTokens_whenTokenExists() {
        User user = User.builder().id(UUID.randomUUID()).build();

        tokenService.logoutAll(new JpaUserDetails(user));

        verify(tokenRepository).revokeAllByUserAndExpiresAtAfter(
                eq(user), any(Instant.class));
    }

    @Test
    void rotateRefreshToken_shouldReturnNewTokens_whenOldTokenIsValid() {
        mockConfig();

        String rawRefreshToken = "selector:validator";
        User user = User.builder().id(UUID.randomUUID()).build();
        RefreshToken oldToken = RefreshToken.builder()
                .revoked(false)
                .validator("hashedValidator")
                .user(user)
                .build();

        when(tokenRepository.findBySelectorAndRevokedFalseAndExpiresAtAfter(any(), any()))
                .thenReturn(Optional.of(oldToken));
        when(passwordEncoder.matches("validator", "hashedValidator"))
                .thenReturn(true);
        when(userService.getById(user.getId()))
                .thenReturn(user);
        when(tokenGenerator.generate())
                .thenReturn(new TokenParts("newSelector", "newValidator"));

        TokenPairDto tokenPairDto = tokenService.rotateRefreshToken(rawRefreshToken);

        verify(tokenRepository, times(2)).save(refreshTokenCaptor.capture());
        List<RefreshToken> capturedTokens = refreshTokenCaptor.getAllValues();
        assertThat(capturedTokens).hasSize(2);

        RefreshToken revokedToken = capturedTokens.getFirst();
        assertThat(revokedToken.isRevoked()).isTrue();
        assertThat(revokedToken).isEqualTo(oldToken);

        RefreshToken newToken = capturedTokens.get(1);
        assertThat(newToken.isRevoked()).isFalse();
        assertThat(newToken.getSelector()).isEqualTo("newSelector");

        assertThat(tokenPairDto.newAccessTokenDuration())
                .isEqualTo(Duration.ofSeconds(ACCESS_TOKEN_EXPIRE));
        assertThat(tokenPairDto.newRefreshTokenDuration())
                .isEqualTo(Duration.ofSeconds(REFRESH_TOKEN_EXPIRE));
    }

}

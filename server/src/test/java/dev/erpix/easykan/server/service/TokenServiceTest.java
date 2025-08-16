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
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.TestPropertySources;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.sql.Ref;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringJUnitConfig(classes = TokenService.class)
@EnableConfigurationProperties(EasyKanConfig.class)
@TestPropertySources({
        @TestPropertySource(properties = "easykan.jwt.access-token-expire=" + TokenServiceTest.ACCESS_TOKEN_EXPIRE),
        @TestPropertySource(properties = "easykan.jwt.refresh-token-expire=" + TokenServiceTest.REFRESH_TOKEN_EXPIRE),
        @TestPropertySource(properties = "easykan.jwt.secret=secret")
})
public class TokenServiceTest {

    static final int ACCESS_TOKEN_EXPIRE = 180;
    static final int REFRESH_TOKEN_EXPIRE = 3600;

    @Autowired
    private TokenService tokenService;

    @MockitoBean
    private TokenRepository tokenRepository;
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private PasswordEncoder passwordEncoder;
    @MockitoBean
    private TokenGenerator tokenGenerator;
    @MockitoBean
    private JwtProvider jwtProvider;
    @MockitoBean
    private UserDetailsProvider userDetailsProvider;

    @Captor
    private ArgumentCaptor<RefreshToken> refreshTokenCaptor;

    @Test
    void createAccessToken_shouldReturnToken_forAnyId() {
        UUID userId = UUID.randomUUID();
        when(jwtProvider.generate(userId.toString()))
                .thenReturn("accessToken");

        var result = tokenService.createAccessToken(userId);
        assertThat(result.rawToken()).isEqualTo("accessToken");
        assertThat(result.duration()).isEqualTo(Duration.ofSeconds(ACCESS_TOKEN_EXPIRE));
    }

    @Test
    void createRefreshToken_shouldReturnNewToken_whenUserExists() {
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

        when(tokenRepository.findBySelectorAndRevokedFalseAndExpiresAtAfter(any(), any()))
                .thenReturn(Optional.empty());

        tokenService.logout(rawRefreshToken);

        verify(tokenRepository, never()).save(any(RefreshToken.class));
    }

    @Test
    void logoutAll_shouldRevokeAllTokens_whenTokenExists() {
        User user = User.builder().id(UUID.randomUUID()).build();

        when(userDetailsProvider.getRequiredCurrentUserDetails())
                .thenReturn(new JpaUserDetails(user));

        tokenService.logoutAll();

        verify(tokenRepository).revokeAllByUserAndExpiresAtAfter(
                eq(user), any(Instant.class));
    }

    @Test
    void logoutAll_shouldThrowException_whenUserDetailsNotFound() {
        when(userDetailsProvider.getRequiredCurrentUserDetails())
                .thenThrow(new IllegalStateException("User details not found"));

        assertThrows(IllegalStateException.class, tokenService::logoutAll);

        verify(tokenRepository, never()).revokeAllByUserAndExpiresAtAfter(any(), any());
    }

    @Test
    void rotateRefreshToken_shouldReturnNewTokens_whenOldTokenIsValid() {
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

        RefreshToken revokedToken = capturedTokens.get(0);
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

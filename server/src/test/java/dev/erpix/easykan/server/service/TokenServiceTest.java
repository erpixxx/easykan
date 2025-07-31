package dev.erpix.easykan.server.service;

import dev.erpix.easykan.server.domain.token.service.TokenService;
import dev.erpix.easykan.server.domain.user.service.UserService;
import dev.erpix.easykan.server.exception.ResourceNotFoundException;
import dev.erpix.easykan.server.domain.token.model.RefreshToken;
import dev.erpix.easykan.server.domain.token.dto.RotatedTokensDto;
import dev.erpix.easykan.server.domain.user.model.EKUser;
import dev.erpix.easykan.server.domain.token.repository.TokenRepository;
import dev.erpix.easykan.server.domain.auth.service.JwtProvider;
import dev.erpix.easykan.server.security.TokenGenerator;
import dev.erpix.easykan.server.security.TokenParts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TokenServiceTest {

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

    @InjectMocks
    private TokenService tokenService;

    @Captor
    private ArgumentCaptor<RefreshToken> refreshTokenCaptor;

    @Test
    void createRefreshToken_shouldReturnNewToken_whenUserExists() {
        UUID userId = UUID.randomUUID();
        EKUser user = EKUser.builder().id(userId).build();
        TokenParts tokenParts = new TokenParts("selector", "validator");
        String hashedValidator = "hashedValidator";

        when(userService.getById(userId)).thenReturn(user);
        when(tokenGenerator.generate()).thenReturn(tokenParts);
        when(passwordEncoder.encode(tokenParts.validator())).thenReturn(hashedValidator);

        var result = tokenService.createRefreshToken(userId);

        assertThat(result.rawToken()).isEqualTo("selector:validator");
        assertThat(result.expiresAt()).isNotNull();

        // Verify that the token was saved
        verify(tokenRepository, times(1)).save(any(RefreshToken.class));
    }

    @Test
    void createRefreshToken_shouldThrowException_whenUserDoesNotExist() {
        UUID userId = UUID.randomUUID();

        when(userService.getById(userId)).thenThrow(
                new ResourceNotFoundException("User not found"));

        assertThrows(ResourceNotFoundException.class, () -> tokenService.createRefreshToken(userId));

        // Verify that no token was saved
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
        when(passwordEncoder.matches("validator", "hashedValidator")).thenReturn(true);

        tokenService.logout(rawRefreshToken);

        verify(tokenRepository).save(refreshTokenCaptor.capture());

        RefreshToken captured = refreshTokenCaptor.getValue();
        assertThat(captured.isRevoked()).isTrue();
    }

    @Test
    void logout_shouldDoNothing_whenTokenIsInvalid() {
        String invalidToken = "invalid-selector:invalid-validator";

        when(tokenRepository.findBySelectorAndRevokedFalseAndExpiresAtAfter(any(), any()))
                .thenReturn(Optional.empty());

        tokenService.logout(invalidToken);

        verify(tokenRepository, never()).save(any());
    }

    @Test
    void logoutAll_shouldRevokeAllTokens_whenTokenExists() {
        String rawRefreshToken = "selector:validator";
        EKUser user = EKUser.builder().id(UUID.randomUUID()).build();

        RefreshToken primaryToken = RefreshToken.builder()
                .revoked(false)
                .validator("hashedValidator")
                .user(user)
                .build();
        RefreshToken otherToken = RefreshToken.builder()
                .revoked(false)
                .validator("validatorHashed")
                .user(user)
                .build();

        when(tokenRepository.findBySelectorAndRevokedFalseAndExpiresAtAfter(any(), any()))
                .thenReturn(Optional.of(primaryToken));
        when(passwordEncoder.matches("validator", "hashedValidator")).thenReturn(true);
        when(tokenRepository.findByUserAndRevokedFalse(user))
                .thenReturn(List.of(primaryToken, otherToken));

        tokenService.logoutAll(rawRefreshToken);

        verify(tokenRepository, times(2)).save(any());
    }

    @Test
    void logoutAll_shouldDoNothing_whenTokenIsInvalid() {
        String rawRefreshToken = "invalid-selector:invalid-validator";

        when(tokenRepository.findBySelectorAndRevokedFalseAndExpiresAtAfter(any(), any()))
                .thenReturn(Optional.empty());

        tokenService.logoutAll(rawRefreshToken);

        verify(tokenRepository, never()).save(any());
    }

    @Test
    void rotateRefreshToken_shouldSucceed_whenTokenIsValid() {
        String rawOldToken = "selector:validator";
        UUID userId = UUID.randomUUID();
        EKUser user = EKUser.builder().id(userId).build();
        RefreshToken oldToken = RefreshToken.builder().user(user).validator("hashedValidator").build();
        TokenParts newParts = new TokenParts("new-selector", "new-validator");

        when(tokenRepository.findBySelectorAndRevokedFalseAndExpiresAtAfter(any(), any()))
                .thenReturn(Optional.of(oldToken));
        when(passwordEncoder.matches("validator", "hashedValidator")).thenReturn(true);
        when(jwtProvider.generate(any())).thenReturn("new-access-token");
        when(userService.getById(userId)).thenReturn(user);
        when(tokenGenerator.generate()).thenReturn(newParts);

        Optional<RotatedTokensDto> result = tokenService.rotateRefreshToken(rawOldToken);

        assertThat(result).isPresent();
        assertThat(result.get().newAccessToken()).isEqualTo("new-access-token");
        assertThat(result.get().newRawRefreshToken()).isEqualTo("new-selector:new-validator");

        // Verify that the old token was revoked
        verify(tokenRepository, times(2)).save(any(RefreshToken.class));
    }

    @Test
    void rotateRefreshToken_shouldFail_whenTokenIsInvalid() {
        String invalidToken = "invalid-selector:invalid-validator";
        when(tokenRepository.findBySelectorAndRevokedFalseAndExpiresAtAfter(any(), any()))
                .thenReturn(Optional.empty());

        Optional<RotatedTokensDto> result = tokenService.rotateRefreshToken(invalidToken);

        assertThat(result).isNotPresent();
    }

}

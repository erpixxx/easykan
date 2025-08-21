package dev.erpix.easykan.server.service;

import dev.erpix.easykan.server.domain.auth.dto.AuthLoginRequestDto;
import dev.erpix.easykan.server.domain.auth.service.AuthService;
import dev.erpix.easykan.server.domain.token.dto.CreateTokenDto;
import dev.erpix.easykan.server.domain.token.dto.TokenPairDto;
import dev.erpix.easykan.server.domain.token.service.TokenService;
import dev.erpix.easykan.server.domain.user.model.User;
import dev.erpix.easykan.server.domain.user.service.UserService;
import dev.erpix.easykan.server.exception.auth.UnsupportedAuthenticationMethodException;
import dev.erpix.easykan.server.testsupport.Category;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@Tag(Category.UNIT_TEST)
@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    @Mock
    private UserService userService;

    @Test
    void loginWithPassword_shouldReturnTokenPair_whenCredentialsAreValid() {
        String userLogin = "testUser";
        AuthLoginRequestDto request = new AuthLoginRequestDto(userLogin, "testPassword");

        User user = User.builder()
                .id(UUID.randomUUID())
                .login(userLogin)
                .passwordHash("hashedPassword")
                .build();

        String accessToken = "accessToken";
        Duration accessTokenDuration = Duration.ofMinutes(15);
        String refreshToken = "refreshToken";
        Duration refreshTokenDuration = Duration.ofDays(7);

        when(userService.getByLogin(userLogin))
                .thenReturn(user);
        when(passwordEncoder.matches("testPassword", user.getPasswordHash()))
                .thenReturn(true);
        when(tokenService.createAccessToken(user.getId()))
                .thenReturn(new CreateTokenDto(accessToken, accessTokenDuration));
        when(tokenService.createRefreshToken(user.getId()))
                .thenReturn(new CreateTokenDto(refreshToken, refreshTokenDuration));

        TokenPairDto tokenPair = authService.loginWithPassword(request);
        assertEquals(accessToken, tokenPair.newAccessToken());
        assertEquals(accessTokenDuration, tokenPair.newAccessTokenDuration());
        assertEquals(refreshToken, tokenPair.newRawRefreshToken());
        assertEquals(refreshTokenDuration, tokenPair.newRefreshTokenDuration());
    }

    @Test
    void loginWithPassword_shouldThrowException_whenUserHasNoPassword() {
        String userLogin = "testUser";
        AuthLoginRequestDto request = new AuthLoginRequestDto(userLogin, "testPassword");

        User user = User.builder()
                .id(UUID.randomUUID())
                .login(userLogin)
                .passwordHash(null)
                .build();

        when(userService.getByLogin(userLogin))
                .thenReturn(user);

        assertThrows(UnsupportedAuthenticationMethodException.class, () ->
                authService.loginWithPassword(request));
    }

    @Test
    void loginWithPassword_shouldThrowException_whenPasswordDoesNotMatch() {
        String userLogin = "testUser";
        AuthLoginRequestDto request = new AuthLoginRequestDto(userLogin, "wrongPassword");

        User user = User.builder()
                .id(UUID.randomUUID())
                .login(userLogin)
                .passwordHash("hashedPassword")
                .build();

        when(userService.getByLogin(userLogin))
                .thenReturn(user);
        when(passwordEncoder.matches("wrongPassword", user.getPasswordHash()))
                .thenReturn(false);

        assertThrows(BadCredentialsException.class, () ->
                authService.loginWithPassword(request));
    }

}

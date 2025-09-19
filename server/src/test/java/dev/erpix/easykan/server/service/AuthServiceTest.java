package dev.erpix.easykan.server.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import dev.erpix.easykan.server.domain.auth.dto.AuthLoginRequestDto;
import dev.erpix.easykan.server.domain.auth.dto.UserAndTokenPairResponseDto;
import dev.erpix.easykan.server.domain.auth.service.AuthService;
import dev.erpix.easykan.server.domain.token.AccessToken;
import dev.erpix.easykan.server.domain.token.RawRefreshToken;
import dev.erpix.easykan.server.domain.token.security.TokenParts;
import dev.erpix.easykan.server.domain.token.service.TokenService;
import dev.erpix.easykan.server.domain.user.model.User;
import dev.erpix.easykan.server.domain.user.model.UserPermission;
import dev.erpix.easykan.server.domain.user.service.UserService;
import dev.erpix.easykan.server.exception.auth.UnsupportedAuthenticationMethodException;
import dev.erpix.easykan.server.testsupport.Category;
import java.time.Duration;
import java.util.UUID;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

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

        User user =
                User.builder().id(UUID.randomUUID()).login(userLogin).passwordHash("hashedPassword")
                        .permissions(UserPermission.DEFAULT_PERMISSIONS.getValue()).build();

        String accessToken = "accessToken";
        Duration accessTokenDuration = Duration.ofMinutes(15);
        String refreshToken = "refresh:token";
        Duration refreshTokenDuration = Duration.ofDays(7);

        when(userService.getByLogin(userLogin)).thenReturn(user);
        when(passwordEncoder.matches("testPassword", user.getPasswordHash())).thenReturn(true);
        when(tokenService.createAccessToken(user.getId()))
                .thenReturn(new AccessToken(accessToken, accessTokenDuration));
        when(tokenService.createRefreshToken(user.getId())).thenReturn(
                new RawRefreshToken(new TokenParts("refresh", "token"), refreshTokenDuration));

        UserAndTokenPairResponseDto responseDto = authService.loginWithPassword(request);
        assertEquals(accessToken, responseDto.tokenPair().newAccessToken());
        assertEquals(accessTokenDuration, responseDto.tokenPair().newAccessTokenDuration());
        assertEquals(refreshToken, responseDto.tokenPair().newRawRefreshToken());
        assertEquals(refreshTokenDuration, responseDto.tokenPair().newRefreshTokenDuration());
    }

    @Test
    void loginWithPassword_shouldThrowException_whenUserHasNoPassword() {
        String userLogin = "testUser";
        AuthLoginRequestDto request = new AuthLoginRequestDto(userLogin, "testPassword");

        User user =
                User.builder().id(UUID.randomUUID()).login(userLogin).passwordHash(null).build();

        when(userService.getByLogin(userLogin)).thenReturn(user);

        assertThrows(UnsupportedAuthenticationMethodException.class,
                () -> authService.loginWithPassword(request));
    }

    @Test
    void loginWithPassword_shouldThrowException_whenPasswordDoesNotMatch() {
        String userLogin = "testUser";
        AuthLoginRequestDto request = new AuthLoginRequestDto(userLogin, "wrongPassword");

        User user = User.builder().id(UUID.randomUUID()).login(userLogin)
                .passwordHash("hashedPassword").build();

        when(userService.getByLogin(userLogin)).thenReturn(user);
        when(passwordEncoder.matches("wrongPassword", user.getPasswordHash())).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> authService.loginWithPassword(request));
    }
}

package dev.erpix.easykan.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.erpix.easykan.server.domain.user.security.JpaUserDetails;
import dev.erpix.easykan.server.testsupport.Category;
import dev.erpix.easykan.server.testsupport.annotation.WithMockUser;
import dev.erpix.easykan.server.config.EasyKanConfig;
import dev.erpix.easykan.server.config.SecurityConfig;
import dev.erpix.easykan.server.domain.auth.service.AuthService;
import dev.erpix.easykan.server.domain.token.dto.TokenPairDto;
import dev.erpix.easykan.server.domain.token.dto.CreateTokenDto;
import dev.erpix.easykan.server.exception.GlobalExceptionHandler;
import dev.erpix.easykan.server.exception.auth.InvalidTokenException;
import dev.erpix.easykan.server.exception.user.UserNotFoundException;
import dev.erpix.easykan.server.domain.auth.dto.AuthLoginRequestDto;
import dev.erpix.easykan.server.domain.token.service.JwtProvider;
import dev.erpix.easykan.server.domain.user.service.JpaUserDetailsService;
import dev.erpix.easykan.server.domain.token.service.TokenService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Tag(Category.INTEGRATION_TEST)
@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
public class AuthControllerIT extends AbstractControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @SuppressWarnings("unused")
    @MockitoBean
    private AuthService authService;

    @SuppressWarnings("unused")
    @MockitoBean
    private JpaUserDetailsService jpaUserDetailsService;

    @SuppressWarnings("unused")
    @MockitoBean
    private JwtProvider jwtProvider;

    @SuppressWarnings("unused")
    @MockitoBean
    private TokenService tokenService;

    @SuppressWarnings("unused")
    @MockitoBean
    private EasyKanConfig config;

    @Override
    protected Stream<Arguments> provideProtectedEndpoints() {
        return Stream.of(Arguments.of("POST", "/api/auth/logout-all"));
    }

    @Test
    void login_shouldReturnOkAndTokens_whenCredentialsAreValid() throws Exception {
        AuthLoginRequestDto request = new AuthLoginRequestDto("testuser", "password123");

        CreateTokenDto accessToken = new CreateTokenDto("raw-access-token", Duration.ofMinutes(15));
        CreateTokenDto refreshToken = new CreateTokenDto("raw-refresh-token", Duration.ofDays(7));
        int accessTokenExpire = (int) accessToken.duration().getSeconds();
        int refreshTokenExpire = (int) refreshToken.duration().getSeconds();

        when(authService.loginWithPassword(any(AuthLoginRequestDto.class))).thenReturn(
                new TokenPairDto(accessToken.rawToken(), accessToken.duration(),
                        refreshToken.rawToken(), refreshToken.duration()));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("access_token"))
                .andExpect(cookie().value("access_token", accessToken.rawToken()))
                .andExpect(cookie().maxAge("access_token", accessTokenExpire))
                .andExpect(cookie().httpOnly("access_token", true))
                .andExpect(cookie().exists("refresh_token"))
                .andExpect(cookie().value("refresh_token", refreshToken.rawToken()))
                .andExpect(cookie().maxAge("refresh_token", refreshTokenExpire))
                .andExpect(cookie().httpOnly("refresh_token", true));

        verify(authService).loginWithPassword(request);
    }

    @Test
    void login_shouldReturnNotFound_whenUserNotFound() throws Exception {
        AuthLoginRequestDto request = new AuthLoginRequestDto("nouser", "password123");
        when(authService.loginWithPassword(any(AuthLoginRequestDto.class)))
                .thenThrow(UserNotFoundException.byLogin("nouser"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException())
                        .isInstanceOf(UserNotFoundException.class))
                .andReturn();

        verify(authService).loginWithPassword(request);
    }

    @Test
    void login_ShouldReturnUnauthorized_whenPasswordIsIncorrect() throws Exception {
        AuthLoginRequestDto request = new AuthLoginRequestDto("testuser", "wrongpassword");

        when(authService.loginWithPassword(any(AuthLoginRequestDto.class)))
                .thenThrow(new BadCredentialsException("Invalid login or password."));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isUnauthorized());

        verify(authService).loginWithPassword(request);
    }

    @Test
    void logout_shouldClearCookies_whenTokenIsPresent() throws Exception {
        String refreshToken = "some-refresh-token";

        mockMvc.perform(post("/api/auth/logout")
                        .cookie(new Cookie("refresh_token", refreshToken)))
                .andExpect(status().isOk())
                .andExpect(cookie().maxAge("access_token", 0))
                .andExpect(cookie().maxAge("refresh_token", 0));

        verify(tokenService).logout(refreshToken);
    }

    @Test
    void logout_shouldClearCookies_whenTokenIsNotPresent() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(cookie().maxAge("access_token", 0))
                .andExpect(cookie().maxAge("refresh_token", 0));

        verify(tokenService, never()).logout(anyString());
    }

    @Test
    @WithMockUser
    void logoutAll_shouldClearCookies_whenTokenIsPresent() throws Exception {
        String refreshToken = "some-refresh-token";

        mockMvc.perform(post("/api/auth/logout-all")
                        .cookie(new Cookie("refresh_token", refreshToken)))
                .andExpect(status().isOk())
                .andExpect(cookie().maxAge("access_token", 0))
                .andExpect(cookie().maxAge("refresh_token", 0));

        verify(tokenService).logoutAll(any(JpaUserDetails.class));
    }

    @Test
    void logoutAll_shouldReturnUnauthorized_whenTokenIsNotPresent() throws Exception {
        mockMvc.perform(post("/api/auth/logout-all"))
                .andExpect(status().isUnauthorized());

        verify(tokenService, never()).logoutAll(any(JpaUserDetails.class));
    }

    @Test
    void refresh_shouldReturnOkAndNewTokens_whenTokenIsValid() throws Exception {
        String oldRefreshToken = "old-valid-token";
        TokenPairDto rotatedTokens = new TokenPairDto(
                "new-access-token",
                Duration.ofMinutes(15),
                "new-raw-refresh-token",
                Duration.ofDays(7));

        when(tokenService.rotateRefreshToken(oldRefreshToken))
                .thenReturn(rotatedTokens);

        mockMvc.perform(post("/api/auth/refresh")
                        .cookie(new Cookie("refresh_token", oldRefreshToken)))
                .andExpect(status().isOk())
                .andExpect(cookie().value("access_token", "new-access-token"))
                .andExpect(cookie().value("refresh_token", "new-raw-refresh-token"));
    }

    @Test
    void refresh_shouldReturnUnauthorized_whenTokenIsInvalid() throws Exception {
        String invalidRefreshToken = "invalid-token";
        when(tokenService.rotateRefreshToken(invalidRefreshToken))
                .thenThrow(new InvalidTokenException());

        mockMvc.perform(post("/api/auth/refresh")
                        .cookie(new Cookie("refresh_token", invalidRefreshToken)))
                .andExpect(status().isUnauthorized())
                .andExpect(result -> assertThat(result.getResolvedException())
                        .isInstanceOf(InvalidTokenException.class));
    }

}

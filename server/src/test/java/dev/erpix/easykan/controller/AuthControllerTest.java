package dev.erpix.easykan.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.erpix.easykan.config.EasyKanConfig;
import dev.erpix.easykan.config.SecurityConfig;
import dev.erpix.easykan.exception.GlobalExceptionHandler;
import dev.erpix.easykan.exception.UserNotFoundException;
import dev.erpix.easykan.model.auth.dto.AuthLoginRequest;
import dev.erpix.easykan.model.token.dto.CreateRefreshTokenDto;
import dev.erpix.easykan.model.token.dto.RotatedTokensDto;
import dev.erpix.easykan.model.user.EKUser;
import dev.erpix.easykan.security.JwtService;
import dev.erpix.easykan.service.TokenService;
import dev.erpix.easykan.service.UserService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private EasyKanConfig properties;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void login_shouldReturnOkAndTokens_whenCredentialsAreValid() throws Exception {
        UUID userId = UUID.randomUUID();
        String login = "testuser";
        String password = "password123";
        String displayName = "Test User";

        AuthLoginRequest request = new AuthLoginRequest(login, password);
        EKUser user = EKUser.builder()
                .id(userId)
                .login(login)
                .displayName(displayName)
                .passwordHash("hashedPassword")
                .canAuthWithPassword(true)
                .build();
        CreateRefreshTokenDto refreshToken = new CreateRefreshTokenDto("raw-refresh-token",
                LocalDateTime.now().plusDays(7));

        when(userService.getByLogin(login)).thenReturn(user);
        when(passwordEncoder.matches(password, "hashedPassword")).thenReturn(true);
        when(tokenService.createAccessToken(userId)).thenReturn("new-access-token");
        when(tokenService.createRefreshToken(userId)).thenReturn(refreshToken);

        var jwtProperties = mock(EasyKanConfig.JwtProperties.class);
        int accessTokenExpire = 60;
        int refreshTokenExpire = 3600;
        when(properties.jwt()).thenReturn(jwtProperties);
        when(jwtProperties.accessTokenExpire()).thenReturn(accessTokenExpire);
        when(jwtProperties.refreshTokenExpire()).thenReturn(refreshTokenExpire);
        when(properties.useHttps()).thenReturn(false);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value(login))
                .andExpect(jsonPath("$.displayName").value(displayName))
                .andExpect(cookie().exists("access_token"))
                .andExpect(cookie().value("access_token", "new-access-token"))
                .andExpect(cookie().maxAge("access_token", accessTokenExpire))
                .andExpect(cookie().httpOnly("access_token", true))
                .andExpect(cookie().exists("refresh_token"))
                .andExpect(cookie().value("refresh_token", "raw-refresh-token"))
                .andExpect(cookie().maxAge("refresh_token", refreshTokenExpire))
                .andExpect(cookie().httpOnly("refresh_token", true));

        verify(tokenService).createAccessToken(userId);
        verify(tokenService).createRefreshToken(userId);
    }

    @Test
    void login_shouldReturnNotFound_whenUserNotFound() throws Exception {
        AuthLoginRequest request = new AuthLoginRequest("nouser", "password123");
        when(userService.getByLogin("nouser"))
                .thenThrow(UserNotFoundException.byLogin("nouser"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void login_ShouldReturnUnauthorized_whenPasswordIsIncorrect() throws Exception {
        AuthLoginRequest request = new AuthLoginRequest("testuser", "wrongpassword");
        EKUser user = EKUser.builder()
                .id(UUID.randomUUID())
                .login("testuser")
                .passwordHash("hashedPassword")
                .build();

        when(userService.getByLogin("testuser")).thenReturn(user);
        when(passwordEncoder.matches("wrongpassword", "hashedPassword")).thenReturn(false);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void logout_shouldClearCookies_whenTokenIsPresent() throws Exception {
        String refreshToken = "some-refresh-token";

        mockMvc.perform(post("/api/v1/auth/logout")
                        .cookie(new Cookie("refresh_token", refreshToken)))
                .andExpect(status().isOk())
                .andExpect(cookie().maxAge("access_token", 0))
                .andExpect(cookie().maxAge("refresh_token", 0));

        verify(tokenService).logout(refreshToken);
    }

    @Test
    void logout_shouldClearCookies_whenTokenIsNotPresent() throws Exception {
        mockMvc.perform(post("/api/v1/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(cookie().maxAge("access_token", 0))
                .andExpect(cookie().maxAge("refresh_token", 0));

        verify(tokenService, never()).logout(anyString());
    }

    @Test
    void refresh_shouldReturnOkAndNewTokens_whenTokenIsValid() throws Exception {
        String oldRefreshToken = "old-valid-token";
        RotatedTokensDto rotatedTokens = new RotatedTokensDto(
                "new-access-token",
                "new-raw-refresh-token",
                LocalDateTime.now().plusDays(7));

        when(tokenService.rotateRefreshToken(oldRefreshToken)).thenReturn(Optional.of(rotatedTokens));

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .cookie(new Cookie("refresh_token", oldRefreshToken)))
                .andExpect(status().isOk())
                .andExpect(cookie().value("access_token", "new-access-token"))
                .andExpect(cookie().value("refresh_token", "new-raw-refresh-token"));
    }

    @Test
    void refresh_shouldReturnUnauthorized_whenTokenIsInvalid() throws Exception {
        String invalidRefreshToken = "invalid-token";
        when(tokenService.rotateRefreshToken(invalidRefreshToken)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .cookie(new Cookie("refresh_token", invalidRefreshToken)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void refresh_shouldReturnUnauthorized_whenNoTokenIsProvided() throws Exception {
        mockMvc.perform(post("/api/v1/auth/refresh"))
                .andExpect(status().isUnauthorized());
    }

}

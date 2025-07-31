package dev.erpix.easykan.server.controller;

import dev.erpix.easykan.server.config.EasyKanConfig;
import dev.erpix.easykan.server.domain.auth.dto.AuthLoginRequest;
import dev.erpix.easykan.server.domain.auth.dto.AuthUserResponse;
import dev.erpix.easykan.server.domain.token.dto.CreateRefreshTokenDto;
import dev.erpix.easykan.server.domain.user.model.EKUser;
import dev.erpix.easykan.server.domain.token.service.TokenService;
import dev.erpix.easykan.server.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;

@Tag(name = "Authentication",
        description = "Endpoints for user authentication and session management")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final EasyKanConfig properties;
    private final TokenService tokenService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Operation(
            summary = "Log in a user",
            description = "Authenticates a user with login and password.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful login"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "404", description = "User does not exist")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthUserResponse> login(@RequestBody AuthLoginRequest request) {
        EKUser user = userService.getByLogin(request.login());

        if (!user.isCanAuthWithPassword() && user.getPasswordHash() != null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String accessToken = tokenService.createAccessToken(user.getId());
        CreateRefreshTokenDto dto = tokenService.createRefreshToken(user.getId());

        AuthUserResponse res = new AuthUserResponse(user.getLogin(), user.getDisplayName());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, createCookie("access_token",
                        accessToken, Duration.ofSeconds(properties.jwt().accessTokenExpire())))
                .header(HttpHeaders.SET_COOKIE, createCookie("refresh_token",
                        dto.rawToken(), Duration.ofSeconds(properties.jwt().refreshTokenExpire())))
                .body(res);
    }

    @Operation(
            summary = "Log out the current session",
            description = "Invalidates the current refresh token and clears session cookies.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful logout")
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = "refresh_token", required = false) String refreshToken
    ) {
        if (refreshToken != null) {
            tokenService.logout(refreshToken);
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, createCookie("access_token", "",
                        Duration.ZERO))
                .header(HttpHeaders.SET_COOKIE, createCookie("refresh_token", "",
                        Duration.ZERO))
                .build();
    }

    @Operation(
            summary = "Log out all sessions",
            description = "Invalidates all active refresh tokens for the user and clears session cookies.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful logout from all sessions")
    })
    @PostMapping("/logout-all")
    public ResponseEntity<Void> logoutAll(
            @CookieValue(name = "refresh_token", required = false) String refreshToken) {
        if (refreshToken != null) {
            tokenService.logoutAll(refreshToken);
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, createCookie("access_token", "",
                        Duration.ZERO))
                .header(HttpHeaders.SET_COOKIE, createCookie("refresh_token", "",
                        Duration.ZERO))
                .build();
    }

    @Operation(
            summary = "Refresh access token",
            description = "Generates a new access token using the provided refresh token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tokens refreshed successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid or missing refresh token")
    })
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(
            @CookieValue(name = "refresh_token", required = false) String refreshToken) {
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return tokenService.rotateRefreshToken(refreshToken)
                .map(rotatedTokens -> ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, createCookie("access_token",
                                rotatedTokens.newAccessToken(), Duration.ofMinutes(15)))
                        .header(HttpHeaders.SET_COOKIE, createCookie("refresh_token",
                                rotatedTokens.newRawRefreshToken(),
                                Duration.between(LocalDateTime.now(), rotatedTokens.newRefreshTokenExpiration())))
                        .build())
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    private String createCookie(String name, String value, Duration maxAge) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(properties.useHttps())
                .path("/")
                .sameSite("Strict")
                .maxAge(maxAge)
                .build()
                .toString();
    }

}

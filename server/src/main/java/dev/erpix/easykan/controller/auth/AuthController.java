package dev.erpix.easykan.controller.auth;

import dev.erpix.easykan.config.EasyKanProperties;
import dev.erpix.easykan.model.auth.dto.AuthRequest;
import dev.erpix.easykan.model.token.dto.CreateRefreshTokenDto;
import dev.erpix.easykan.service.TokenService;
import dev.erpix.easykan.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final EasyKanProperties properties;
    private final TokenService tokenService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody AuthRequest request) {
        return userService.getByEmail(request.email())
                .filter(user -> passwordEncoder.matches(request.password(), user.getPassword()))
                .map(user -> {
                    String accessToken = tokenService.createAccessToken(user.getId());
                    CreateRefreshTokenDto dto = tokenService.createRefreshToken(user.getId());

                    return ResponseEntity.ok()
                            .header(HttpHeaders.SET_COOKIE, createCookie("access_token",
                                    accessToken, Duration.ofMinutes(15)))
                            .header(HttpHeaders.SET_COOKIE, createCookie("refresh_token",
                                    dto.rawToken(),
                                    Duration.ofDays(7)))
                            .build();
                })
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

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

    @PostMapping("/refresh")
    @Operation(
            summary = "Refresh access token",
            description = "Generates a new access token using the provided refresh token. " +
                    "If the refresh token is valid, a new access token is returned in a cookie. " +
                    "If the refresh token is invalid or expired, an unauthorized response is returned.")
    @ApiResponse(
            responseCode = "200",
            description = "Access token refreshed successfully. New access token is returned in a cookie.")
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

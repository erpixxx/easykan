package dev.erpix.easykan.server.controller;

import dev.erpix.easykan.server.config.EasyKanConfig;
import dev.erpix.easykan.server.domain.auth.dto.AuthLoginRequestDto;
import dev.erpix.easykan.server.domain.auth.dto.UserAndTokenPairResponseDto;
import dev.erpix.easykan.server.domain.auth.service.AuthService;
import dev.erpix.easykan.server.domain.token.dto.TokenPairDto;
import dev.erpix.easykan.server.domain.token.service.TokenService;
import dev.erpix.easykan.server.domain.user.dto.UserResponseDto;
import dev.erpix.easykan.server.domain.user.security.JpaUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication",
        description = "Endpoints for user authentication and session management")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final EasyKanConfig config;
    private final AuthService authService;
    private final TokenService tokenService;

    @Operation(summary = "Log in with password",
            description = "Authenticates a user with login and password.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Successful login"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "404", description = "User does not exist")})
    @PostMapping("/login")
    public ResponseEntity<UserResponseDto> login(@RequestBody AuthLoginRequestDto request) {
        UserAndTokenPairResponseDto result = authService.loginWithPassword(request);
        TokenPairDto tokenPairDto = result.tokenPair();
        UserResponseDto user = result.user();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE,
                        createCookie("access_token", tokenPairDto.newAccessToken(),
                                tokenPairDto.newAccessTokenDuration()))
                .header(HttpHeaders.SET_COOKIE, createCookie("refresh_token",
                        tokenPairDto.newRawRefreshToken(), tokenPairDto.newRefreshTokenDuration()))
                .body(user);
    }

    @Operation(summary = "Log out the current session",
            description = "Invalidates the current refresh token and clears session cookies.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Successful logout")})
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = "refresh_token", required = false) String refreshToken) {
        tokenService.logout(refreshToken);
        return tokenClearResponse();
    }

    @Operation(summary = "Log out all sessions",
            description = "Invalidates all active refresh tokens for the user and clears session cookies.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful logout from all sessions"),
            @ApiResponse(responseCode = "401", description = "Invalid or missing refresh token")})
    @PostMapping("/logout-all")
    public ResponseEntity<Void> logoutAll(@AuthenticationPrincipal JpaUserDetails userDetails) {
        tokenService.logoutAll(userDetails);
        return tokenClearResponse();
    }

    @Operation(summary = "Refresh access token",
            description = "Generates a new access token using the provided refresh token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tokens refreshed successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid or missing refresh token")})
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@CookieValue(name = "refresh_token") String refreshToken) {
        TokenPairDto tokenPairDto = tokenService.rotateRefreshToken(refreshToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE,
                        createCookie("access_token", tokenPairDto.newAccessToken(),
                                tokenPairDto.newAccessTokenDuration()))
                .header(HttpHeaders.SET_COOKIE, createCookie("refresh_token",
                        tokenPairDto.newRawRefreshToken(), tokenPairDto.newRefreshTokenDuration()))
                .build();
    }

    private String createCookie(String name, String value, Duration maxAge) {
        return ResponseCookie.from(name, value).httpOnly(true).secure(config.useHttps()).path("/")
                .sameSite("Strict").maxAge(maxAge).build().toString();
    }

    private ResponseEntity<Void> tokenClearResponse() {
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, createCookie("access_token", "", Duration.ZERO))
                .header(HttpHeaders.SET_COOKIE, createCookie("refresh_token", "", Duration.ZERO))
                .build();
    }
}

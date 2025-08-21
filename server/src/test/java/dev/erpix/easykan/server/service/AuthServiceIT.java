package dev.erpix.easykan.server.service;

import dev.erpix.easykan.server.config.EasyKanConfig;
import dev.erpix.easykan.server.domain.auth.dto.AuthLoginRequestDto;
import dev.erpix.easykan.server.domain.auth.service.AuthService;
import dev.erpix.easykan.server.domain.token.dto.TokenPairDto;
import dev.erpix.easykan.server.domain.token.model.RefreshToken;
import dev.erpix.easykan.server.domain.token.repository.TokenRepository;
import dev.erpix.easykan.server.domain.token.service.JwtProvider;
import dev.erpix.easykan.server.domain.user.model.User;
import dev.erpix.easykan.server.domain.user.repository.UserRepository;
import dev.erpix.easykan.server.exception.UnsupportedAuthenticationMethodException;
import dev.erpix.easykan.server.exception.UserNotFoundException;
import dev.erpix.easykan.server.testsupport.Category;
import dev.erpix.easykan.server.testsupport.config.TestcontainersConfig;
import dev.erpix.easykan.server.testsupport.annotation.WithPersistedUser;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag(Category.INTEGRATION_TEST)
@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfig.class)
@Transactional
public class AuthServiceIT {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EasyKanConfig config;

    @Autowired
    private JwtProvider jwtProvider;

    @Test
    @WithPersistedUser
    void loginWithPassword_shouldReturnTokens_whenCredentialsAreValid() {
        User testUser = userRepository.findByLogin(WithPersistedUser.Default.LOGIN)
                .orElseThrow();

        AuthLoginRequestDto requestDto = new AuthLoginRequestDto(
                WithPersistedUser.Default.LOGIN,
                WithPersistedUser.Default.PASSWORD);

        TokenPairDto result = authService.loginWithPassword(requestDto);

        assertThat(result).isNotNull();

        // Access token assertions
        assertThat(result.newAccessToken())
                .isNotBlank();
        assertThat(result.newAccessTokenDuration().getSeconds())
                .isEqualTo(config.jwt().accessTokenExpire());
        assertThat(jwtProvider.validate(result.newAccessToken()))
                .isEqualTo(testUser.getId().toString());

        // Refresh token assertions
        assertThat(result.newRawRefreshToken())
                .isNotBlank();
        assertThat(result.newRefreshTokenDuration().getSeconds())
                .isEqualTo(config.jwt().refreshTokenExpire());
        String[] tokenParts = result.newRawRefreshToken().split(":");
        assertThat(tokenParts).hasSize(2);
        String selector = tokenParts[0];
        String rawValidator = tokenParts[1];
        RefreshToken tokenFromDb = tokenRepository.findBySelector(selector)
                .orElseThrow(() -> new AssertionError("Refresh token not found in database"));
        assertThat(tokenFromDb.getUser().getId())
                .isEqualTo(testUser.getId());
        assertThat(tokenFromDb.getExpiresAt())
                .isAfter(Instant.now());
        assertThat(passwordEncoder.matches(rawValidator, tokenFromDb.getValidator()))
                .isTrue();
    }

    @Test
    @WithPersistedUser
    void loginWithPassword_shouldThrowBadCredentialsException_whenCredentialsAreInvalid() {
        AuthLoginRequestDto requestDto = new AuthLoginRequestDto(
                WithPersistedUser.Default.LOGIN,
                "wrongpassword");

        assertThatThrownBy(() -> authService.loginWithPassword(requestDto))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    @WithPersistedUser
    void loginWithPassword_shouldThrowUnsupportedAuthenticationMethodException_whenUserHasNoPassword() {
        User testUser = userRepository.findByLogin(WithPersistedUser.Default.LOGIN)
                .orElseThrow();
        testUser.setPasswordHash(null);
        userRepository.save(testUser);

        AuthLoginRequestDto requestDto = new AuthLoginRequestDto(
                WithPersistedUser.Default.LOGIN,
                WithPersistedUser.Default.PASSWORD);

        assertThatThrownBy(() -> authService.loginWithPassword(requestDto))
                .isInstanceOf(UnsupportedAuthenticationMethodException.class);
    }

    @Test
    void loginWithPassword_shouldThrowUserNotFoundException_whenUserDoesNotExist() {
        AuthLoginRequestDto requestDto = new AuthLoginRequestDto(
                WithPersistedUser.Default.LOGIN,
                WithPersistedUser.Default.PASSWORD);

        assertThatThrownBy(() -> authService.loginWithPassword(requestDto))
                .isInstanceOf(UserNotFoundException.class);
    }

}

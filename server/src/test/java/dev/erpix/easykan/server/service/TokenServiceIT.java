package dev.erpix.easykan.server.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import dev.erpix.easykan.server.config.EasyKanConfig;
import dev.erpix.easykan.server.domain.token.model.RefreshToken;
import dev.erpix.easykan.server.domain.token.repository.TokenRepository;
import dev.erpix.easykan.server.domain.token.security.TokenGenerator;
import dev.erpix.easykan.server.domain.token.security.TokenParts;
import dev.erpix.easykan.server.domain.token.service.TokenService;
import dev.erpix.easykan.server.domain.user.security.JpaUserDetails;
import dev.erpix.easykan.server.domain.user.service.UserService;
import dev.erpix.easykan.server.testsupport.Category;
import dev.erpix.easykan.server.testsupport.annotation.IntegrationTest;
import dev.erpix.easykan.server.testsupport.annotation.WithPersistedUser;
import java.time.Instant;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@Tag(Category.INTEGRATION_TEST)
@IntegrationTest
public class TokenServiceIT {

	@Autowired
	private TokenService tokenService;

	@Autowired
	private EasyKanConfig config;

	@Autowired
	private TokenRepository tokenRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@MockitoBean
	private TokenGenerator tokenGenerator;

	private static final String SAMPLE_SELECTOR = "selector";

	private static final String SAMPLE_VALIDATOR = "validator";

	@Test
	@WithPersistedUser
	void createRefreshToken_shouldSaveRefreshTokenAndReturnDto() {
		var user = userService.getByLogin(WithPersistedUser.Default.LOGIN);

		when(tokenGenerator.generate()).thenReturn(new TokenParts(SAMPLE_SELECTOR, SAMPLE_VALIDATOR));

		var tokenDto = tokenService.createRefreshToken(user.getId());
		var savedToken = tokenRepository.findBySelector(SAMPLE_SELECTOR)
			.orElseThrow(() -> new AssertionError("Refresh token not found in repository"));

		assertThat(tokenDto.combine()).isNotBlank();
		assertThat(tokenDto.duration().getSeconds()).isEqualTo(config.jwt().refreshTokenExpire());
		assertThat(passwordEncoder.matches(SAMPLE_VALIDATOR, savedToken.getValidator())).isTrue();
		assertThat(savedToken.getUser().getId()).isEqualTo(user.getId());
		assertThat(savedToken.getExpiresAt()).isAfter(Instant.now());
	}

	@Test
	@WithPersistedUser
	void logout_shouldRevokeCurrentUserToken() {
		var user = userService.getByLogin(WithPersistedUser.Default.LOGIN);

		when(tokenGenerator.generate()).thenReturn(new TokenParts(SAMPLE_SELECTOR, SAMPLE_VALIDATOR));

		var tokenDto = tokenService.createRefreshToken(user.getId());
		var tokenBeforeLogout = tokenRepository.findBySelector(SAMPLE_SELECTOR)
			.orElseThrow(() -> new AssertionError("Token not found in repository"));

		assertThat(tokenBeforeLogout.isRevoked()).isFalse();

		tokenService.logout(tokenDto.combine());

		var revokedToken = tokenRepository.findBySelector(SAMPLE_SELECTOR)
			.orElseThrow(() -> new AssertionError("Token not found in repository"));

		assertThat(revokedToken.isRevoked()).isTrue();
	}

	@Test
	@WithPersistedUser
	void logout_shouldDoNothing_whenValidatorIsIncorrect() {
		var user = userService.getByLogin(WithPersistedUser.Default.LOGIN);

		when(tokenGenerator.generate()).thenReturn(new TokenParts(SAMPLE_SELECTOR, SAMPLE_VALIDATOR));

		tokenService.createRefreshToken(user.getId());
		String tokenWithInvalidValidator = SAMPLE_SELECTOR + ":wrong-validator";

		tokenService.logout(tokenWithInvalidValidator);

		var tokenInDb = tokenRepository.findBySelector(SAMPLE_SELECTOR).orElseThrow();

		assertThat(tokenInDb.isRevoked()).isFalse();
	}

	@Test
	@WithPersistedUser
	void logout_shouldDoNothing_whenTokenIsAlreadyRevoked() {
		var user = userService.getByLogin(WithPersistedUser.Default.LOGIN);

		when(tokenGenerator.generate()).thenReturn(new TokenParts(SAMPLE_SELECTOR, SAMPLE_VALIDATOR));

		var tokenDto = tokenService.createRefreshToken(user.getId());

		var tokenToRevoke = tokenRepository.findBySelector(SAMPLE_SELECTOR).orElseThrow();
		tokenToRevoke.setRevoked(true);
		tokenRepository.saveAndFlush(tokenToRevoke);

		tokenService.logout(tokenDto.combine());

		var tokenInDb = tokenRepository.findBySelector(SAMPLE_SELECTOR).orElseThrow();
		assertThat(tokenInDb.isRevoked()).isTrue();
	}

	@Test
	@WithPersistedUser
	void logoutAll_shouldRevokeAllTokensForUser() {
		var user = userService.getByLogin(WithPersistedUser.Default.LOGIN);
		int numberOfTokens = 3;

		for (int i = 0; i < numberOfTokens; i++) {
			tokenRepository.save(RefreshToken.builder()
				.selector("selector" + i)
				.validator("validator" + i)
				.user(user)
				.expiresAt(Instant.now().plusSeconds(config.jwt().refreshTokenExpire()))
				.revoked(false)
				.build());
		}

		var tokensBeforeLogout = tokenRepository.findByUserAndRevokedFalse(user);
		assertThat(tokensBeforeLogout).hasSize(numberOfTokens);

		tokenService.logoutAll(new JpaUserDetails(user));
		var revokedTokens = tokenRepository.findByUserAndRevokedTrue(user);

		assertThat(revokedTokens).hasSize(numberOfTokens);
		assertThat(tokenRepository.findByUserAndRevokedFalse(user)).isEmpty();
	}

	@Test
	@WithPersistedUser
	void rotateRefreshToken_shouldRevokeOldTokenAndCreateNewOne() {
		var user = userService.getByLogin(WithPersistedUser.Default.LOGIN);

		when(tokenGenerator.generate()).thenReturn(new TokenParts(SAMPLE_SELECTOR, SAMPLE_VALIDATOR));

		var oldTokenDto = tokenService.createRefreshToken(user.getId());
		var oldToken = tokenRepository.findBySelector(SAMPLE_SELECTOR)
			.orElseThrow(() -> new AssertionError("Old token not found in repository"));

		assertThat(oldToken.isRevoked()).isFalse();
		assertThat(oldToken.getUser().getId()).isEqualTo(user.getId());
		assertThat(oldToken.getExpiresAt()).isAfter(Instant.now());
		assertThat(passwordEncoder.matches(SAMPLE_VALIDATOR, oldToken.getValidator())).isTrue();

		when(tokenGenerator.generate()).thenReturn(new TokenParts("newSelector", "newValidator"));

		var newTokenPair = tokenService.rotateRefreshToken(oldTokenDto.combine());

		assertThat(newTokenPair.newRawRefreshToken()).isNotBlank();
		assertThat(newTokenPair.newRefreshTokenDuration().getSeconds()).isEqualTo(config.jwt().refreshTokenExpire());
		String[] newTokenParts = newTokenPair.newRawRefreshToken().split(":");
		assertThat(newTokenParts).hasSize(2);

		String newSelector = newTokenParts[0];
		String newRawValidator = newTokenParts[1];
		RefreshToken newTokenFromDb = tokenRepository.findBySelector(newSelector)
			.orElseThrow(() -> new AssertionError("New refresh token not found in database"));

		assertThat(newTokenFromDb.getUser().getId()).isEqualTo(user.getId());
		assertThat(newTokenFromDb.getExpiresAt()).isAfter(Instant.now());
		assertThat(passwordEncoder.matches(newRawValidator, newTokenFromDb.getValidator())).isTrue();
		assertThat(oldToken.isRevoked()).isTrue();
	}

}

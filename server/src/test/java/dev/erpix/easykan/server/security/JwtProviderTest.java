package dev.erpix.easykan.server.security;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import dev.erpix.easykan.server.config.EasyKanConfig;
import dev.erpix.easykan.server.domain.token.service.JwtProvider;
import dev.erpix.easykan.server.exception.auth.InvalidTokenException;
import dev.erpix.easykan.server.testsupport.Category;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag(Category.UNIT_TEST)
public class JwtProviderTest {

	private JwtProvider jwtProvider;

	@BeforeEach
	public void setUp() {
		EasyKanConfig mockConfig = mock(EasyKanConfig.class);
		EasyKanConfig.Jwt mockJwt = mock(EasyKanConfig.Jwt.class);

		when(mockConfig.jwt()).thenReturn(mockJwt);
		when(mockJwt.secret()).thenReturn("msrzFGLW0mLB1TlEvwu3MlYMqjGXPAH9sSU3LJlvTxA=");
		when(mockJwt.accessTokenExpire()).thenReturn(3600);

		jwtProvider = new JwtProvider(mockConfig);
		jwtProvider.init();
	}

	@Test
	void generateAndValidate_shouldReturnSubject_whenTokenIsValid() {
		String subject = UUID.randomUUID().toString();

		String token = jwtProvider.generate(subject);
		String validatedSubject = jwtProvider.validate(token);

		assertThat(token).isNotNull();
		assertThat(validatedSubject).isEqualTo(subject);
	}

	@Test
	void validate_shouldThrowException_whenTokenIsSignedWithDifferentKey() {
		String subject = UUID.randomUUID().toString();
		String token = jwtProvider.generate(subject);

		EasyKanConfig otherConfig = mock(EasyKanConfig.class);
		EasyKanConfig.Jwt otherJwt = mock(EasyKanConfig.Jwt.class);
		when(otherConfig.jwt()).thenReturn(otherJwt);
		when(otherJwt.secret()).thenReturn("other-super-secure-token-which-is-not-the-same-as-the-original");
		JwtProvider otherProvider = new JwtProvider(otherConfig);
		otherProvider.init();

		assertThrows(InvalidTokenException.class, () -> otherProvider.validate(token));
	}

	@Test
	void validate_shouldThrowException_whenTokenIsMalformed() {
		String malformedToken = "not.a.real.token";

		assertThrows(InvalidTokenException.class, () -> jwtProvider.validate(malformedToken));
	}

	@Test
	void validate_shouldThrowException_whenTokenIsExpired() throws InterruptedException {
		EasyKanConfig shortLivedConfig = mock(EasyKanConfig.class);
		EasyKanConfig.Jwt shortLivedJwt = mock(EasyKanConfig.Jwt.class);
		when(shortLivedConfig.jwt()).thenReturn(shortLivedJwt);
		when(shortLivedJwt.secret()).thenReturn("msrzFGLW0mLB1TlEvwu3MlYMqjGXPAH9sSU3LJlvTxA=");
		when(shortLivedJwt.accessTokenExpire()).thenReturn(1);

		JwtProvider shortLivedProvider = new JwtProvider(shortLivedConfig);
		shortLivedProvider.init();

		String subject = UUID.randomUUID().toString();
		String token = shortLivedProvider.generate(subject);

		Thread.sleep(1001);

		assertThrows(InvalidTokenException.class, () -> shortLivedProvider.validate(token));
	}

}

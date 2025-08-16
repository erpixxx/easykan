package dev.erpix.easykan.server.security;

import dev.erpix.easykan.server.config.EasyKanConfig;
import dev.erpix.easykan.server.domain.token.service.JwtProvider;
import dev.erpix.easykan.server.exception.InvalidTokenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JwtProviderTest {

    private JwtProvider jwtProvider;

    @BeforeEach
    public void setUp() {
        EasyKanConfig mockConfig = mock(EasyKanConfig.class);
        EasyKanConfig.JwtProperties mockJwtProperties = mock(EasyKanConfig.JwtProperties.class);

        when(mockConfig.jwt())
                .thenReturn(mockJwtProperties);
        when(mockJwtProperties.secret())
                .thenReturn("msrzFGLW0mLB1TlEvwu3MlYMqjGXPAH9sSU3LJlvTxA=");
        when(mockJwtProperties.accessTokenExpire())
                .thenReturn(3600);

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
        EasyKanConfig.JwtProperties otherJwtProperties = mock(EasyKanConfig.JwtProperties.class);
        when(otherConfig.jwt())
                .thenReturn(otherJwtProperties);
        when(otherJwtProperties.secret())
                .thenReturn("other-super-secure-token-which-is-not-the-same-as-the-original");
        JwtProvider otherProvider = new JwtProvider(otherConfig);
        otherProvider.init();

        assertThrows(InvalidTokenException.class, () ->
                otherProvider.validate(token));
    }

    @Test
    void validate_shouldThrowException_whenTokenIsMalformed() {
        String malformedToken = "not.a.real.token";

        assertThrows(InvalidTokenException.class, () ->
                jwtProvider.validate(malformedToken));
    }

    @Test
    void validate_shouldThrowException_whenTokenIsExpired() throws InterruptedException {
        EasyKanConfig shortLivedConfig = mock(EasyKanConfig.class);
        EasyKanConfig.JwtProperties shortLivedJwtProperties = mock(EasyKanConfig.JwtProperties.class);
        when(shortLivedConfig.jwt())
                .thenReturn(shortLivedJwtProperties);
        when(shortLivedJwtProperties.secret())
                .thenReturn("msrzFGLW0mLB1TlEvwu3MlYMqjGXPAH9sSU3LJlvTxA=");
        when(shortLivedJwtProperties.accessTokenExpire())
                .thenReturn(1);

        JwtProvider shortLivedProvider = new JwtProvider(shortLivedConfig);
        shortLivedProvider.init();

        String subject = UUID.randomUUID().toString();
        String token = shortLivedProvider.generate(subject);

        Thread.sleep(1001);

        assertThrows(InvalidTokenException.class, () ->
                shortLivedProvider.validate(token));
    }

}

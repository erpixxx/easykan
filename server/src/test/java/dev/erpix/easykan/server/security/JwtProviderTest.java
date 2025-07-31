package dev.erpix.easykan.server.security;

import dev.erpix.easykan.server.config.EasyKanConfig;
import dev.erpix.easykan.server.domain.auth.service.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JwtProviderTest {

    private JwtProvider jwtProvider;

    @BeforeEach
    public void setUp() {
        EasyKanConfig mockConfig = mock(EasyKanConfig.class);
        EasyKanConfig.JwtProperties mockJwtProperties = mock(EasyKanConfig.JwtProperties.class);

        when(mockConfig.jwt()).thenReturn(mockJwtProperties);
        when(mockJwtProperties.secret()).thenReturn("msrzFGLW0mLB1TlEvwu3MlYMqjGXPAH9sSU3LJlvTxA=");
        when(mockJwtProperties.accessTokenExpire()).thenReturn(3600);

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
    void validate_shouldReturnNull_whenTokenIsSignedWithDifferentKey() {
        String subject = UUID.randomUUID().toString();
        String token = jwtProvider.generate(subject);

        EasyKanConfig otherConfig = mock(EasyKanConfig.class);
        EasyKanConfig.JwtProperties otherJwtProperties = mock(EasyKanConfig.JwtProperties.class);
        when(otherConfig.jwt()).thenReturn(otherJwtProperties);
        when(otherJwtProperties.secret()).thenReturn("inny-bardzo-dlugi-i-bezpieczny-sekret-ktory-ma-wystarczajaco-duzo-bitow");
        JwtProvider otherProvider = new JwtProvider(otherConfig);
        otherProvider.init();

        String validatedSubject = otherProvider.validate(token);

        assertThat(validatedSubject).isNull();
    }

    @Test
    void validate_shouldReturnNull_whenTokenIsMalformed() {
        String malformedToken = "not.a.real.token";

        String validatedSubject = jwtProvider.validate(malformedToken);

        assertThat(validatedSubject).isNull();
    }
}

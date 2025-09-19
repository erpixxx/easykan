package dev.erpix.easykan.server.domain.token.security;

import java.security.SecureRandom;
import java.util.Base64;
import org.springframework.stereotype.Component;

@Component
public class TokenGenerator {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final int SELECTOR_LENGTH_BYTES = 16;
    private static final int VALIDATOR_LENGTH_BYTES = 32;

    /**
     * Generates a new token consisting of a selector and a validator.
     *
     * @return {@link TokenParts} containing the selector and validator.
     */
    public TokenParts generate() {
        String selector = generateRandomString(SELECTOR_LENGTH_BYTES);
        String validator = generateRandomString(VALIDATOR_LENGTH_BYTES);
        return new TokenParts(selector, validator);
    }

    private String generateRandomString(int bytes) {
        byte[] randomBytes = new byte[bytes];
        SECURE_RANDOM.nextBytes(randomBytes);
        return ENCODER.encodeToString(randomBytes);
    }
}

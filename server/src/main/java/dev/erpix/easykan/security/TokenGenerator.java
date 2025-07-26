package dev.erpix.easykan.security;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

@Component
public class TokenGenerator {

    private static final SecureRandom random = new SecureRandom();
    private static final Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();

    /**
     * Generates a new token consisting of a selector and a validator.
     *
     * @return {@link TokenParts} containing the selector and validator.
     */
    public TokenParts generate() {
        String selector = generateRandomString(16);
        String validator = generateRandomString(32);
        return new TokenParts(selector, validator);
    }

    private String generateRandomString(int bytes) {
        byte[] randomBytes = new byte[bytes];
        random.nextBytes(randomBytes);
        return encoder.encodeToString(randomBytes);
    }

}

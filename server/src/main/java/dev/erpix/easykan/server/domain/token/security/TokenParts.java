package dev.erpix.easykan.server.domain.token.security;

import org.jetbrains.annotations.NotNull;

/**
 * Represents the parts of a token in format {@code selector:validator}.
 *
 * @param selector
 * @param validator
 */
public record TokenParts(
        @NotNull String selector,
        @NotNull String validator
) {

    public @NotNull String combine() {
        return selector + ':' + validator;
    }

}

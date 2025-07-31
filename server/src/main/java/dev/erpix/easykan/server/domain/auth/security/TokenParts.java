package dev.erpix.easykan.server.domain.auth.security;

import org.jetbrains.annotations.NotNull;

/**
 * Represents the parts of a token.
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

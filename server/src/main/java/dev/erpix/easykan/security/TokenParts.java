package dev.erpix.easykan.security;

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

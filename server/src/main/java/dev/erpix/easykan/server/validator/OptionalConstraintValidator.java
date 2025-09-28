package dev.erpix.easykan.server.validator;

import java.util.Optional;
import java.util.function.Function;

public interface OptionalConstraintValidator<T> {

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	default boolean validateIfPresent(Optional<T> value, Function<T, Boolean> nextValidation) {
		return value.map(nextValidation).orElse(true);
	}

}

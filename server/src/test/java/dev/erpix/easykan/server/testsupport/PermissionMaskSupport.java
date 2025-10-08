package dev.erpix.easykan.server.testsupport;

import dev.erpix.easykan.server.domain.PermissionMask;

import java.util.Arrays;
import java.util.stream.Stream;

public final class PermissionMaskSupport {

	public static <T extends Enum<T> & PermissionMask> Stream<Long> generateInvalidPermissions(Class<T> enumType) {
		long allValidBits = Arrays.stream(enumType.getEnumConstants())
			.mapToLong(PermissionMask::getValue)
			.reduce(0, (a, b) -> a | b);

		long invalidBit = (allValidBits == 0) ? 1L : Long.highestOneBit(allValidBits) << 1;

		return Stream.of(-1L, // Negative value
				invalidBit, // Single invalid bit
				allValidBits | invalidBit // All valid bits plus one invalid bit
		);
	}

}

package dev.erpix.easykan.server.domain.user.security;

import dev.erpix.easykan.server.domain.user.model.User;
import dev.erpix.easykan.server.domain.user.model.UserPermission;
import dev.erpix.easykan.server.domain.user.util.UserDetailsProvider;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class RequireUserPermissionAspect {

	private final UserDetailsProvider userDetailsProvider;

	@Before("@annotation(requireUserPermission)")
	public void checkPermission(RequireUserPermission requireUserPermission) {
		UserPermission[] required = requireUserPermission.value();

		JpaUserDetails userDetails = userDetailsProvider.getCurrentUserDetails()
			.orElseThrow(() -> new AccessDeniedException("User not authenticated"));

		User user = userDetails.user();

		if (UserPermission.hasPermission(user, UserPermission.ADMIN)) {
			// Admins bypass permission checks
			return;
		}

		for (UserPermission permission : required) {
			if (!UserPermission.hasPermission(user, permission)) {
				throw new AccessDeniedException("User does not have required permission: " + permission);
			}
		}
	}

}

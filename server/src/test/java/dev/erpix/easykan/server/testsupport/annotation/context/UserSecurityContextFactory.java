package dev.erpix.easykan.server.testsupport.annotation.context;

import dev.erpix.easykan.server.domain.PermissionUtils;
import dev.erpix.easykan.server.domain.user.model.User;
import dev.erpix.easykan.server.domain.user.security.JpaUserDetails;
import dev.erpix.easykan.server.testsupport.annotation.WithSecurityContextUser;
import java.util.UUID;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class UserSecurityContextFactory implements WithSecurityContextFactory<WithSecurityContextUser> {

	@Override
	public SecurityContext createSecurityContext(WithSecurityContextUser annotation) {
		SecurityContext context = SecurityContextHolder.createEmptyContext();

		User user = User.builder()
			.id(UUID.fromString(annotation.id()))
			.login(annotation.login())
			.displayName(annotation.displayName())
			.email(annotation.email())
			.passwordHash(annotation.password())
			.permissions(PermissionUtils.toValue(annotation.permissions()))
			.build();

		JpaUserDetails userDetails = new JpaUserDetails(user);

		var auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

		context.setAuthentication(auth);
		return context;
	}

}

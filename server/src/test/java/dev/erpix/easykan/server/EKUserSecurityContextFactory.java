package dev.erpix.easykan.server;

import dev.erpix.easykan.server.domain.user.model.EKUser;
import dev.erpix.easykan.server.domain.user.model.Role;
import dev.erpix.easykan.server.domain.user.security.EKUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Arrays;
import java.util.UUID;

public class EKUserSecurityContextFactory implements WithSecurityContextFactory<WithMockEKUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockEKUser annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        EKUser user = EKUser.builder()
                .id(UUID.fromString(annotation.id()))
                .login(annotation.name())
                .displayName(annotation.displayName())
                .email(annotation.email())
                .passwordHash(annotation.password())
                .isAdmin(Arrays.stream(annotation.roles())
                        .anyMatch(r -> r == Role.ROLE_ADMIN))
                .canAuthWithPassword(annotation.canAuthWithPassword())
                .build();

        EKUserDetails userDetails = new EKUserDetails(user);

        var auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        context.setAuthentication(auth);
        return context;
    }

}

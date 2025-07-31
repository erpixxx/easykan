package dev.erpix.easykan.security;

import dev.erpix.easykan.model.user.Role;
import org.springframework.stereotype.Component;

@Component("roles")
public final class SecurityRole {

    public static final String USER = Role.ROLE_USER.getAuthority();
    public static final String ADMIN = Role.ROLE_ADMIN.getAuthority();

}

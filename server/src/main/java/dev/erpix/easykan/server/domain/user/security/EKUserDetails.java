package dev.erpix.easykan.server.domain.user.security;

import dev.erpix.easykan.server.domain.user.model.EKUser;
import dev.erpix.easykan.server.domain.user.model.Role;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class EKUserDetails implements UserDetails {

    private final EKUser user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.isAdmin() ?
                List.of(Role.ROLE_ADMIN, Role.ROLE_USER) :
                List.of(Role.ROLE_USER);
    }

    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return user.getLogin();
    }

}

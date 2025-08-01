package dev.erpix.easykan.server.domain.user.security;

import dev.erpix.easykan.server.domain.user.model.EKUser;
import dev.erpix.easykan.server.domain.user.model.UserPermission;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class EKUserDetails implements UserDetails {

    private final EKUser user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return UserPermission.fromValue(user.getPermissions());
    }

    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return user.getLogin();
    }

    public UUID getId() {
        return user.getId();
    }

}

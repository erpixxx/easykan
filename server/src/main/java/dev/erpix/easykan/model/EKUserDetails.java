package dev.erpix.easykan.model;

import dev.erpix.easykan.model.user.EKUser;
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
                List.of(Role.ADMIN, Role.USER) :
                List.of(Role.USER);
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

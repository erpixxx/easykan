package dev.erpix.easykan.server.domain.user.security;

import dev.erpix.easykan.server.domain.user.model.User;
import dev.erpix.easykan.server.domain.user.model.UserPermission;
import java.util.Collection;
import java.util.UUID;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public record JpaUserDetails(User user)implements UserDetails{

@Override public Collection<?extends GrantedAuthority>getAuthorities(){return UserPermission.fromValue(user.getPermissions());}

@Override public String getPassword(){return user.getPasswordHash();}

@Override public String getUsername(){return user.getLogin();}

public UUID getId(){return user.getId();}}

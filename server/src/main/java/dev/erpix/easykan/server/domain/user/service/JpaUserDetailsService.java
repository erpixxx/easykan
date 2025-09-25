package dev.erpix.easykan.server.domain.user.service;

import dev.erpix.easykan.server.domain.user.security.JpaUserDetails;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JpaUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new JpaUserDetails(userService.getByLogin(username));
    }

    public UserDetails loadUserById(UUID userId) throws UsernameNotFoundException {
        return new JpaUserDetails(userService.getById(userId));
    }
}

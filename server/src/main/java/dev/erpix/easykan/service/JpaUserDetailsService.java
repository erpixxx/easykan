package dev.erpix.easykan.service;

import dev.erpix.easykan.exception.UserNotFoundException;
import dev.erpix.easykan.model.EKUserDetails;
import dev.erpix.easykan.model.user.EKUser;
import dev.erpix.easykan.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JpaUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        EKUser user = userRepository.findByLogin(username)
                .orElseThrow(() -> UserNotFoundException.byLogin(username));
        return new EKUserDetails(user);
    }

    public UserDetails loadUserById(UUID userId) throws UsernameNotFoundException {
        EKUser user = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.byId(userId));
        return new EKUserDetails(user);
    }
}

package dev.erpix.easykan.server.domain.user.service;

import dev.erpix.easykan.server.constant.CacheKey;
import dev.erpix.easykan.server.exception.UserNotFoundException;
import dev.erpix.easykan.server.domain.user.model.EKUser;
import dev.erpix.easykan.server.domain.user.dto.UserCreateRequestDto;
import dev.erpix.easykan.server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public @NotNull EKUser create(@NotNull UserCreateRequestDto dto) {
        EKUser user = dto.toUser();
        if (user.getPasswordHash() != null)
            user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        return userRepository.save(user);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN') and #userId != authentication.principal.getId()")
    public void deleteUser(@NotNull UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw UserNotFoundException.byId(userId);
        }
        userRepository.deleteById(userId);
    }

    public @NotNull List<EKUser> getAllUsers() {
        return userRepository.findAll();
    }

    @Cacheable(value = CacheKey.USERS, key = "#userId")
    public @NotNull EKUser getById(@NotNull UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.byId(userId));
    }

    public @NotNull EKUser getByLogin(@NotNull String login) {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> UserNotFoundException.byLogin(login));
    }

    public @NotNull EKUser getByEmail(@NotNull String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> UserNotFoundException.byEmail(email));
    }

}

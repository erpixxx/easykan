package dev.erpix.easykan.service;

import dev.erpix.easykan.CacheKey;
import dev.erpix.easykan.exception.ResourceNotFoundException;
import dev.erpix.easykan.exception.UserNotFoundException;
import dev.erpix.easykan.model.user.EKUser;
import dev.erpix.easykan.model.user.dto.UserCreateRequestDto;
import dev.erpix.easykan.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public @NotNull EKUser create(@NotNull UserCreateRequestDto dto) {
        EKUser user = dto.toUser();
        if (user.getPasswordHash() != null)
            user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        return userRepository.save(user);
    }

    public void deleteUser(@NotNull UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw UserNotFoundException.byId(userId);
        }
        userRepository.deleteById(userId);
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

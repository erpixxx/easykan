package dev.erpix.easykan.service;

import dev.erpix.easykan.CacheKey;
import dev.erpix.easykan.model.user.EKUser;
import dev.erpix.easykan.model.user.dto.UserCreateRequestDto;
import dev.erpix.easykan.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Limit;
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

    public long count() {
        return userRepository.count();
    }

    public @NotNull EKUser create(@NotNull UserCreateRequestDto dto) {
        EKUser user = dto.toUser();
        if (user.getPassword() != null)
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public void delete(@NotNull UUID uuid) {
        userRepository.deleteById(uuid);
    }

    public boolean exists(@NotNull UUID uuid) {
        return userRepository.existsById(uuid);
    }

    @Cacheable(value = CacheKey.USERS, key = "#uuid")
    public @NotNull Optional<EKUser> getById(@NotNull UUID uuid) {
        return userRepository.findById(uuid);
    }

    public @NotNull Optional<EKUser> getByLogin(@NotNull String login) {
        return userRepository.findByLogin(login);
    }

    public @NotNull List<EKUser> getByDisplayName(@NotNull String displayName) {
        return userRepository.findByDisplayName(displayName);
    }

    public @NotNull List<EKUser> getByDisplayNameStartingWith(@NotNull String displayName) {
        return userRepository.findByDisplayNameStartingWith(displayName);
    }

    public @NotNull Optional<EKUser> getByEmail(@NotNull String email) {
        return userRepository.findByEmail(email);
    }

}

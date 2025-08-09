package dev.erpix.easykan.server.domain.user.service;

import dev.erpix.easykan.server.constant.CacheKey;
import dev.erpix.easykan.server.domain.auth.model.OAuthAccount;
import dev.erpix.easykan.server.domain.auth.repository.OAuthAccountRepository;
import dev.erpix.easykan.server.domain.user.dto.CurrentUserUpdateRequestDto;
import dev.erpix.easykan.server.domain.user.dto.UserUpdateRequestDto;
import dev.erpix.easykan.server.domain.user.model.User;
import dev.erpix.easykan.server.domain.user.model.UserPermission;
import dev.erpix.easykan.server.domain.user.security.RequirePermission;
import dev.erpix.easykan.server.domain.user.util.UserDetailsProvider;
import dev.erpix.easykan.server.exception.UserNotFoundException;
import dev.erpix.easykan.server.domain.user.dto.UserCreateRequestDto;
import dev.erpix.easykan.server.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
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
    private final OAuthAccountRepository oAuthAccountRepository;
    private final UserDetailsProvider userDetailsProvider;

    @Cacheable(value = CacheKey.USERS, key = "#userId")
    public @NotNull User getById(@NotNull UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.byId(userId));
    }

    public @NotNull User getByLogin(@NotNull String login) {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> UserNotFoundException.byLogin(login));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    public @NotNull User create(@NotNull UserCreateRequestDto dto) {
        User user = dto.toUser();
        if (user.getPasswordHash() != null)
            user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        return userRepository.save(user);
    }

    @PreAuthorize("hasAuthority('ADMIN') and #userId != authentication.principal.getId()")
    public void deleteUser(@NotNull UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw UserNotFoundException.byId(userId);
        }
        userRepository.deleteById(userId);
    }

    public @NotNull List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public void updateCurrentUser(@NotNull CurrentUserUpdateRequestDto dto) {
        UUID currentUserId = userDetailsProvider.getCurrentUserDetails()
                .map(details -> details.getUser().getId())
                .orElseThrow(() -> new IllegalStateException("User details not found in security context")); // lub inna obsługa błędu

        User userToUpdate = userRepository.findById(currentUserId)
                .orElseThrow(() -> UserNotFoundException.byId(currentUserId));

        if (dto.login() != null) {
            userToUpdate.setLogin(dto.login());
        }
        if (dto.displayName() != null) {
            userToUpdate.setDisplayName(dto.displayName());
        }
        if (dto.email() != null) {
            userToUpdate.setEmail(dto.email());
        }

        userRepository.save(userToUpdate);
    }

    @RequirePermission(UserPermission.ADMIN)
    public void updateUser(@NotNull UUID userId, @NotNull UserUpdateRequestDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.byId(userId));
    }

    @Transactional
    public User loadOrCreateFromOAuth(
            @NotNull String providerId,
            @NotNull String providerName,
            @NotNull String login,
            @NotNull String displayName,
            @NotNull String email) {

        // Make sure that login, displayName, and email are not repeated
        return oAuthAccountRepository.findByProviderIdAndProviderName(providerId, providerName)
                .map(OAuthAccount::getUser)
                .orElseGet(() -> {
                    User user = User.builder()
                            .login(login)
                            .displayName(displayName)
                            .email(email)
                            .build();
                    user = userRepository.save(user);

                    OAuthAccount account = new OAuthAccount();
                    account.setUser(user);
                    account.setProviderName(providerName);
                    account.setProviderId(providerId);
                    oAuthAccountRepository.save(account);

                    return user;
                });
    }

}

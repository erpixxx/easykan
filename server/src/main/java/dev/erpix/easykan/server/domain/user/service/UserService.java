package dev.erpix.easykan.server.domain.user.service;

import dev.erpix.easykan.server.constant.CacheKey;
import dev.erpix.easykan.server.domain.auth.model.OAuthAccount;
import dev.erpix.easykan.server.domain.auth.repository.OAuthAccountRepository;
import dev.erpix.easykan.server.domain.user.dto.UserInfoUpdateRequestDto;
import dev.erpix.easykan.server.domain.user.dto.UserPermissionsUpdateRequestDto;
import dev.erpix.easykan.server.domain.user.model.User;
import dev.erpix.easykan.server.domain.user.model.UserPermission;
import dev.erpix.easykan.server.domain.user.security.RequireUserPermission;
import dev.erpix.easykan.server.exception.user.UserNotFoundException;
import dev.erpix.easykan.server.domain.user.dto.UserCreateRequestDto;
import dev.erpix.easykan.server.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OAuthAccountRepository oAuthAccountRepository;

    @RequireUserPermission(UserPermission.MANAGE_USERS)
    public @NotNull Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Cacheable(value = CacheKey.USERS_ID, key = "#userId")
    public @NotNull User getById(@NotNull UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.byId(userId));
    }

    @Cacheable(value = CacheKey.USERS_LOGIN, key = "#login")
    public @NotNull User getByLogin(@NotNull String login) {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> UserNotFoundException.byLogin(login));
    }

    @RequireUserPermission(UserPermission.MANAGE_USERS)
    public @NotNull User create(@NotNull UserCreateRequestDto dto) {
        User user = dto.toUser();
        if (user.getPasswordHash() != null)
            user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        return userRepository.save(user);
    }

    @Caching(evict = {
            @CacheEvict(value = CacheKey.USERS_ID, key = "#userId"),
            @CacheEvict(value = CacheKey.USERS_LOGIN, allEntries = true) // Clear all login cache for consistency
    })
    @PreAuthorize("#userId != authentication.principal.getId()")
    @RequireUserPermission(UserPermission.MANAGE_USERS)
    public void deleteUser(@NotNull UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw UserNotFoundException.byId(userId);
        }
        userRepository.deleteById(userId);
    }

    @Transactional
    @Caching(put = {
            @CachePut(value = CacheKey.USERS_ID, key = "#result.id"),
            @CachePut(value = CacheKey.USERS_LOGIN, key = "#result.login")
    })
    public User updateCurrentUserInfo(
            @NotNull UUID userId,
            @NotNull UserInfoUpdateRequestDto dto
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.byId(userId));
        return updateUserInfoAndSave(user, dto);
    }

    @Transactional
    @Caching(put = {
            @CachePut(value = CacheKey.USERS_ID, key = "#result.id"),
            @CachePut(value = CacheKey.USERS_LOGIN, key = "#result.login")
    })
    @RequireUserPermission(UserPermission.MANAGE_USERS)
    public User updateUserInfo(@NotNull UUID userId, @NotNull UserInfoUpdateRequestDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.byId(userId));
        return updateUserInfoAndSave(user, dto);
    }

    @Transactional
    @Caching(put = {
            @CachePut(value = CacheKey.USERS_ID, key = "#result.id"),
            @CachePut(value = CacheKey.USERS_LOGIN, key = "#result.login")
    })
    @PreAuthorize("#userId != authentication.principal.getId()")
    @RequireUserPermission(UserPermission.ADMIN)
    public User updateUserPermissions(
            @NotNull UUID userId,
            @NotNull UserPermissionsUpdateRequestDto dto
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.byId(userId));

        user.setPermissions(dto.permissions());
        return userRepository.save(user);
    }

    protected @NotNull User updateUserInfoAndSave(@NotNull User user, @NotNull UserInfoUpdateRequestDto dto) {
        dto.login().ifPresent(user::setLogin);
        dto.displayName().ifPresent(user::setDisplayName);
        dto.email().ifPresent(user::setEmail);

        return userRepository.save(user);
    }

    @Transactional
    public OAuthAccount loadOrCreateFromOAuth(
            @NotNull String providerId,
            @NotNull String providerName,
            @NotNull String login,
            @NotNull String displayName,
            @NotNull String email
    ) {
        // todo: Make sure that login, displayName, and email are not duplicated
        return oAuthAccountRepository.findByProviderIdAndProviderName(providerId, providerName)
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

                    return account;
                });
    }

}

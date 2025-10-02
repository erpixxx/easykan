package dev.erpix.easykan.server.domain.user.service;

import dev.erpix.easykan.server.constant.CacheKey;
import dev.erpix.easykan.server.domain.user.dto.UserCreateRequestDto;
import dev.erpix.easykan.server.domain.user.dto.UserInfoUpdateRequestDto;
import dev.erpix.easykan.server.domain.user.dto.UserPermissionsUpdateRequestDto;
import dev.erpix.easykan.server.domain.user.model.User;
import dev.erpix.easykan.server.domain.user.model.UserPermission;
import dev.erpix.easykan.server.domain.user.repository.UserRepository;
import dev.erpix.easykan.server.domain.user.security.RequireUserPermission;
import dev.erpix.easykan.server.exception.user.UserNotFoundException;
import jakarta.transaction.Transactional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	private final PasswordEncoder passwordEncoder;

	@RequireUserPermission(UserPermission.MANAGE_USERS)
	public Page<User> getAllUsers(Pageable pageable) {
		return userRepository.findAll(pageable);
	}

	@Cacheable(value = CacheKey.USERS_ID, key = "#userId")
	public User getById(UUID userId) {
		return userRepository.findById(userId).orElseThrow(() -> UserNotFoundException.byId(userId));
	}

	@Cacheable(value = CacheKey.USERS_LOGIN, key = "#login")
	public User getByLogin(String login) {
		return userRepository.findByLogin(login).orElseThrow(() -> UserNotFoundException.byLogin(login));
	}

	@RequireUserPermission(UserPermission.MANAGE_USERS)
	public User create(UserCreateRequestDto dto) {
		User user = dto.toUser();
		if (user.getPasswordHash() != null)
			user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
		return userRepository.save(user);
	}

	@Caching(evict = { @CacheEvict(value = CacheKey.USERS_ID, key = "#userId"),
			@CacheEvict(value = CacheKey.USERS_LOGIN, allEntries = true) })
	@PreAuthorize("#userId != authentication.principal.getId()")
	@RequireUserPermission(UserPermission.MANAGE_USERS)
	public void deleteUser(UUID userId) {
		if (!userRepository.existsById(userId)) {
			throw UserNotFoundException.byId(userId);
		}
		userRepository.deleteById(userId);
	}

	@Transactional
	@Caching(put = { @CachePut(value = CacheKey.USERS_ID, key = "#result.id"),
			@CachePut(value = CacheKey.USERS_LOGIN, key = "#result.login") })
	public User updateCurrentUserInfo(UUID userId, UserInfoUpdateRequestDto dto) {
		User user = userRepository.findById(userId).orElseThrow(() -> UserNotFoundException.byId(userId));
		return updateUserInfoAndSave(user, dto);
	}

	@Transactional
	@Caching(put = { @CachePut(value = CacheKey.USERS_ID, key = "#result.id"),
			@CachePut(value = CacheKey.USERS_LOGIN, key = "#result.login") })
	@RequireUserPermission(UserPermission.MANAGE_USERS)
	public User updateUserInfo(UUID userId, UserInfoUpdateRequestDto dto) {
		User user = userRepository.findById(userId).orElseThrow(() -> UserNotFoundException.byId(userId));
		return updateUserInfoAndSave(user, dto);
	}

	@Transactional
	@Caching(put = { @CachePut(value = CacheKey.USERS_ID, key = "#result.id"),
			@CachePut(value = CacheKey.USERS_LOGIN, key = "#result.login") })
	@PreAuthorize("#userId != authentication.principal.getId()")
	@RequireUserPermission(UserPermission.ADMIN)
	public User updateUserPermissions(UUID userId, UserPermissionsUpdateRequestDto dto) {
		User user = userRepository.findById(userId).orElseThrow(() -> UserNotFoundException.byId(userId));

		user.setPermissions(dto.permissions());
		return userRepository.save(user);
	}

	protected User updateUserInfoAndSave(User user, UserInfoUpdateRequestDto dto) {
		dto.login().ifPresent(user::setLogin);
		dto.displayName().ifPresent(user::setDisplayName);
		dto.email().ifPresent(user::setEmail);

		return userRepository.save(user);
	}

}

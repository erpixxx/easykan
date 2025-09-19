package dev.erpix.easykan.server.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dev.erpix.easykan.server.domain.user.dto.UserCreateRequestDto;
import dev.erpix.easykan.server.domain.user.dto.UserInfoUpdateRequestDto;
import dev.erpix.easykan.server.domain.user.dto.UserPermissionsUpdateRequestDto;
import dev.erpix.easykan.server.domain.user.model.User;
import dev.erpix.easykan.server.domain.user.model.UserPermission;
import dev.erpix.easykan.server.domain.user.repository.UserRepository;
import dev.erpix.easykan.server.domain.user.security.JpaUserDetails;
import dev.erpix.easykan.server.domain.user.service.UserService;
import dev.erpix.easykan.server.exception.user.UserNotFoundException;
import dev.erpix.easykan.server.testsupport.Category;
import dev.erpix.easykan.server.testsupport.annotation.IntegrationTest;
import dev.erpix.easykan.server.testsupport.annotation.WithPersistedUser;
import dev.erpix.easykan.server.testsupport.annotation.WithPersistedUsers;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Tag(Category.INTEGRATION_TEST)
@IntegrationTest
public class UserServiceIT {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @WithPersistedUsers(
            principal = @WithPersistedUser(login = "admin", email = "admin@easykan.dev",
                    permissions = UserPermission.MANAGE_USERS),
            others = {@WithPersistedUser(login = "user1", email = "user1@easykan.dev"),
                    @WithPersistedUser(login = "user2", email = "user2@easykan.dev"),
                    @WithPersistedUser(login = "user3", email = "user3@easykan.dev"),})
    void getAllUsers_shouldReturnAllUsers() {
        var pageRequest = PageRequest.of(0, 5);
        var users = userService.getAllUsers(pageRequest);

        assertThat(users.getTotalElements()).isEqualTo(4);
    }

    @Test
    @WithPersistedUser
    void getAllUsers_shouldThrowAccessDenied_whenUserDoesNotHavePermission() {
        var pageRequest = PageRequest.of(0, 5);

        assertThrows(AccessDeniedException.class, () -> userService.getAllUsers(pageRequest));
    }

    @Test
    @WithPersistedUser
    void getById_shouldReturnUser_whenUserExists() {
        JpaUserDetails principal = (JpaUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        var user = userService.getById(principal.getId());

        assertThat(user).isEqualTo(principal.user());
    }

    @Test
    void getById_shouldThrowUserNotFoundException_whenUserDoesNotExist() {
        assertThrows(UserNotFoundException.class, () -> userService.getById(UUID.randomUUID()));
    }

    @Test
    @WithPersistedUser
    void getByLogin_shouldReturnUser_whenUserExists() {
        JpaUserDetails principal = (JpaUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        var user = userService.getByLogin(principal.getUsername());

        assertThat(user).isEqualTo(principal.user());
    }

    @Test
    void getByLogin_shouldThrowUserNotFoundException_whenUserDoesNotExist() {
        assertThrows(UserNotFoundException.class, () -> userService.getByLogin("nonexistentuser"));
    }

    @Test
    @WithPersistedUser(permissions = UserPermission.MANAGE_USERS)
    void createUser_shouldCreateNewUser_whenUserHasPermission() {
        var requestDto = new UserCreateRequestDto("newuser", "New User", "new.user@easykan.dev",
                "passwd123");

        assertThat(userRepository.count()).isEqualTo(1);

        var createdUser = userService.create(requestDto);

        assertThat(createdUser.getLogin()).isEqualTo(requestDto.login());
        assertThat(createdUser.getDisplayName()).isEqualTo(requestDto.displayName());
        assertThat(createdUser.getEmail()).isEqualTo(requestDto.email());
        assertThat(passwordEncoder.matches(requestDto.password(), createdUser.getPasswordHash()))
                .isTrue();
        assertThat(userRepository.count()).isEqualTo(2);
    }

    @Test
    @WithPersistedUser
    void createUser_shouldThrowAccessDenied_whenUserDoesNotHavePermission() {
        var requestDto = new UserCreateRequestDto("newuser", "New User", "new.user@easykan.dev",
                "passwd123");

        assertThrows(AccessDeniedException.class, () -> userService.create(requestDto));
        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    @WithPersistedUsers(
            principal = @WithPersistedUser(login = "admin", email = "admin@easykan.dev",
                    permissions = UserPermission.MANAGE_USERS),
            others = @WithPersistedUser(login = "usertodelete", email = "usertodelete@easykan.dev"))
    void deleteUser_shouldDeleteUser_whenUserExistsAndHasPermission() {
        User userToDelete = userRepository.findByLogin("usertodelete")
                .orElseThrow(() -> new AssertionError("User to delete not found"));

        assertThat(userRepository.count()).isEqualTo(2);

        userService.deleteUser(userToDelete.getId());

        assertThat(userRepository.existsById(userToDelete.getId())).isFalse();
        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    @WithPersistedUser(permissions = UserPermission.MANAGE_USERS)
    void deleteUser_shouldThrowUserNotFound_whenGivenUserDoesNotExists() {
        assertThat(userRepository.count()).isEqualTo(1);
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(UUID.randomUUID()));
        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    @WithPersistedUsers(
            principal = @WithPersistedUser(login = "admin", email = "admin@easykan.dev"),
            others = @WithPersistedUser(login = "usertodelete", email = "usertodelete@easykan.dev"))
    void deleteUser_shouldThrowAccessDenied_whenUserDoesNotHavePermission() {
        User userToDelete = userRepository.findByLogin("usertodelete")
                .orElseThrow(() -> new AssertionError("User to delete not found"));

        assertThat(userRepository.count()).isEqualTo(2);
        assertThrows(AccessDeniedException.class,
                () -> userService.deleteUser(userToDelete.getId()));
        assertThat(userRepository.count()).isEqualTo(2);
    }

    @Test
    @WithPersistedUser(permissions = UserPermission.MANAGE_USERS)
    void deleteUser_shouldThrowAccessDenied_whenTryingToDeleteSelf() {
        var principal = (JpaUserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        assertThrows(AccessDeniedException.class, () -> userService.deleteUser(principal.getId()));
    }

    @Test
    @WithPersistedUser
    void updateCurrentUserInfo_shouldUpdateUserInfo_whenRequestIsValid() {
        var principal = (JpaUserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        String updatedLogin = "updatedlogin";
        String updatedDisplayName = "Updated User";
        String updatedEmail = "updated.email@easykan.dev";
        var requestDto = new UserInfoUpdateRequestDto(Optional.of(updatedLogin),
                Optional.of(updatedDisplayName), Optional.of(updatedEmail));

        var updatedUser = userService.updateCurrentUserInfo(principal.getId(), requestDto);

        User userFromDb = userRepository.findById(updatedUser.getId())
                .orElseThrow(() -> new AssertionError("Updated user not found in repository"));

        assertThat(userFromDb.getLogin()).isEqualTo(updatedLogin);
        assertThat(userFromDb.getDisplayName()).isEqualTo(updatedDisplayName);
        assertThat(userFromDb.getEmail()).isEqualTo(updatedEmail);
    }

    @Test
    void updateCurrentUserInfo_shouldThrowUserNotFound_whenUserDoesNotExist() {
        var requestDto = new UserInfoUpdateRequestDto(Optional.of("updatedlogin"),
                Optional.of("Updated User"), Optional.of("updated.email@easykan.dev"));

        assertThrows(UserNotFoundException.class,
                () -> userService.updateCurrentUserInfo(UUID.randomUUID(), requestDto));
    }

    @Test
    @WithPersistedUsers(
            principal = @WithPersistedUser(login = "admin", email = "admin@easykan.dev",
                    permissions = UserPermission.MANAGE_USERS),
            others = @WithPersistedUser(login = "usertoupdate", email = "usertoupdate@easykan.dev"))
    void updateUserInfo_shouldUpdateUserInfo_whenRequestIsValidAndUserHasPermissions() {
        User userToUpdate = userRepository.findByLogin("usertoupdate")
                .orElseThrow(() -> new AssertionError("User to update not found"));

        String updatedLogin = "updatedlogin";
        String updatedDisplayName = "Updated User";
        String updatedEmail = "updated.email@easykan.dev";
        var requestDto = new UserInfoUpdateRequestDto(Optional.of(updatedLogin),
                Optional.of(updatedDisplayName), Optional.of(updatedEmail));

        var updatedUser = userService.updateUserInfo(userToUpdate.getId(), requestDto);

        User userFromDb = userRepository.findById(updatedUser.getId())
                .orElseThrow(() -> new AssertionError("Updated user not found in repository"));

        assertThat(userFromDb.getLogin()).isEqualTo(updatedLogin);
        assertThat(userFromDb.getDisplayName()).isEqualTo(updatedDisplayName);
        assertThat(userFromDb.getEmail()).isEqualTo(updatedEmail);
    }

    @Test
    @WithPersistedUsers(
            principal = @WithPersistedUser(login = "admin", email = "admin@easykan.dev"),
            others = @WithPersistedUser(login = "usertoupdate", email = "usertoupdate@easykan.dev"))
    void updateUserInfo_shouldThrowAccessDenied_whenUserDoesNotHavePermission() {
        User userToUpdate = userRepository.findByLogin("usertoupdate")
                .orElseThrow(() -> new AssertionError("User to update not found"));

        var requestDto = new UserInfoUpdateRequestDto(Optional.of("updatedlogin"),
                Optional.of("Updated User"), Optional.of("updated.email@easykan.dev"));

        assertThrows(AccessDeniedException.class,
                () -> userService.updateUserInfo(userToUpdate.getId(), requestDto));
    }

    @Test
    @WithPersistedUsers(
            principal = @WithPersistedUser(login = "admin", email = "admin@easykan.dev",
                    permissions = UserPermission.ADMIN),
            others = @WithPersistedUser(login = "usertoupdate", email = "usertoupdate@easykan.dev"))
    void updateUserPermissions_shouldUpdateUserPermissions_whenUserHasAdminPermission() {
        User userToUpdate = userRepository.findByLogin("usertoupdate")
                .orElseThrow(() -> new AssertionError("User to update not found"));

        assertThat(UserPermission.hasPermission(userToUpdate, UserPermission.MANAGE_USERS))
                .isFalse();

        long newPermission = UserPermission.toValue(UserPermission.MANAGE_USERS);
        var requestDto = new UserPermissionsUpdateRequestDto(newPermission);

        userService.updateUserPermissions(userToUpdate.getId(), requestDto);

        User updatedUser = userRepository.findById(userToUpdate.getId())
                .orElseThrow(() -> new AssertionError("Updated user not found in repository"));

        assertThat(UserPermission.hasPermission(updatedUser, UserPermission.MANAGE_USERS)).isTrue();
    }

    @Test
    @WithPersistedUsers(
            principal = @WithPersistedUser(login = "admin", email = "admin@easykan.dev"),
            others = @WithPersistedUser(login = "usertoupdate", email = "usertoupdate@easykan.dev"))
    void updateUserPermissions_shouldThrowAccessDenied_whenUserDoesNotHavePermission() {
        User userToUpdate = userRepository.findByLogin("usertoupdate")
                .orElseThrow(() -> new AssertionError("User to update not found"));

        long newPermission = UserPermission.toValue(UserPermission.MANAGE_USERS);
        var requestDto = new UserPermissionsUpdateRequestDto(newPermission);

        assertThrows(AccessDeniedException.class,
                () -> userService.updateUserPermissions(userToUpdate.getId(), requestDto));
    }
}

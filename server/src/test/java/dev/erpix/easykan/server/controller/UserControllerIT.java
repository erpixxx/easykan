package dev.erpix.easykan.server.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.erpix.easykan.server.domain.PermissionUtils;
import dev.erpix.easykan.server.domain.user.dto.UserCreateRequestDto;
import dev.erpix.easykan.server.domain.user.dto.UserInfoUpdateRequestDto;
import dev.erpix.easykan.server.domain.user.dto.UserPermissionsUpdateRequestDto;
import dev.erpix.easykan.server.domain.user.model.User;
import dev.erpix.easykan.server.domain.user.model.UserPermission;
import dev.erpix.easykan.server.domain.user.security.JpaUserDetails;
import dev.erpix.easykan.server.domain.user.service.UserService;
import dev.erpix.easykan.server.exception.user.UserNotFoundException;
import dev.erpix.easykan.server.testsupport.Category;
import dev.erpix.easykan.server.testsupport.PermissionMaskSupport;
import dev.erpix.easykan.server.testsupport.annotation.WebMvcBundle;
import dev.erpix.easykan.server.testsupport.annotation.WithSecurityContextUser;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@Tag(Category.INTEGRATION_TEST)
@WebMvcBundle(UserController.class)
public class UserControllerIT extends AbstractControllerSecurityIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@SuppressWarnings("unused")
	@MockitoBean
	private UserService userService;

	@Override
	protected Stream<Arguments> provideProtectedEndpoints() {
		return Stream.of(Arguments.of("GET", "/api/users/@me"), Arguments.of("GET", "/api/users"),
				Arguments.of("POST", "/api/users"),
				Arguments.of("DELETE", "/api/users/" + WithSecurityContextUser.Default.ID),
				Arguments.of("PATCH", "/api/users/@me"),
				Arguments.of("PATCH", "/api/users/" + WithSecurityContextUser.Default.ID),
				Arguments.of("PUT", "/api/users/" + WithSecurityContextUser.Default.ID + "/permissions"));
	}

	@Test
	@WithSecurityContextUser
	void getCurrentUser_shouldReturnCurrentUser_whenUserIsAuthenticated() throws Exception {
		mockMvc.perform(get("/api/users/@me"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.id").value(WithSecurityContextUser.Default.ID))
			.andExpect(jsonPath("$.login").value(WithSecurityContextUser.Default.LOGIN))
			.andExpect(jsonPath("$.displayName").value(WithSecurityContextUser.Default.DISPLAY_NAME))
			.andExpect(jsonPath("$.permissions")
				.value(PermissionUtils.toValue(WithSecurityContextUser.Default.PERMISSIONS)));
	}

	@Test
	void getCurrentUser_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
		mockMvc.perform(get("/api/users/@me")).andExpect(status().isUnauthorized());
	}

	@Test
	@WithSecurityContextUser
	void getAllUsers_shouldReturnAllUsers_whenUserHasManageUsersPermission() throws Exception {
		UUID otherUserId = UUID.randomUUID();
		String otherUserLogin = "otheruser";
		String otherUserDisplayName = "Other User";
		long otherUserPermissions = PermissionUtils.toValue(UserPermission.DEFAULT_PERMISSIONS);
		User otherUser = User.builder()
			.id(otherUserId)
			.login(otherUserLogin)
			.displayName(otherUserDisplayName)
			.permissions(otherUserPermissions)
			.build();

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		JpaUserDetails principal = (JpaUserDetails) authentication.getPrincipal();
		User currentUser = principal.user();

		when(userService.getAllUsers(any())).thenReturn(new PageImpl<>(List.of(otherUser, currentUser)));

		mockMvc.perform(get("/api/users"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.content").isArray())
			.andExpect(jsonPath("$.content.length()").value(2))
			.andExpect(jsonPath("$.content[0].id").value(otherUserId.toString()))
			.andExpect(jsonPath("$.content[0].login").value(otherUserLogin))
			.andExpect(jsonPath("$.content[0].displayName").value(otherUserDisplayName))
			.andExpect(jsonPath("$.content[0].permissions").value(otherUserPermissions))
			.andExpect(jsonPath("$.content[1].id").value(currentUser.getId().toString()))
			.andExpect(jsonPath("$.content[1].login").value(currentUser.getLogin()))
			.andExpect(jsonPath("$.content[1].displayName").value(currentUser.getDisplayName()))
			.andExpect(jsonPath("$.content[1].permissions").value(currentUser.getPermissions()));
	}

	@Test
	@WithSecurityContextUser
	void getAllUsers_shouldReturnForbidden_whenUserDoesNotHaveManageUsersPermission() throws Exception {
		when(userService.getAllUsers(any(Pageable.class)))
			.thenThrow(new AccessDeniedException("Insufficient permissions."));

		mockMvc.perform(get("/api/users").accept(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden());
	}

	@Test
	@WithSecurityContextUser
	void createUser_shouldCreateUser_whenUserHasManageUsersPermission() throws Exception {
		String newUserLogin = "newuser";
		String newUserDisplayName = "New User";
		String newUserEmail = "new.user@easykan.dev";
		String newUserPermission = "Pa$$w0rd";
		User newUser = User.builder()
			.id(UUID.randomUUID())
			.login(newUserLogin)
			.displayName(newUserDisplayName)
			.email(newUserEmail)
			.passwordHash(newUserPermission)
			.permissions(PermissionUtils.toValue(UserPermission.DEFAULT_PERMISSIONS))
			.build();

		UserCreateRequestDto request = new UserCreateRequestDto(newUserLogin, newUserDisplayName, newUserEmail,
				newUserPermission);

		when(userService.create(any(UserCreateRequestDto.class))).thenReturn(newUser);

		mockMvc
			.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(request)))
			.andExpect(status().isCreated())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.id").value(newUser.getId().toString()))
			.andExpect(jsonPath("$.login").value(newUser.getLogin()))
			.andExpect(jsonPath("$.displayName").value(newUser.getDisplayName()))
			.andExpect(jsonPath("$.permissions").value(UserPermission.DEFAULT_PERMISSIONS.getValue()));
	}

	@Test
	@WithSecurityContextUser
	void createUser_shouldReturnForbidden_whenUserDoesNotHaveManageUsersPermission() throws Exception {
		UserCreateRequestDto request = new UserCreateRequestDto("newuser", "New User", "new.user@easykan.dev",
				"Pa$$w0rd");

		when(userService.create(any(UserCreateRequestDto.class)))
			.thenThrow(new AccessDeniedException("Insufficient permissions."));

		mockMvc
			.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(request)))
			.andExpect(status().isForbidden());
	}

	@ParameterizedTest
	@CsvSource({ "'',        'New User', 'user@test.dev', 'Pa$$w0rd''",
			"'  ',      'New User', 'user@test.dev', 'Pa$$w0rd'", "'$$$',     'New User', 'user@test.dev', 'Pa$$w0rd'",
			"'newuser', '',         'user@test.dev', 'Pa$$w0rd'", "'newuser', '  ',       'user@test.dev', 'Pa$$w0rd'",
			"'newuser', 'New User', 'not-an-email',  'Pa$$w0rd'", "'newuser', 'New User', 'user@test.dev', ''",
			"'newuser', 'New User', 'user@test.dev', '1234'" })
	@WithSecurityContextUser
	void createUser_shouldReturnBadRequest_whenInvalidDataProvided(String login, String displayName, String email,
			String password) throws Exception {

		UserCreateRequestDto request = new UserCreateRequestDto(login, displayName, email, password);

		mockMvc
			.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(request)))
			.andExpect(status().isBadRequest());

		verify(userService, never()).create(any(UserCreateRequestDto.class));
	}

	@Test
	@WithSecurityContextUser
	void deleteUser_shouldDeleteUser_whenUserHasManageUsersPermission() throws Exception {
		UUID userIdToDelete = UUID.randomUUID();

		mockMvc.perform(delete("/api/users/{userId}", userIdToDelete)).andExpect(status().isNoContent());

		verify(userService).deleteUser(userIdToDelete);
	}

	@Test
	@WithSecurityContextUser
	void deleteUser_shouldReturnForbidden_whenUserDoesNotHaveManageUsersPermission() throws Exception {
		UUID userIdToDelete = UUID.randomUUID();

		doThrow(new AccessDeniedException("Insufficient permissions.")).when(userService).deleteUser(userIdToDelete);

		mockMvc.perform(delete("/api/users/{userId}", userIdToDelete)).andExpect(status().isForbidden());
	}

	@Test
	@WithSecurityContextUser
	void deleteUser_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
		UUID userIdToDelete = UUID.randomUUID();

		doThrow(UserNotFoundException.byId(userIdToDelete)).when(userService).deleteUser(userIdToDelete);

		mockMvc.perform(delete("/api/users/{userId}", userIdToDelete)).andExpect(status().isNotFound());
	}

	@Test
	@WithSecurityContextUser
	void deleteUser_shouldReturnForbidden_whenTryingToDeleteSelf() throws Exception {
		doThrow(new AccessDeniedException("Users cannot delete themselves")).when(userService)
			.deleteUser(UUID.fromString(WithSecurityContextUser.Default.ID));

		mockMvc.perform(delete("/api/users/{userId}", WithSecurityContextUser.Default.ID))
			.andExpect(status().isForbidden());
	}

	@Test
	@WithSecurityContextUser
	void updateCurrentUser_shouldReturnUpdatedUser_whenValidDataProvided() throws Exception {
		String newLogin = "updateduser";
		String newDisplayName = "Updated User";
		UserInfoUpdateRequestDto requestDto = new UserInfoUpdateRequestDto(Optional.of(newLogin),
				Optional.of(newDisplayName), Optional.empty());

		long permissions = PermissionUtils.toValue(WithSecurityContextUser.Default.PERMISSIONS);
		User updatedUser = User.builder()
			.id(UUID.fromString(WithSecurityContextUser.Default.ID))
			.login(newLogin)
			.displayName(newDisplayName)
			.passwordHash(WithSecurityContextUser.Default.PASSWORD)
			.permissions(permissions)
			.build();

		when(userService.updateCurrentUserInfo(any(UUID.class), any(UserInfoUpdateRequestDto.class)))
			.thenReturn(updatedUser);

		mockMvc
			.perform(patch("/api/users/@me").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(requestDto)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.id").value(WithSecurityContextUser.Default.ID))
			.andExpect(jsonPath("$.login").value(newLogin))
			.andExpect(jsonPath("$.displayName").value(newDisplayName))
			.andExpect(jsonPath("$.permissions").value(permissions));
	}

	@ParameterizedTest
	@CsvSource({ "'',            'Updated User', 'updated.user@easykan.dev'",
			"'  ',          'Updated User', 'updated.user@easykan.dev'",
			"'$$$',         'Updated User', 'updated.user@easykan.dev'",
			"'updateduser', '',             'updated.user@easykan.dev'",
			"'updateduser', '  ',           'updated.user@easykan.dev'",
			"'updateduser', 'Updated User', 'bademailformat'" })
	@WithSecurityContextUser
	void updateCurrentUser_shouldReturnBadRequest_whenInvalidDataProvided(String login, String displayName,
			String email) throws Exception {
		UserInfoUpdateRequestDto requestDto = new UserInfoUpdateRequestDto(Optional.ofNullable(login),
				Optional.ofNullable(displayName), Optional.ofNullable(email));

		mockMvc
			.perform(patch("/api/users/@me").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(requestDto)))
			.andExpect(status().isBadRequest());
	}

	@Test
	@WithSecurityContextUser
	void updateCurrentUser_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
		UserInfoUpdateRequestDto requestDto = new UserInfoUpdateRequestDto(Optional.of("updateduser"),
				Optional.of("Updated User"), Optional.empty());

		when(userService.updateCurrentUserInfo(any(UUID.class), any(UserInfoUpdateRequestDto.class)))
			.thenThrow(UserNotFoundException.byId(UUID.fromString(WithSecurityContextUser.Default.ID)));

		mockMvc
			.perform(patch("/api/users/@me").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(requestDto)))
			.andExpect(status().isNotFound());
	}

	@Test
	@WithSecurityContextUser
	void updateUserPermissions_shouldReturnUpdatedUser_whenValidRequest() throws Exception {
		UUID userId = UUID.randomUUID();
		var requestDto = new UserPermissionsUpdateRequestDto(PermissionUtils.toValue(UserPermission.MANAGE_PROJECTS));

		var updatedUser = new User();
		updatedUser.setId(userId);
		updatedUser.setPermissions(requestDto.permissions());

		when(userService.updateUserPermissions(eq(userId), any())).thenReturn(updatedUser);

		mockMvc
			.perform(put("/api/users/{userId}/permissions", userId).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(requestDto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(userId.toString()))
			.andExpect(jsonPath("$.permissions").value(requestDto.permissions()));

		verify(userService).updateUserPermissions(eq(userId), any());
	}

	@Test
	@WithSecurityContextUser
	void updateUserPermissions_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
		UUID userId = UUID.randomUUID();
		var requestDto = new UserPermissionsUpdateRequestDto(PermissionUtils.toValue(UserPermission.MANAGE_PROJECTS));

		doThrow(UserNotFoundException.byId(userId)).when(userService).updateUserPermissions(eq(userId), any());

		mockMvc
			.perform(put("/api/users/{userId}/permissions", userId).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(requestDto)))
			.andExpect(status().isNotFound());
	}

	@ParameterizedTest
	@MethodSource("provideInvalidPermissions")
	@WithSecurityContextUser
	void updateUserPermissions_shouldReturnBadRequest_whenInvalidPermissionsProvided(long invalidPermissions)
			throws Exception {
		UUID userId = UUID.randomUUID();

		var requestDto = new UserPermissionsUpdateRequestDto(invalidPermissions);

		mockMvc
			.perform(put("/api/users/{userId}/permissions", userId).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(requestDto)))
			.andExpect(status().isBadRequest());

		verify(userService, never()).updateUserPermissions(eq(userId), any());
	}

	@Test
	@WithSecurityContextUser
	void updateUserPermissions_shouldReturnForbidden_whenUserDoesNotHaveAdminPermission() throws Exception {
		UUID userId = UUID.randomUUID();
		var requestDto = new UserPermissionsUpdateRequestDto(PermissionUtils.toValue(UserPermission.MANAGE_PROJECTS));

		doThrow(new AccessDeniedException("Insufficient permissions.")).when(userService)
			.updateUserPermissions(eq(userId), any());

		mockMvc
			.perform(put("/api/users/{userId}/permissions", userId).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(requestDto)))
			.andExpect(status().isForbidden());
	}

	// Helper methods

	static Stream<Long> provideInvalidPermissions() {
		return PermissionMaskSupport.generateInvalidPermissions(UserPermission.class);
	}

}

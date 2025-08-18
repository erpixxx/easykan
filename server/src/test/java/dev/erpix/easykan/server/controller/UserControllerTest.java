package dev.erpix.easykan.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.erpix.easykan.server.config.SecurityConfig;
import dev.erpix.easykan.server.domain.token.service.JwtProvider;
import dev.erpix.easykan.server.domain.user.dto.UserCreateRequestDto;
import dev.erpix.easykan.server.domain.user.security.JpaUserDetails;
import dev.erpix.easykan.server.domain.user.service.JpaUserDetailsService;
import dev.erpix.easykan.server.domain.user.service.UserService;
import dev.erpix.easykan.server.exception.GlobalExceptionHandler;
import dev.erpix.easykan.server.exception.UserNotFoundException;
import dev.erpix.easykan.server.testsupport.security.WithMockUser;
import dev.erpix.easykan.server.domain.user.model.User;
import dev.erpix.easykan.server.domain.user.model.UserPermission;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
public class UserControllerTest extends AbstractControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JpaUserDetailsService jpaUserDetailsService;

    @MockitoBean
    private JwtProvider jwtProvider;

    @MockitoBean
    private UserService userService;

    @Override
    protected Stream<Arguments> provideProtectedEndpoints() {
        return Stream.of(
                Arguments.of("GET", "/api/users/@me"),
                Arguments.of("GET", "/api/users"),
                Arguments.of("POST", "/api/users"),
                Arguments.of("DELETE", "/api/users/" + WithMockUser.Default.ID),
                Arguments.of("PATCH", "/api/users/@me"),
                Arguments.of("PATCH", "/api/users/" + WithMockUser.Default.ID)
        );
    }

    @Test
    @WithMockUser
    void getCurrentUser_shouldReturnCurrentUser_whenUserIsAuthenticated() throws Exception {
        mockMvc.perform(get("/api/users/@me"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id")
                        .value(WithMockUser.Default.ID))
                .andExpect(jsonPath("$.login")
                        .value(WithMockUser.Default.LOGIN))
                .andExpect(jsonPath("$.displayName")
                        .value(WithMockUser.Default.DISPLAY_NAME))
                .andExpect(jsonPath("$.permissions")
                        .value(UserPermission.toValue(WithMockUser.Default.PERMISSIONS)));
    }

    @Test
    void getCurrentUser_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/users/@me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void getAllUsers_shouldReturnAllUsers_whenUserHasManageUsersPermission() throws Exception {
        UUID otherUserId = UUID.randomUUID();
        String otherUserLogin = "otheruser";
        String otherUserDisplayName = "Other User";
        long otherUserPermissions = UserPermission.toValue(UserPermission.DEFAULT_PERMISSIONS);
        User otherUser = User.builder()
                .id(otherUserId)
                .login(otherUserLogin)
                .displayName(otherUserDisplayName)
                .permissions(otherUserPermissions)
                .build();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JpaUserDetails principal = (JpaUserDetails) authentication.getPrincipal();
        User currentUser = principal.user();

        when(userService.getAllUsers(any()))
                .thenReturn(new PageImpl<>(List.of(otherUser, currentUser)));

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
    @WithMockUser
    void getAllUsers_shouldReturnForbidden_whenUserDoesNotHaveManageUsersPermission() throws Exception {
        when(userService.getAllUsers(any(Pageable.class)))
                .thenThrow(new AccessDeniedException("Insufficient permissions."));

        mockMvc.perform(get("/api/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void createUser_shouldCreateUser_whenUserHasManageUsersPermission() throws Exception {
        String newUserLogin = "newuser";
        String newUserDisplayName = "New User";
        String newUserEmail = "new.user@easykan.dev";
        String newUserPermission = "password";
        User newUser = User.builder()
                .id(UUID.randomUUID())
                .login(newUserLogin)
                .displayName(newUserDisplayName)
                .email(newUserEmail)
                .passwordHash(newUserPermission)
                .permissions(UserPermission.toValue(UserPermission.DEFAULT_PERMISSIONS))
                .build();

        UserCreateRequestDto request = new UserCreateRequestDto(
                newUserLogin, newUserDisplayName, newUserEmail, newUserPermission);

        when(userService.create(any(UserCreateRequestDto.class)))
                .thenReturn(newUser);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id")
                        .value(newUser.getId().toString()))
                .andExpect(jsonPath("$.login")
                        .value(newUser.getLogin()))
                .andExpect(jsonPath("$.displayName")
                        .value(newUser.getDisplayName()))
                .andExpect(jsonPath("$.permissions")
                        .value(UserPermission.DEFAULT_PERMISSIONS.getValue()));
    }

    @Test
    @WithMockUser
    void createUser_shouldReturnForbidden_whenUserDoesNotHaveManageUsersPermission() throws Exception {
        UserCreateRequestDto request = new UserCreateRequestDto(
                "newuser", "New User", "new.user@easykan.dev", "password");

        when(userService.create(any(UserCreateRequestDto.class)))
                .thenThrow(new AccessDeniedException("Insufficient permissions."));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    @CsvSource({
            "'',          'New User', 'user@test.dev', 'password123''",
            "'  ',        'New User', 'user@test.dev', 'password123'",
            "'newuser',   '',         'user@test.dev', 'password123'",
            "'newuser',   'New User', 'not-an-email',  'password123'",
            "'newuser',   'New User', 'user@test.dev', '1234'"
    })
    @WithMockUser
    void createUser_shouldReturnBadRequest_whenInvalidDataProvided(
            String login, String displayName, String email, String password) throws Exception {

        UserCreateRequestDto request = new UserCreateRequestDto(login, displayName, email, password);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).create(any(UserCreateRequestDto.class));
    }

    @Test
    @WithMockUser
    void deleteUser_shouldDeleteUser_whenUserHasManageUsersPermission() throws Exception {
        UUID userIdToDelete = UUID.randomUUID();

        mockMvc.perform(delete("/api/users/{userId}", userIdToDelete))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(userIdToDelete);
    }

    @Test
    @WithMockUser
    void deleteUser_shouldReturnForbidden_whenUserDoesNotHaveManageUsersPermission() throws Exception {
        UUID userIdToDelete = UUID.randomUUID();

        doThrow(new AccessDeniedException("Insufficient permissions."))
                .when(userService).deleteUser(userIdToDelete);

        mockMvc.perform(delete("/api/users/{userId}", userIdToDelete))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void deleteUser_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
        UUID userIdToDelete = UUID.randomUUID();

        doThrow(UserNotFoundException.byId(userIdToDelete))
                .when(userService).deleteUser(userIdToDelete);

        mockMvc.perform(delete("/api/users/{userId}", userIdToDelete))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void deleteUser_shouldReturnForbidden_whenTryingToDeleteSelf() throws Exception {
        doThrow(new AccessDeniedException("Users cannot delete themselves"))
                .when(userService).deleteUser(UUID.fromString(WithMockUser.Default.ID));

        mockMvc.perform(delete("/api/users/{userId}", WithMockUser.Default.ID))
                .andExpect(status().isForbidden());
    }

}

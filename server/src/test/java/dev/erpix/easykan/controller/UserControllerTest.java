package dev.erpix.easykan.controller;

import dev.erpix.easykan.TestcontainersConfig;
import dev.erpix.easykan.model.user.EKUser;
import dev.erpix.easykan.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfig.class)
@Transactional
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private static final String USER_JSON = """
            {
                "login": "newUser",
                "displayName": "New User",
                "email": "new.user@example.com",
                "password": "password123",
                "isAdmin": false
            }
            """;
    private static final String TEST_USER_LOGIN = "testuser";
    private static final String TEST_USER_DISPLAY_NAME = "Test User";

    @BeforeEach
    void setUp() {
        if (userRepository.findByLogin("testuser").isEmpty()) {
            EKUser user = EKUser.builder()
                    .login(TEST_USER_LOGIN)
                    .displayName(TEST_USER_DISPLAY_NAME)
                    .email("test.user@easykan.dev")
                    .passwordHash("hashedPassword")
                    .isAdmin(false)
                    .canAuthWithPassword(true)
                    .build();
            userRepository.save(user);
        }
    }

    @Test
    @WithUserDetails(value = TEST_USER_LOGIN, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void getCurrentUser_shouldReturnUserInfo_whenAuthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/users/@me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value(TEST_USER_LOGIN))
                .andExpect(jsonPath("$.displayName").value(TEST_USER_DISPLAY_NAME));
    }

    @Test
    void getCurrentUser_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/users/@me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = "T(dev.erpix.easykan.model.Role).USER")
    void deleteUser_shouldReturnForbidden_whenUserIsNotAdmin() throws Exception {
        mockMvc.perform(delete("/api/v1/users/" + UUID.randomUUID()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "T(dev.erpix.easykan.model.Role).ADMIN")
    void deleteUser_shouldReturnNoContent_whenUserIsAdmin() throws Exception {
        EKUser userToDelete = userRepository.save(EKUser.builder()
                .login("todelete")
                .displayName("User to delete")
                .email("to.delete@easykkan.dev")
                .passwordHash("hashedPassword")
                .canAuthWithPassword(true)
                .build());

        mockMvc.perform(delete("/api/v1/users/" + userToDelete.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(authorities = "T(dev.erpix.easykan.model.Role).ADMIN")
    void deleteUser_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
        UUID nonExistentUserId = UUID.randomUUID();

        mockMvc.perform(delete("/api/v1/users/" + nonExistentUserId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = "T(dev.erpix.easykan.model.Role).USER")
    void createUser_shouldReturnForbidden_whenUserIsNotAdmin() throws Exception {
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(USER_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "T(dev.erpix.easykan.model.Role).ADMIN")
    void createUser_shouldReturnCreated_whenUserIsAdmin() throws Exception {
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(USER_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(authorities = "T(dev.erpix.easykan.model.Role).ADMIN")
    void createUser_shouldReturnBadRequest_whenDataIsInvalid() throws Exception {
        String invalidUserJson = """
                {
                    "login": "",
                    "displayName": "Invalid User",
                    "email": "invalid@example.com",
                    "password": "password123",
                    "isAdmin": false
                }
                """;

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidUserJson))
                .andExpect(status().isBadRequest());
    }

}

package dev.erpix.easykan.server.controller;

import dev.erpix.easykan.server.domain.user.security.JpaUserDetails;
import dev.erpix.easykan.server.domain.user.model.User;
import dev.erpix.easykan.server.domain.user.dto.UserCreateRequestDto;
import dev.erpix.easykan.server.domain.user.dto.UserResponseDto;
import dev.erpix.easykan.server.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Users",
        description = "Endpoints for user management")
@RestController
@RequestMapping("/api/v1/users")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get current user info",
            description = "Returns details of the currently authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "401", description = "The user is not logged in")
    })
    @GetMapping("/@me")
    public ResponseEntity<UserResponseDto> getCurrentUser(@AuthenticationPrincipal JpaUserDetails userDetails) {
        User currentUser = userService.getById(userDetails.getUser().getId());
        return ResponseEntity.ok(UserResponseDto.fromUser(currentUser));
    }

    @Operation(summary = "Get all users",
            description = "Returns a list of all users. Requires ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "403", description = "Forbidden if the user is not an ADMIN")
    })
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> users = userService.getAllUsers().stream()
                .map(UserResponseDto::fromUser)
                .toList();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Create a new user",
            description = "Creates a new user. Requires ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "403", description = "The user is not an ADMIN")
    })
    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@RequestBody @Valid UserCreateRequestDto requestDto) {
        User createdUser = userService.create(requestDto);
        return new ResponseEntity<>(UserResponseDto.fromUser(createdUser), HttpStatus.CREATED);
    }

    @Operation(summary = "Delete a user",
            description = "Deletes a user by their ID. Requires ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "403", description = "The user is not an ADMIN"),
            @ApiResponse(responseCode = "404", description = "The user with given ID does not exist")
    })
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable UUID userId) {
        userService.deleteUser(userId);
    }

}

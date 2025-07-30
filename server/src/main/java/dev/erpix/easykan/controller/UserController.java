package dev.erpix.easykan.controller;

import dev.erpix.easykan.model.EKUserDetails;
import dev.erpix.easykan.model.user.EKUser;
import dev.erpix.easykan.model.user.dto.UserCreateRequestDto;
import dev.erpix.easykan.model.user.dto.UserResponseDto;
import dev.erpix.easykan.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Users", description = "Endpoints for user management")
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
    public ResponseEntity<UserResponseDto> getCurrentUser(@AuthenticationPrincipal EKUserDetails userDetails) {
        EKUser currentUser = userService.getById(userDetails.getUser().getId());
        return ResponseEntity.ok(UserResponseDto.fromUser(currentUser));
    }

    @Operation(summary = "Create a new user",
            description = "Creates a new user. Requires ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "403", description = "The user is not an ADMIN")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('T(dev.erpix.easykan.model.Role).ADMIN')")
    public ResponseEntity<UserResponseDto> createUser(@RequestBody @Valid UserCreateRequestDto requestDto) {
        EKUser createdUser = userService.create(requestDto);
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
    @PreAuthorize("hasAuthority('T(dev.erpix.easykan.model.Role).ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable UUID userId) {
        userService.deleteUser(userId);
    }

}

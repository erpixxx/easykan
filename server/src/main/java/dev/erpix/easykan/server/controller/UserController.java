package dev.erpix.easykan.server.controller;

import dev.erpix.easykan.server.domain.user.dto.UserCreateRequestDto;
import dev.erpix.easykan.server.domain.user.dto.UserInfoUpdateRequestDto;
import dev.erpix.easykan.server.domain.user.dto.UserPermissionsUpdateRequestDto;
import dev.erpix.easykan.server.domain.user.dto.UserResponseDto;
import dev.erpix.easykan.server.domain.user.model.User;
import dev.erpix.easykan.server.domain.user.security.JpaUserDetails;
import dev.erpix.easykan.server.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Users", description = "Endpoints for user management")
@RestController
@RequestMapping("/api/users")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@Operation(summary = "Get current user info", description = "Returns details of the currently authenticated user.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successful operation",
					content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
			@ApiResponse(responseCode = "401", description = "User is not authenticated", content = @Content) })
	@GetMapping("/@me")
	public ResponseEntity<UserResponseDto> getCurrentUser(@AuthenticationPrincipal JpaUserDetails userDetails) {
		return ResponseEntity.ok(UserResponseDto.fromUser(userDetails.user()));
	}

	@Operation(summary = "Get all users", description = "Returns a list of all users.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successful operation",
					content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
			@ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
			@ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content) })
	@GetMapping
	public ResponseEntity<Page<UserResponseDto>> getAllUsers(@ParameterObject Pageable pageable) {
		Page<UserResponseDto> users = userService.getAllUsers(pageable).map(UserResponseDto::fromUser);
		return ResponseEntity.ok(users);
	}

	@Operation(summary = "Create a new user", description = "Creates a new user.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Created successfully",
					content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
			@ApiResponse(responseCode = "400", description = "Invalid user data provided", content = @Content),
			@ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content) })
	@PostMapping
	public ResponseEntity<UserResponseDto> createUser(@RequestBody @Valid UserCreateRequestDto requestDto) {
		User createdUser = userService.create(requestDto);
		return new ResponseEntity<>(UserResponseDto.fromUser(createdUser), HttpStatus.CREATED);
	}

	@Operation(summary = "Delete a user", description = "Deletes a user by their ID.")
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Deleted successfully"),
			@ApiResponse(responseCode = "403", description = "Insufficient permissions"),
			@ApiResponse(responseCode = "404", description = "The user with given ID does not exist") })
	@DeleteMapping("/{userId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteUser(@PathVariable UUID userId) {
		userService.deleteUser(userId);
	}

	@Operation(summary = "Update current user's info",
			description = "Updates the details of the currently authenticated user.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful operation",
					content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
			@ApiResponse(responseCode = "400", description = "Invalid data provided", content = @Content),
			@ApiResponse(responseCode = "401", description = "User is not authenticated", content = @Content) })
	@PatchMapping("/@me")
	public ResponseEntity<UserResponseDto> updateCurrentUser(@AuthenticationPrincipal JpaUserDetails userDetails,
			@RequestBody @Valid UserInfoUpdateRequestDto requestDto) {
		User user = userService.updateCurrentUserInfo(userDetails.getId(), requestDto);
		return ResponseEntity.ok(UserResponseDto.fromUser(user));
	}

	@Operation(summary = "Update user details", description = "Updates the given user's details.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successful operation",
					content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
			@ApiResponse(responseCode = "400", description = "Invalid data provided", content = @Content),
			@ApiResponse(responseCode = "401", description = "User is not authenticated", content = @Content),
			@ApiResponse(responseCode = "403", description = "User does not have permission to update this user",
					content = @Content), })
	@PatchMapping("/{userId}")
	public ResponseEntity<UserResponseDto> updateUser(@PathVariable UUID userId,
			@RequestBody @Valid UserInfoUpdateRequestDto requestDto) {
		User user = userService.updateUserInfo(userId, requestDto);
		return ResponseEntity.ok(UserResponseDto.fromUser(user));
	}

	@Operation(summary = "Update user permissions", description = "Sets the permissions for a given user")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Permissions updated successfully",
					content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
			@ApiResponse(responseCode = "400", description = "Invalid permissions value provided", content = @Content),
			@ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content),
			@ApiResponse(responseCode = "404", description = "User not found", content = @Content) })
	@PutMapping("/{userId}/permissions")
	public ResponseEntity<UserResponseDto> updateUserPermissions(@PathVariable UUID userId,
			@RequestBody @Valid UserPermissionsUpdateRequestDto requestDto) {
		User user = userService.updateUserPermissions(userId, requestDto);
		return ResponseEntity.ok(UserResponseDto.fromUser(user));
	}

}

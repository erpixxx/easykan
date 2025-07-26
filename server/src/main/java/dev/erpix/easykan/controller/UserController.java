package dev.erpix.easykan.controller;

import dev.erpix.easykan.model.user.EKUser;
import dev.erpix.easykan.model.user.dto.UserCreateRequestDto;
import dev.erpix.easykan.model.user.dto.UserResponseDto;
import dev.erpix.easykan.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

//    @GetMapping("/count")
//    public long countUsers() {
//        return userService.count();
//    }

    @PostMapping
    @Operation(
            summary = "Create a new user",
            description = "Creates a new user with the provided details."
    )
    @ApiResponse(
            responseCode = "201",
            description = "User created successfully.",
            content = @Content(schema = @Schema(implementation = UserResponseDto.class))
    )
    @ApiResponse(
            responseCode = "409",
            description = "User with the same email or login already exists.",
            content = @Content
    )
    public ResponseEntity<UserResponseDto> createUser(@RequestBody UserCreateRequestDto dto) {
        if (userService.getByEmail(dto.getEmail()).isPresent() || userService.getByLogin(dto.getLogin()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        EKUser savedUser = userService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new UserResponseDto(savedUser));
    }

//    @DeleteMapping("/{uuid}")
//    public ResponseEntity<Void> deleteUser(@PathVariable UUID uuid) {
//        if (!userService.exists(uuid)) {
//            return ResponseEntity.notFound().build();
//        }
//        userService.delete(uuid);
//        return ResponseEntity.noContent().build();
//    }

    @GetMapping("/{uuid}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable UUID uuid) {
        return userService.getById(uuid)
                .map(UserResponseDto::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/@me")
    public ResponseEntity<UserResponseDto> getCurrentUser(Authentication auth) {
        UUID userId = UUID.fromString((String) auth.getPrincipal());
        return userService.getById(userId)
                .map(UserResponseDto::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}

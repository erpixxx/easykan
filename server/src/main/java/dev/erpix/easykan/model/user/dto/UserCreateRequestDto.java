package dev.erpix.easykan.model.user.dto;

import dev.erpix.easykan.model.user.EKUser;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.jetbrains.annotations.NotNull;

public record UserCreateRequestDto(
        @NotBlank String login,
        @NotBlank String displayName,
        @NotBlank @Email String email,
        @Size(min = 8, max = 64) String password,
        boolean canAuthWithPassword
) {

    public @NotNull EKUser toUser() {
        EKUser user = new EKUser();
        user.setLogin(login);
        user.setDisplayName(displayName);
        user.setEmail(email);
        user.setCanAuthWithPassword(canAuthWithPassword);
        if (password != null) {
            user.setPasswordHash(password);
        }
        return user;
    }

}

package dev.erpix.easykan.model.user.dto;

import dev.erpix.easykan.model.user.EKUser;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Getter
@ToString
public class UserResponseDto {

    @NonNull
    private final UUID id;
    @NonNull
    private final String login;
    @NonNull
    private final String displayName;

    public UserResponseDto(@NotNull EKUser user) {
        this.id = user.getId();
        this.login = user.getLogin();
        this.displayName = user.getDisplayName();
    }
}

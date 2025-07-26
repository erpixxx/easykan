package dev.erpix.easykan.model.user.dto;

import dev.erpix.easykan.model.user.EKUser;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter @Setter
@RequiredArgsConstructor
public class UserCreateRequestDto {

    @NonNull
    private final String login;
    @NonNull
    private final String displayName;
    @NonNull
    private final String email;
    private final String password;
    private final boolean canAuthWithPassword;

    public @NotNull EKUser toUser() {
        EKUser user = new EKUser();
        user.setLogin(login);
        user.setDisplayName(displayName);
        user.setEmail(email);
        user.setCanAuthWithPassword(canAuthWithPassword);
        if (password != null) {
            user.setPassword(password);
        }
        return user;
    }

}

package dev.erpix.easykan.server.domain.user.util;

import dev.erpix.easykan.server.domain.user.security.JpaUserDetails;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserDetailsProvider {

    public @NotNull Optional<JpaUserDetails> getCurrentUserDetails() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = auth.getPrincipal();

        if (principal instanceof JpaUserDetails userDetails) {
            return Optional.of(userDetails);
        }

        return Optional.empty();
    }

    public @NotNull JpaUserDetails getRequiredCurrentUserDetails() {
        return getCurrentUserDetails().orElseThrow(() -> new IllegalStateException(
                "Could not find authenticated user details in security context"));
    }
}

package dev.erpix.easykan;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CurrentUser {

    public UUID getId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = auth.getPrincipal();

        if (principal instanceof UUID uuid) {
            return uuid;
        }

        throw new IllegalStateException("Current user is not authenticated or does not have a UUID as principal");
    }

}

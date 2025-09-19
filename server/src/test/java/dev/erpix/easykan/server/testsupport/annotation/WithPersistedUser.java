package dev.erpix.easykan.server.testsupport.annotation;

import dev.erpix.easykan.server.domain.user.model.UserPermission;
import dev.erpix.easykan.server.testsupport.extension.PersistUserExtension;
import java.lang.annotation.*;
import org.junit.jupiter.api.extension.ExtendWith;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@ExtendWith(PersistUserExtension.class)
public @interface WithPersistedUser {

    String login() default Default.LOGIN;

    String displayName() default Default.DISPLAY_NAME;

    String email() default Default.EMAIL;

    String password() default Default.PASSWORD;

    UserPermission[] permissions() default {UserPermission.DEFAULT_PERMISSIONS};

    interface Default {

        String LOGIN = "testuser";

        String DISPLAY_NAME = "Test User";

        String EMAIL = "test.user@easykan.dev";

        String PASSWORD = "password";
    }
}

package dev.erpix.easykan.server.testsupport.security;

import dev.erpix.easykan.server.domain.user.model.UserPermission;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@WithSecurityContext(factory = UserSecurityContextFactory.class)
public @interface WithMockUser {

    String id() default Default.ID;

    String login() default Default.LOGIN;

    String displayName() default Default.DISPLAY_NAME;

    String email() default Default.EMAIL;

    String password() default Default.PASSWORD;

    UserPermission[] permissions() default { UserPermission.DEFAULT_PERMISSIONS };

    interface Default {

        String ID = "00000000-0000-0000-0000-000000000000";

        String LOGIN = "user";

        String DISPLAY_NAME = "User";

        String EMAIL = "user@easykan.dev";

        String PASSWORD = "password";

        UserPermission[] PERMISSIONS = { UserPermission.DEFAULT_PERMISSIONS };

    }

}

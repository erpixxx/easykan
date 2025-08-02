package dev.erpix.easykan.server;

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

    String id() default "00000000-0000-0000-0000-000000000000";

    String name() default "user";

    String displayName() default "User";

    String email() default "user@easykan.dev";

    String password() default "password";

    boolean canAuthWithPassword() default true;

    UserPermission[] permissions() default { UserPermission.DEFAULT_PERMISSIONS};

}

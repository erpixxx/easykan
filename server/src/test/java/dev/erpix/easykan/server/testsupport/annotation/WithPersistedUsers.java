package dev.erpix.easykan.server.testsupport.annotation;

import dev.erpix.easykan.server.testsupport.extension.PersistUserExtension;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@ExtendWith(PersistUserExtension.class)
public @interface WithPersistedUsers {

    WithPersistedUser principal() default @WithPersistedUser;

    WithPersistedUser[] others() default {};
}

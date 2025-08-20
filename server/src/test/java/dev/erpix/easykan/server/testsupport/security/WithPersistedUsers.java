package dev.erpix.easykan.server.testsupport.security;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@ExtendWith(PersistUserExtension.class)
public @interface WithPersistedUsers {

    WithPersistedUser[] value() default {};

}

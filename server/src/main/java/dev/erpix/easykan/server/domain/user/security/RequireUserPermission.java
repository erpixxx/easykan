package dev.erpix.easykan.server.domain.user.security;

import dev.erpix.easykan.server.domain.user.model.UserPermission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RequireUserPermission {

    UserPermission[] value();

}

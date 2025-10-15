package dev.erpix.easykan.server.domain.project.security;

import dev.erpix.easykan.server.domain.project.model.ProjectPermission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RequireProjectPermission {

	ProjectPermission[] value();

	String message() default "You don't have required permissions";

}

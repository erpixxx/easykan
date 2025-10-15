package dev.erpix.easykan.server.testsupport.annotation;

import dev.erpix.easykan.server.domain.user.model.UserPermission;
import dev.erpix.easykan.server.testsupport.annotation.context.UserSecurityContextFactory;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.security.test.context.support.WithSecurityContext;

/**
 * Annotation to be used on test methods to indicate that a mock user should be created in
 * the security context before the test runs.
 * <p>
 * The user will be created according to the specified parameters, and can be accessed in
 * the test using standard Spring Security mechanisms.
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
@WithSecurityContext(factory = UserSecurityContextFactory.class)
public @interface WithSecurityContextUser {

	/**
	 * The ID of the user to be created.
	 * @return the user ID
	 */
	String id() default Default.ID;

	/**
	 * The login of the user to be created.
	 * @return the user login
	 */
	String login() default Default.LOGIN;

	/**
	 * The display name of the user to be created.
	 * @return the user display name
	 */
	String displayName() default Default.DISPLAY_NAME;

	/**
	 * The email of the user to be created.
	 * @return the user email
	 */
	String email() default Default.EMAIL;

	/**
	 * The plain password of the user to be created.
	 * @return the user password
	 */
	String password() default Default.PASSWORD;

	/**
	 * The permissions to be granted to the created user.
	 * @return the user permissions
	 */
	UserPermission[] permissions() default { UserPermission.DEFAULT_PERMISSIONS };

	/**
	 * Default values for the annotation parameters.
	 */
	interface Default {

		String ID = "00000000-0000-0000-0000-000000000000";

		String LOGIN = "user";

		String DISPLAY_NAME = "User";

		String EMAIL = "user@easykan.dev";

		String PASSWORD = "password";

		UserPermission PERMISSIONS = UserPermission.DEFAULT_PERMISSIONS;

	}

}

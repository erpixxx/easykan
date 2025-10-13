package dev.erpix.easykan.server.testsupport.annotation;

import dev.erpix.easykan.server.domain.user.model.UserPermission;
import dev.erpix.easykan.server.testsupport.PersistedDataProvider;
import java.lang.annotation.*;

import dev.erpix.easykan.server.testsupport.extension.TestFixtureExtension;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Annotation to be used on test methods to indicate that a user should be created and
 * persisted before the test runs.
 * <p>
 * The user will be created according to the specified parameters, and can be accessed in
 * the test using {@link PersistedDataProvider}.
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
@ExtendWith(TestFixtureExtension.class)
public @interface WithUser {

	/**
	 * The login of the user to be created.
	 * @return the user login.
	 */
	String login() default Default.LOGIN;

	/**
	 * The display name of the user to be created.
	 * @return the user display name.
	 */
	String displayName() default Default.DISPLAY_NAME;

	/**
	 * The email of the user to be created.
	 * @return the user email.
	 */
	String email() default Default.EMAIL;

	/**
	 * The plain password of the user to be created.
	 * @return the user password.
	 */
	String password() default Default.PASSWORD;

	/**
	 * The permissions to be granted to the created user.
	 * @return the user permissions.
	 */
	UserPermission[] permissions() default { UserPermission.DEFAULT_PERMISSIONS };

	interface Default {

		String LOGIN = "testuser";

		String DISPLAY_NAME = "Test User";

		String EMAIL = "test.user@easykan.dev";

		String PASSWORD = "password";

	}

}

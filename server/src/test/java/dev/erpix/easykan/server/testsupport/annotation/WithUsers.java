package dev.erpix.easykan.server.testsupport.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import dev.erpix.easykan.server.testsupport.extension.TestFixtureExtension;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Container annotation for {@link WithUser} to allow multiple annotations on a single
 * test method.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
@ExtendWith(TestFixtureExtension.class)
public @interface WithUsers {

	/**
	 * The primary user for the test. This user can be considered the "main" user,
	 * typically the one that is authenticated during the test.
	 * @return the primary user specification.
	 */
	WithUser principal() default @WithUser;

	/**
	 * An array of additional {@link WithUser} annotations to create multiple users for
	 * the test. These users can be used to simulate interactions between different users
	 * in the system.
	 * @return the array of additional user specifications.
	 */
	WithUser[] others() default {};

}

package dev.erpix.easykan.server.testsupport.annotation;

import dev.erpix.easykan.server.testsupport.extension.TestFixtureExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Container annotation for {@link WithProject} to allow multiple annotations on a single
 * test method.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
@ExtendWith(TestFixtureExtension.class)
public @interface WithProjects {

	/**
	 * An array of {@link WithProject} annotations.
	 * @return the array of annotations.
	 */
	WithProject[] value() default { @WithProject };

}

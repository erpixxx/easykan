package dev.erpix.easykan.server.testsupport.annotation;

import dev.erpix.easykan.server.domain.project.model.ProjectPermission;
import dev.erpix.easykan.server.testsupport.PersistedDataProvider;
import dev.erpix.easykan.server.testsupport.extension.TestFixtureExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to be used on test methods to indicate that a project should be created and
 * persisted before the test runs.
 * <p>
 * The project will be created according to the specified parameters, and can be accessed
 * in the test using {@link PersistedDataProvider}.
 * </p>
 * <p>
 * By default, the project will be owned by the currently authenticated user, without any
 * additional members. The default ID and name will be used unless specified otherwise.
 * </p>
 * <p>
 * This annotation is typically used in conjunction with {@link WithUser} to ensure that
 * there is a user context.
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
@ExtendWith(TestFixtureExtension.class)
public @interface WithProject {

	/**
	 * The ID of the project to be created.
	 * @return the project ID.
	 */
	String id() default Default.ID;

	/**
	 * The name of the project to be created.
	 * @return the project name.
	 */
	String name() default Default.NAME;

	/**
	 * The owner of the project. Can be either the currently authenticated user or a newly
	 * created user.
	 * @return the project owner specification.
	 */
	OwnerType owner() default OwnerType.CURRENT_USER;

	/**
	 * The number of dummy members to be added to the project. These will be newly created
	 * users.
	 * @return the number of members to add.
	 */
	int memberCount() default 0;

	/**
	 * If {@code true}, the current user will be added as a member of the project with
	 * default permissions. This is only applicable if the owner is not the current user.
	 * @return {@code true} to add the current user as a member, {@code false} otherwise.
	 */
	boolean setCurrentUserAsMember() default false;

	/**
	 * The permissions to be granted to the authenticated user on the created project.
	 * This is only applicable if {@link #setCurrentUserAsMember()} is set to {@code true}
	 * and the owner is not the current user.
	 * @return the array of permissions to be granted.
	 */
	ProjectPermission[] permissions() default {};

	/**
	 * The position of the project in the authenticated user's view. If set to {@code -1},
	 * the project will be added at the end of the list. This is only applicable if
	 * {@link #setCurrentUserAsMember()} is set to {@code true} and the owner is not the
	 * current user.
	 * @return the position index or {@code -1} for the end of the list.
	 */
	int position() default -1;

	/**
	 * Boards to be created and associated with the project.
	 * @return array of {@link BoardSpec} defining the boards to be created.
	 */
	BoardSpec[] boards() default {};

	/**
	 * Default values for the annotation attributes.
	 */
	interface Default {

		String ID = "00000000-0000-0000-0000-000000000001";

		String NAME = "Test Project";

	}

}

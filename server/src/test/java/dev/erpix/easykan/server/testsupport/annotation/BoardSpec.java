package dev.erpix.easykan.server.testsupport.annotation;

import dev.erpix.easykan.server.testsupport.PersistedDataProvider;

/**
 * Annotation to be used on test methods to indicate that a board should be created and
 * persisted before the test runs.
 * <p>
 * The board will be created according to the specified parameters, and can be accessed in
 * the test using {@link PersistedDataProvider}.
 * </p>
 * <p>
 * This annotation is used within the context of a project, which can be set up using
 * {@link WithProject}.
 * </p>
 */
public @interface BoardSpec {

	/**
	 * The name of the board to be created.
	 * @return the board name.
	 */
	String name() default "Test Board";

	/**
	 * The owner of the board. Can be either the currently authenticated user or a newly
	 * created user.
	 * @return the board owner specification.
	 */
	OwnerType owner() default OwnerType.CURRENT_USER;

	/**
	 * The position of the board in the project's board list. If set to {@code -1}, the
	 * board will be added at the end of the list.
	 * @return the position index or {@code -1} for the end of the list.
	 */
	int position() default -1;

}

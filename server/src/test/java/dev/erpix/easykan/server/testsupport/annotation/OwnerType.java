package dev.erpix.easykan.server.testsupport.annotation;

import dev.erpix.easykan.server.testsupport.PersistedDataProvider;

/**
 * Specifies the owner of a persisted entity (e.g., project, board) in test scenarios.
 * Typically used in conjunction with annotation {@link WithProject} and {@link WithUser}.
 * <p>
 * It defines two options for the owner of the entity:
 * </p>
 * <ul>
 * <li>{@link #CURRENT_USER} - The owner will be currently authenticated user.</li>
 * <li>{@link #NEW_USER} - The owner will be a newly created user.</li>
 * </ul>
 */
public enum OwnerType {

	/**
	 * The project will be owned by the currently authenticated user. Must be used in
	 * conjunction with {@link WithUser}.
	 */
	CURRENT_USER,
	/**
	 * The project will be owned by a newly created user. You can retrieve this user in
	 * your tests using {@link PersistedDataProvider}.
	 */
	NEW_USER

}

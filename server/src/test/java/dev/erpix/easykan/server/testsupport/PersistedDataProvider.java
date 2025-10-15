package dev.erpix.easykan.server.testsupport;

import dev.erpix.easykan.server.domain.project.model.Project;
import dev.erpix.easykan.server.domain.project.repository.TestProjectMemberRepository;
import dev.erpix.easykan.server.domain.project.repository.TestProjectRepository;
import dev.erpix.easykan.server.domain.user.model.User;
import dev.erpix.easykan.server.domain.user.repository.TestUserRepository;
import dev.erpix.easykan.server.domain.user.util.UserDetailsProvider;
import dev.erpix.easykan.server.testsupport.annotation.WithProject;
import dev.erpix.easykan.server.testsupport.annotation.WithUser;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A helper bean that could be injected into tests to access persisted test data created
 * by {@link WithUser} and {@link WithProject} annotations.
 *
 * <p>
 * The data is retrieved directly from the database, so it is guaranteed to be consistent
 * with the current transaction state.
 * </p>
 */
@SuppressWarnings("unused") // Some methods might not be used for now, but could be useful
							// later
@Component
public class PersistedDataProvider {

	private final TestProjectRepository projectRepository;

	private final TestProjectMemberRepository projectMemberRepository;

	private final TestUserRepository userRepository;

	private final UserDetailsProvider userDetailsProvider;

	public PersistedDataProvider(TestProjectRepository projectRepository,
			TestProjectMemberRepository projectMemberRepository, TestUserRepository userRepository,
			UserDetailsProvider userDetailsProvider) {
		this.projectRepository = projectRepository;
		this.projectMemberRepository = projectMemberRepository;
		this.userRepository = userRepository;
		this.userDetailsProvider = userDetailsProvider;
	}

	/**
	 * Gets the currently authenticated user.
	 * @return the current user
	 * @throws IllegalStateException if no user is authenticated
	 */
	public User getCurrentUser() {
		try {
			return userDetailsProvider.getRequiredCurrentUserDetails().user();
		}
		catch (IllegalStateException e) {
			throw new IllegalStateException("No authenticated user found in security context. "
					+ "Make sure @WithPersistedUser annotation is present on the test.", e);
		}
	}

	/**
	 * Gets all users in the database.
	 * @return an unmodifiable list of all users
	 */
	public List<User> getAllUsers() {
		return Collections.unmodifiableList(userRepository.findAll());
	}

	/**
	 * Gets all users except the currently authenticated user.
	 * @return a set of all users except the current user
	 */
	public Set<User> getAllUsersExceptCurrent() {
		User currentUser = getCurrentUser();
		return userRepository.findAll()
			.stream()
			.filter(user -> !user.getId().equals(currentUser.getId()))
			.collect(Collectors.toSet());
	}

	/**
	 * Gets the <b>first</b> created user based on creation timestamp.
	 * @return an {@link Optional} containing the first created user, or empty if no users
	 * exist
	 */
	public Optional<User> getFirstCreatedUser() {
		return userRepository.findFirstCreated();
	}

	/**
	 * Gets the <b>last</b> created user based on creation timestamp.
	 * @return an {@link Optional} containing the last created user, or empty if no users
	 * exist
	 */
	public Optional<User> getLastCreatedUser() {
		return userRepository.findLastCreated();
	}

	/**
	 * Finds a user by their ID.
	 * @param id the ID of the user to find
	 * @return an {@link Optional} containing the user if found, or empty if not found
	 */
	public Optional<User> getUserById(UUID id) {
		return userRepository.findById(id);
	}

	/**
	 * Finds a user by their login.
	 * @param login the login of the user to find
	 * @return an {@link Optional} containing the user if found, or empty if not found
	 */
	public Optional<User> getUserByLogin(String login) {
		return userRepository.findByLogin(login);
	}

	/**
	 * Gets all projects in the database.
	 * @return an unmodifiable list of all projects
	 */
	public List<Project> getAllProjects() {
		return Collections.unmodifiableList(projectRepository.findAll());
	}

	/**
	 * Gets the <b>first</b> created project based on creation timestamp.
	 * @return an {@link Optional} containing the first created project, or empty if no
	 * projects exist
	 */
	public Optional<Project> getFirstCreatedProject() {
		return projectRepository.findFirstCreated();
	}

	/**
	 * Gets the <b>last</b> created project based on creation timestamp.
	 * @return an {@link Optional} containing the last created project, or empty if no
	 * projects exist
	 */
	public Optional<Project> getLastCreatedProject() {
		return projectRepository.findLastCreated();
	}

	/**
	 * Finds a project by its ID.
	 * @param id the ID of the project to find
	 * @return an {@link Optional} containing the project if found, or empty if not found
	 */
	public Optional<Project> getProjectById(UUID id) {
		return projectRepository.findById(id);
	}

	/**
	 * Finds projects by their name.
	 * @param name the name of the projects to find
	 * @return an unmodifiable list of projects with the specified name
	 */
	public List<Project> getProjectsByName(String name) {
		return Collections.unmodifiableList(projectRepository.findByName(name));
	}

	/**
	 * Gets all projects where the currently authenticated user is a member.
	 * @return an unmodifiable list of projects for the current user
	 */
	public List<Project> getProjectsForCurrentUser() {
		return Collections.unmodifiableList(projectMemberRepository.findProjectsByUserId(getCurrentUser().getId()));
	}

	/**
	 * Gets all projects owned by the currently authenticated user.
	 * @return an unmodifiable list of projects owned by the current user
	 */
	public List<Project> getProjectsOwnedByCurrentUser() {
		return Collections.unmodifiableList(projectRepository.findOwnedProjectsByUserId(getCurrentUser().getId()));
	}

}

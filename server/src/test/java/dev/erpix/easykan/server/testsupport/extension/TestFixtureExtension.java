package dev.erpix.easykan.server.testsupport.extension;

import dev.erpix.easykan.server.domain.PermissionUtils;
import dev.erpix.easykan.server.domain.board.model.Board;
import dev.erpix.easykan.server.domain.board.repository.BoardRepository;
import dev.erpix.easykan.server.domain.project.model.Project;
import dev.erpix.easykan.server.domain.project.model.ProjectMember;
import dev.erpix.easykan.server.domain.project.model.ProjectPermission;
import dev.erpix.easykan.server.domain.project.model.ProjectUserView;
import dev.erpix.easykan.server.domain.project.repository.ProjectRepository;
import dev.erpix.easykan.server.domain.project.repository.ProjectUserViewRepository;
import dev.erpix.easykan.server.domain.user.model.User;
import dev.erpix.easykan.server.domain.user.model.UserPermission;
import dev.erpix.easykan.server.domain.user.repository.UserRepository;
import dev.erpix.easykan.server.domain.user.security.JpaUserDetails;
import dev.erpix.easykan.server.testsupport.TestPrepareException;
import dev.erpix.easykan.server.testsupport.annotation.*;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

public class TestFixtureExtension implements AfterEachCallback, BeforeEachCallback {

	@Override
	public void afterEach(ExtensionContext context) {
		SecurityContextHolder.clearContext();
		CacheManager cacheManager = SpringExtension.getApplicationContext(context).getBean(CacheManager.class);
		cacheManager.getCacheNames().forEach(cacheName -> {
			Cache cache = cacheManager.getCache(cacheName);
			if (cache != null) {
				cache.clear();
			}
		});
	}

	@Override
	public void beforeEach(ExtensionContext context) {
		AnnotationSupport.findAnnotation(context.getElement(), WithUsers.class).ifPresent(ann -> {
			User principal = createPersistedUser(context, ann.principal());
			setAuthentication(principal);
			Arrays.stream(ann.others()).forEach(o -> createPersistedUser(context, o));
		});
		AnnotationSupport.findAnnotation(context.getElement(), WithUser.class).ifPresent(ann -> {
			User user = createPersistedUser(context, ann);
			setAuthentication(user);
		});

		AnnotationSupport.findAnnotation(context.getElement(), WithProjects.class).ifPresent(ann -> {
			Arrays.stream(ann.value()).forEach(proj -> createPersistedProject(context, proj));
		});
		AnnotationSupport.findAnnotation(context.getElement(), WithProject.class).ifPresent(ann -> {
			createPersistedProject(context, ann);
		});

	}

	private static void log(String message) {
		System.out.println("[" + TestFixtureExtension.class.getName() + "] " + message);
	}

	private User createPersistedUser(ExtensionContext context, WithUser ann) {
		ApplicationContext appCtx = SpringExtension.getApplicationContext(context);
		UserRepository userRepository = appCtx.getBean(UserRepository.class);
		PasswordEncoder passwordEncoder = appCtx.getBean(PasswordEncoder.class);

		String login = ann.login();
		String displayName = ann.displayName();
		String email = ann.email();
		String password = ann.password();
		UserPermission[] permissions = ann.permissions();

		log("Creating user:");
		log("|-login: " + login);
		log("|-displayName: " + displayName);
		log("|-email: " + email);
		log("|-password: " + password);
		log("|-permissions: " + Arrays.toString(permissions));
		User user = User.builder()
			.login(login)
			.displayName(displayName)
			.email(email)
			.passwordHash(passwordEncoder.encode(password))
			.permissions(PermissionUtils.toValue(permissions))
			.build();

		return userRepository.save(user);
	}

	private void setAuthentication(User user) {
		JpaUserDetails userDetails = new JpaUserDetails(user);
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
				userDetails.getAuthorities());

		SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
		securityContext.setAuthentication(authentication);
		SecurityContextHolder.setContext(securityContext);

		log("Set authenticated user to: " + user);
	}

	private void createPersistedProject(ExtensionContext context, WithProject ann) {
		ApplicationContext appCtx = SpringExtension.getApplicationContext(context);
		UserRepository userRepository = appCtx.getBean(UserRepository.class);
		ProjectRepository projectRepository = appCtx.getBean(ProjectRepository.class);
		ProjectUserViewRepository projectUserViewRepository = appCtx.getBean(ProjectUserViewRepository.class);
		BoardRepository boardRepository = appCtx.getBean(BoardRepository.class);

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !(authentication.getPrincipal() instanceof JpaUserDetails userDetails)) {
			throw new TestPrepareException("WithProject requires an authenticated user. Use '"
					+ WithUser.class.getName() + "' or '" + WithUsers.class.getName() + "' to set up a user context.");
		}
		User currentUser = userRepository.findById(userDetails.getId())
			.orElseThrow(() -> new TestPrepareException(
					"Authenticated user with id " + userDetails.getId() + " not found in the database."));

		String projectName = ann.name();
		Project project = Project.builder().name(projectName).build();
		Set<ProjectMember> members = new HashSet<>();
		Set<ProjectUserView> userViews = new HashSet<>();

		log("Creating project with name '" + projectName + "'");

		// Determine position for current user if they will be added as member
		int currentUserPosition = ann.position() == -1
				? projectUserViewRepository.findNextPositionByUserId(currentUser.getId()) : ann.position();

		// Determine and set the owner
		OwnerType ownerType = ann.owner();
		boolean isCurrentUserOwner = ownerType == OwnerType.CURRENT_USER;
		User owner;
		if (ownerType == OwnerType.CURRENT_USER) {
			log("  Setting current user as project owner");
			owner = currentUser;
		}
		else if (ownerType == OwnerType.NEW_USER) {
			owner = createDummyUser(userRepository);
			log("  Creating new user as project owner: " + owner);
		}
		else {
			throw new IllegalStateException("Unknown PersistedOwner type: " + ownerType);
		}
		project.setOwner(owner);

		// Add owner as project member with OWNER permission
		ProjectMember ownerMember = ProjectMember.builder()
			.user(owner)
			.project(project)
			.permissions(ProjectPermission.OWNER.getValue())
			.build();
		members.add(ownerMember);

		// Add owner to user views
		int position = 0;
		if (isCurrentUserOwner) {
			position = currentUserPosition;
		}
		ProjectUserView ownerView = ProjectUserView.builder().user(owner).project(project).position(position).build();
		userViews.add(ownerView);

		// Create dummy members if specified
		int memberCount = ann.memberCount();
		if (memberCount > 0) {
			log("  Adding " + memberCount + " dummy members to project");
		}
		for (int i = 0; i < memberCount; i++) {
			User dummyMember = createDummyUser(userRepository);
			log("  " + (i + 1) + ") " + dummyMember);
			ProjectMember member = ProjectMember.builder()
				.user(dummyMember)
				.project(project)
				.permissions(ProjectPermission.VIEWER.getValue())
				.build();
			members.add(member);

			ProjectUserView userView = ProjectUserView.builder().user(dummyMember).project(project).position(0).build();
			userViews.add(userView);
		}

		// Create view and member for current user if specified
		// This is skipped if the current user is already the owner
		if (ann.setCurrentUserAsMember() && !isCurrentUserOwner) {
			log("  Adding current user as member to project '" + projectName + "'");

			long perm = PermissionUtils.toValue(ann.permissions());
			log("  |-permissions: " + Arrays.toString(ann.permissions()));
			ProjectMember currentUserMember = ProjectMember.builder()
				.user(currentUser)
				.project(project)
				.permissions(perm)
				.build();
			members.add(currentUserMember);

			log("  |-position: " + currentUserPosition);
			ProjectUserView currentUserView = ProjectUserView.builder()
				.user(currentUser)
				.project(project)
				.position(currentUserPosition)
				.build();
			userViews.add(currentUserView);
		}

		// Create boards if specified
		BoardSpec[] boardSpecs = ann.boards();
		Set<Board> boards = new HashSet<>();
		if (boardSpecs.length > 0) {
			log("  Creating " + boardSpecs.length + " boards:");
		}
		for (BoardSpec boardSpec : boardSpecs) {
			String boardName = boardSpec.name();
			log("    Creating board with name '" + boardName + "'");

			User boardOwner;
			if (boardSpec.owner() == OwnerType.CURRENT_USER) {
				boardOwner = currentUser;
				log("      Setting current user as board owner" );
			}
			else if (boardSpec.owner() == OwnerType.NEW_USER) {
				boardOwner = createDummyUser(userRepository);
				log("      Creating new user as board owner: " + boardOwner);
			}
			else {
				throw new TestPrepareException("Unknown PersistedOwner type: " + boardSpec.owner());
			}

			int boardPosition = boardSpec.position() == -1
					? boards.size() : boardSpec.position();
			log("	    Setting board position to " + boardPosition);

			Board board = Board.builder()
				.name(boardName)
				.project(project)
				.owner(boardOwner)
				.position(boardPosition)
				.build();
			boards.add(board);
		}
		project.setUserViews(userViews);
		project.setMembers(members);
		project.setBoards(boards);

		projectRepository.save(project);
	}

	private User createDummyUser(UserRepository userRepository) {
		String rnd = UUID.randomUUID().toString().substring(0, 8);
		User newUser = User.builder()
			.login(rnd)
			.displayName(rnd)
			.email(rnd + "@easykan.dev")
			.passwordHash("hashedPassword")
			.build();
		return userRepository.save(newUser);
	}

}

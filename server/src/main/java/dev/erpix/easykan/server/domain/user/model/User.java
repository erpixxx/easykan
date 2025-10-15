package dev.erpix.easykan.server.domain.user.model;

import dev.erpix.easykan.server.domain.auth.model.OAuthAccount;
import dev.erpix.easykan.server.domain.board.model.Board;
import dev.erpix.easykan.server.domain.card.model.CardAssignee;
import dev.erpix.easykan.server.domain.comment.model.Comment;
import dev.erpix.easykan.server.domain.project.model.Project;
import dev.erpix.easykan.server.domain.project.model.ProjectMember;
import dev.erpix.easykan.server.domain.project.model.ProjectUserView;
import dev.erpix.easykan.server.domain.task.model.Task;
import dev.erpix.easykan.server.domain.token.model.RefreshToken;
import jakarta.persistence.*;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@Builder
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users", schema = "public",
		indexes = { @Index(name = "users_login_idx", columnList = "login"),
				@Index(name = "users_display_name_idx", columnList = "display_name") },
		uniqueConstraints = { @UniqueConstraint(name = "users_login_key", columnNames = { "login" }),
				@UniqueConstraint(name = "users_email_key", columnNames = { "email" }) })
public class User {

	@Builder.Default
	@EqualsAndHashCode.Include
	@ToString.Include
	@Id
	@Column(name = "id", nullable = false)
	private UUID id = UUID.randomUUID();

	@ToString.Include
	@Size(max = 64)
	@NotNull
	@Column(name = "login", nullable = false, length = 64)
	private String login;

	@ToString.Include
	@Size(max = 64)
	@NotNull
	@Column(name = "display_name", nullable = false, length = 64)
	private String displayName;

	@ToString.Include
	@Size(max = 255)
	@NotNull
	@Column(name = "email", nullable = false)
	private String email;

	@Size(max = 255)
	@Column(name = "password_hash")
	private String passwordHash;

	@NotNull
	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	@Column(name = "last_login_at")
	private Instant lastLogin;

	@ToString.Include
	@NotNull
	@Column(name = "permissions", nullable = false)
	private Long permissions;

	@Builder.Default
	@OneToMany(mappedBy = "user")
	private Set<OAuthAccount> OAuthAccounts = new LinkedHashSet<>();

	@Builder.Default
	@OneToMany(mappedBy = "user")
	private Set<RefreshToken> refreshTokens = new LinkedHashSet<>();

	@Builder.Default
	@OneToMany(mappedBy = "owner")
	private Set<Project> ownedProjects = new LinkedHashSet<>();

	@Builder.Default
	@OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
	private Set<ProjectMember> projectMembers = new LinkedHashSet<>();

	@Builder.Default
	@OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
	private Set<ProjectUserView> projectViews = new LinkedHashSet<>();

	@Builder.Default
	@OneToMany(mappedBy = "owner")
	private Set<Board> ownedBoards = new LinkedHashSet<>();

	@Builder.Default
	@OneToMany(mappedBy = "user")
	private Set<CardAssignee> assignments = new LinkedHashSet<>();

	@Builder.Default
	@OneToMany(mappedBy = "assignedByUser")
	private Set<CardAssignee> createdAssignments = new LinkedHashSet<>();

	@Builder.Default
	@OneToMany(mappedBy = "author")
	private Set<Comment> comments = new LinkedHashSet<>();

	@Builder.Default
	@OneToMany(mappedBy = "createdByUser")
	private Set<Task> createdTasks = new LinkedHashSet<>();

	@Builder.Default
	@OneToMany(mappedBy = "completedByUser")
	private Set<Task> completedTasks = new LinkedHashSet<>();

	@PrePersist
	protected void onCreate() {
		if (this.createdAt == null) {
			this.createdAt = Instant.now();
		}
		if (this.permissions == null) {
			this.permissions = UserPermission.DEFAULT_PERMISSIONS.getValue();
		}
	}

}

package dev.erpix.easykan.server.domain.task.model;

import dev.erpix.easykan.server.domain.card.model.Card;
import dev.erpix.easykan.server.domain.user.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tasks", schema = "public")
public class Task {

	@Builder.Default
	@EqualsAndHashCode.Include
	@ToString.Include
	@Id
	@Column(name = "id", nullable = false)
	private UUID id = UUID.randomUUID();

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "card_id", nullable = false)
	private Card card;

	@ManyToOne(fetch = FetchType.LAZY)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "parent_task_id")
	private Task parentTask;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "created_by_user_id", nullable = false)
	private User createdByUser;

	@ToString.Include
	@Size(max = 255)
	@NotNull
	@Column(name = "name", nullable = false)
	private String name;

	@NotNull
	@Column(name = "position", nullable = false)
	private Integer position;

	@Builder.Default
	@NotNull
	@ColumnDefault("false")
	@Column(name = "is_completed", nullable = false)
	private Boolean isCompleted = false;

	@Column(name = "completed_at")
	private Instant completedAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@OnDelete(action = OnDeleteAction.SET_NULL)
	@JoinColumn(name = "completed_by_user_id")
	private User completedByUser;

	@NotNull
	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	@NotNull
	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	@Builder.Default
	@OneToMany(mappedBy = "parentTask", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Task> subTasks = new LinkedHashSet<>();

	@PrePersist
	protected void onCreate() {
		Instant now = Instant.now();
		if (this.createdAt == null) {
			createdAt = now;
		}
		if (this.updatedAt == null) {
			updatedAt = now;
		}
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = Instant.now();
	}

}

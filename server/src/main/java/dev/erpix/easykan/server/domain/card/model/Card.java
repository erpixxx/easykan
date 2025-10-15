package dev.erpix.easykan.server.domain.card.model;

import dev.erpix.easykan.server.domain.column.model.BoardColumn;
import dev.erpix.easykan.server.domain.comment.model.Comment;
import dev.erpix.easykan.server.domain.task.model.Task;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import lombok.*;
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
@Table(name = "cards", schema = "public",
		indexes = { @Index(name = "cards_column_id_position_idx", columnList = "column_id, position", unique = true) })
public class Card {

	@Builder.Default
	@EqualsAndHashCode.Include
	@ToString.Include
	@Id
	@Column(name = "id", nullable = false)
	private UUID id = UUID.randomUUID();

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "column_id", nullable = false)
	private BoardColumn column;

	@ToString.Include
	@Size(max = 64)
	@NotNull
	@Column(name = "name", nullable = false, length = 64)
	private String name;

	@Column(name = "description", length = Integer.MAX_VALUE)
	private String description;

	@NotNull
	@Column(name = "position", nullable = false)
	private Integer position;

	@Column(name = "due_date")
	private Instant dueDate;

	@NotNull
	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	@NotNull
	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	@Builder.Default
	@OneToMany(mappedBy = "card", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<CardAssignee> assignees = new LinkedHashSet<>();

	@Builder.Default
	@OneToMany(mappedBy = "card", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<CardLabel> labels = new LinkedHashSet<>();

	@Builder.Default
	@OneToMany(mappedBy = "card", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Comment> comments = new LinkedHashSet<>();

	@Builder.Default
	@OneToMany(mappedBy = "card", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Task> tasks = new LinkedHashSet<>();

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

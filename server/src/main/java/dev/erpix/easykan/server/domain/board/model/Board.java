package dev.erpix.easykan.server.domain.board.model;

import dev.erpix.easykan.server.domain.column.model.BoardColumn;
import dev.erpix.easykan.server.domain.project.model.Project;
import dev.erpix.easykan.server.domain.user.model.User;
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
@Table(name = "boards", schema = "public")
public class Board {

	@Builder.Default
	@EqualsAndHashCode.Include
	@ToString.Include
	@Id
	@Column(name = "id", nullable = false)
	private UUID id = UUID.randomUUID();

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "project_id", nullable = false)
	private Project project;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "owner_id", nullable = false)
	private User owner;

	@ToString.Include
	@Size(max = 64)
	@NotNull
	@Column(name = "name", nullable = false, length = 64)
	private String name;

	@NotNull
	@Column(name = "position", nullable = false)
	private Integer position;

	@NotNull
	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	@NotNull
	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	@Builder.Default
	@OneToMany(mappedBy = "board")
	private Set<BoardColumn> columns = new LinkedHashSet<>();

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

package dev.erpix.easykan.server.domain.board.model;

import dev.erpix.easykan.server.domain.column.model.BoardColumn;
import dev.erpix.easykan.server.domain.project.model.Project;
import dev.erpix.easykan.server.domain.user.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Getter @Setter
@Builder
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor @NoArgsConstructor
@Entity
@Table(name = "boards", schema = "public", indexes = {
        @Index(name = "boards_project_id_idx", columnList = "project_id"),
        @Index(name = "boards_owner_id_idx", columnList = "owner_id")
})
public class Board {

    @ToString.Include
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

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

    @OneToMany(mappedBy = "board")
    private Set<BoardColumn> columns = new LinkedHashSet<>();

    @OneToMany(mappedBy = "board")
    private Set<Label> labels = new LinkedHashSet<>();

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

}
package dev.erpix.easykan.server.domain.project.model;

import dev.erpix.easykan.server.domain.board.model.Board;
import dev.erpix.easykan.server.domain.user.model.User;
import jakarta.persistence.*;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

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
@Table(name = "projects", schema = "public", indexes = {
        @Index(name = "projects_owner_id_idx", columnList = "owner_id"),
        @Index(name = "projects_name_idx", columnList = "name")
})
public class Project {

    @ToString.Include
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @ToString.Include
    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "project")
    private Set<Board> boards = new LinkedHashSet<>();

    @OneToMany(mappedBy = "project")
    private Set<ProjectMember> projectMembers = new LinkedHashSet<>();

}
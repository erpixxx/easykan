package dev.erpix.easykan.server.domain.project.model;

import dev.erpix.easykan.server.domain.user.model.EKUser;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.UUID;

@Getter @Setter
@ToString
@Builder
@AllArgsConstructor @NoArgsConstructor
@Entity
@Table(name = "projects")
public class EKProject {

    @Id
    @Column(name = "uuid")
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_uuid", nullable = false)
    private EKUser owner;

    @Column(name = "name", nullable = false)
    private String name;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EKUserProject> userAccesses;

}

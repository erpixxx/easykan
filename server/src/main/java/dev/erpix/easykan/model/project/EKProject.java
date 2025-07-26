package dev.erpix.easykan.model.project;

import dev.erpix.easykan.model.user.EKUser;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter @Setter
@ToString
@Builder
@AllArgsConstructor @NoArgsConstructor
@Entity
@Table(name = "easykan_projects")
public class EKProject {

    @Id
    @Column(name = "project_uuid")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_owner_uuid", nullable = false)
    private EKUser owner;

    @Column(name = "project_name", nullable = false)
    private String name;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EKUserProject> userAccesses;

}

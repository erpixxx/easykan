package dev.erpix.easykan.server.domain.project.model;

import dev.erpix.easykan.server.domain.user.model.EKUser;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter @Setter
@Builder
@AllArgsConstructor @NoArgsConstructor
@Entity
@Table(name = "project_members")
public class EKUserProject {

    @EmbeddedId
    private Id id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("projectId")
    @JoinColumn(name = "project_uuid")
    private EKProject project;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "member_uuid")
    private EKUser user;

    @Column(name = "permissions")
    private long permissions;

    @Getter @Setter
    @EqualsAndHashCode @ToString
    @AllArgsConstructor @NoArgsConstructor
    @Embeddable
    public static class Id implements Serializable {

        @Column(name = "member_uuid")
        private UUID userId;

        @Column(name = "project_uuid")
        private UUID projectId;

    }

}

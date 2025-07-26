package dev.erpix.easykan.model.project;

import dev.erpix.easykan.model.user.EKUser;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter @Setter
@Builder
@AllArgsConstructor @NoArgsConstructor
@Entity
@Table(name = "easykan_user_projects")
public class EKUserProject {

    @EmbeddedId
    private Id id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_uuid")
    private EKUser user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("projectId")
    @JoinColumn(name = "project_uuid")
    private EKProject project;

    @Column(name = "permissions")
    private long permissions;

    @Getter @Setter
    @EqualsAndHashCode @ToString
    @AllArgsConstructor @NoArgsConstructor
    @Embeddable
    public static class Id implements Serializable {

        @Column(name = "user_uuid")
        private UUID userId;

        @Column(name = "project_uuid")
        private UUID projectId;

    }

}

package dev.erpix.easykan.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.erpix.easykan.model.project.EKUserProject;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter @Setter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "easykan_users")
public class EKUser {

    @Id
    @Column(name = "user_uuid")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_login", nullable = false, length = 64)
    private String login;

    @Column(name = "user_display_name", nullable = false, length = 64)
    private String displayName;

    @JsonIgnore
    @Column(name = "user_email", nullable = false, unique = true)
    private String email;

    @JsonIgnore
    @Column(name = "user_password")
    private String password;

    @Column(name = "user_created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "user_last_login")
    private LocalDateTime lastLogin;

    @Column(name = "user_is_admin", nullable = false)
    private boolean isAdmin;

    @JsonIgnore
    @Column(name = "user_can_auth_with_password", nullable = false)
    private boolean canAuthWithPassword;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<EKUserProject> projects;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

}

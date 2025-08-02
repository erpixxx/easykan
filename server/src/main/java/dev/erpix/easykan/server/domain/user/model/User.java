package dev.erpix.easykan.server.domain.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.erpix.easykan.server.domain.project.model.ProjectMember;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter @Setter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(name = "uuid")
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @Column(name = "login", nullable = false, unique = true, length = 64)
    private String login;

    @Column(name = "display_name", nullable = false, length = 64)
    private String displayName;

    @JsonIgnore
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @JsonIgnore
    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "permissions", nullable = false)
    private long permissions;

    @JsonIgnore
    @Column(name = "can_auth_with_password", nullable = false)
    private boolean canAuthWithPassword;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<ProjectMember> projects;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

}

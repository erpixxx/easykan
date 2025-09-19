package dev.erpix.easykan.server.domain.token.model;

import dev.erpix.easykan.server.domain.user.model.User;
import jakarta.persistence.*;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Getter @Setter
@Builder
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor @NoArgsConstructor
@Entity
@Table(name = "user_tokens", schema = "public", indexes = {
        @Index(name = "user_tokens_user_id_idx", columnList = "user_id"),
        @Index(name = "user_tokens_expires_at_idx", columnList = "expires_at")
}, uniqueConstraints = {
        @UniqueConstraint(name = "user_tokens_selector_key", columnNames = {"selector"})
})
public class RefreshToken {

    @ToString.Include
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ToString.Include
    @Size(max = 255)
    @NotNull
    @Column(name = "selector", nullable = false)
    private String selector;

    @ToString.Include
    @Size(max = 255)
    @NotNull
    @Column(name = "validator_hash", nullable = false)
    private String validator;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ToString.Include
    @NotNull
    @ColumnDefault("now()")
    @Column(name = "issued_at", nullable = false)
    private Instant issuedAt;

    @ToString.Include
    @NotNull
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @ToString.Include
    @NotNull
    @ColumnDefault("false")
    @Column(name = "revoked", nullable = false)
    private boolean revoked = false;

    @PrePersist
    protected void onCreate() {
        if (this.issuedAt == null) {
            this.issuedAt = Instant.now();
        }
    }

}
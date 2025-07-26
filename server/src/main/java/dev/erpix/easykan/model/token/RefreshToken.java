package dev.erpix.easykan.model.token;

import dev.erpix.easykan.model.user.EKUser;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@ToString
@Builder
@AllArgsConstructor @NoArgsConstructor
@Entity
@Table(name = "easykan_user_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private long id;

    @Column(name = "selector", nullable = false, unique = true)
    private String selector;

    @Column(name = "validator", nullable = false)
    private String validator;

    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "revoked", nullable = false)
    private boolean revoked;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_uuid")
    private EKUser user;

    @PrePersist
    protected void onCreate() {
        this.issuedAt = LocalDateTime.now();
    }

}

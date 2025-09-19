package dev.erpix.easykan.server.domain.auth.model;

import dev.erpix.easykan.server.domain.user.model.User;
import jakarta.persistence.*;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Builder
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_oauth_accounts", schema = "public",
        indexes = {
                @Index(name = "user_oauth_accounts_provider_name_provider_id_idx",
                        columnList = "provider_name, provider_id", unique = true),
                @Index(name = "user_oauth_accounts_user_id_idx", columnList = "user_id")})
public class OAuthAccount {

    @ToString.Include
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ToString.Include
    @Size(max = 255)
    @NotNull
    @Column(name = "provider_id", nullable = false)
    private String providerId;

    @ToString.Include
    @Size(max = 255)
    @NotNull
    @Column(name = "provider_name", nullable = false)
    private String providerName;
}

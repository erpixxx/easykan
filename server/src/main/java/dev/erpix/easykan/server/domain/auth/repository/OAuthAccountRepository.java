package dev.erpix.easykan.server.domain.auth.repository;

import dev.erpix.easykan.server.domain.auth.model.OAuthAccount;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OAuthAccountRepository extends JpaRepository<OAuthAccount, Long> {

    boolean existsByUserIdAndProviderId(UUID userId, @Size(max = 255) @NotNull String providerId);

    Optional<OAuthAccount> findByProviderIdAndProviderName(
            @Size(max = 255) @NotNull String providerId,
            @Size(max = 255) @NotNull String providerName);
}

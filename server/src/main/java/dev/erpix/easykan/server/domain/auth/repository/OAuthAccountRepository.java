package dev.erpix.easykan.server.domain.auth.repository;

import dev.erpix.easykan.server.domain.auth.model.OAuthAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OAuthAccountRepository extends JpaRepository<OAuthAccount, Long> {

    Optional<OAuthAccount> findByProviderIdAndUser_Email(String providerId, String userEmail);

    Optional<OAuthAccount> findByProviderIdAndProviderName(String providerId, String providerName);
}

package dev.erpix.easykan.repository;

import dev.erpix.easykan.model.token.RefreshToken;
import dev.erpix.easykan.model.user.EKUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<RefreshToken, Long> {

    void deleteAllByExpiresAtBefore(LocalDateTime expiresAtBefore);

    List<RefreshToken> findByUserAndExpiresAtBefore(EKUser user, LocalDateTime expiresAtBefore);

    List<RefreshToken> findByUserAndExpiresAtAfter(EKUser user, LocalDateTime expiresAtAfter);

    Optional<RefreshToken> findBySelectorAndRevokedFalseAndExpiresAtAfter(String selector, LocalDateTime expiresAtAfter);

    List<RefreshToken> findByUserAndRevokedFalse(EKUser user);
}

package dev.erpix.easykan.server.domain.token.repository;

import dev.erpix.easykan.server.domain.token.model.RefreshToken;
import dev.erpix.easykan.server.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<RefreshToken, Long> {

    void deleteAllByExpiresAtBefore(LocalDateTime expiresAtBefore);

    List<RefreshToken> findByUserAndExpiresAtBefore(User user, LocalDateTime expiresAtBefore);

    List<RefreshToken> findByUserAndExpiresAtAfter(User user, LocalDateTime expiresAtAfter);

    Optional<RefreshToken> findBySelectorAndRevokedFalseAndExpiresAtAfter(String selector, LocalDateTime expiresAtAfter);

    List<RefreshToken> findByUserAndRevokedFalse(User user);
}

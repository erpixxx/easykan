package dev.erpix.easykan.server.domain.token.repository;

import dev.erpix.easykan.server.domain.token.model.RefreshToken;
import dev.erpix.easykan.server.domain.user.model.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<RefreshToken, Long> {

    void deleteAllByExpiresAtBefore(Instant expiresAtBefore);

    Optional<RefreshToken> findBySelector(String selector);

    Optional<RefreshToken> findBySelectorAndRevokedFalseAndExpiresAtAfter(String selector, Instant expiresAtAfter);

    List<RefreshToken> findByUserAndRevokedFalse(User user);

    List<RefreshToken> findByUserAndRevokedTrue(User user);

    @Modifying(clearAutomatically = true)
    @Query("""
    UPDATE RefreshToken rt SET rt.revoked = true
        WHERE rt.revoked = false
            AND rt.user = :user
            AND rt.expiresAt > :expiresAtAfter""")
    void revokeAllByUserAndExpiresAtAfter(@NotNull User user, @NotNull Instant expiresAtAfter);

}

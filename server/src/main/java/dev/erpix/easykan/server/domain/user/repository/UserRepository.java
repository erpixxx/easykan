package dev.erpix.easykan.server.domain.user.repository;

import dev.erpix.easykan.server.domain.user.model.EKUser;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<EKUser, UUID> {

    @NotNull Optional<EKUser> findByEmail(@NotNull String email);

    @NotNull Optional<EKUser> findByLogin(String login);

    @NotNull List<EKUser> findByDisplayName(String displayName);

    @NotNull List<EKUser> findByDisplayNameStartingWith(String displayName);
}

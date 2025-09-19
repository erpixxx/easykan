package dev.erpix.easykan.server.domain.user.repository;

import dev.erpix.easykan.server.domain.user.model.User;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByLogin(@Size(max = 64) @NotNull String login);

    Optional<User> findByLogin(String login);

    Optional<User> findByEmail(@Size(max = 255) @NotNull String email);
}

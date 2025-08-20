package dev.erpix.easykan.server.domain.user.repository;

import dev.erpix.easykan.server.domain.user.model.User;
import jakarta.validation.constraints.Size;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    @NotNull Optional<User> findByLogin(String login);

    boolean existsUserByLogin(@Size(max = 64) @jakarta.validation.constraints.NotNull String login);

    boolean existsByEmailAndIdNot(@Size(max = 255) @jakarta.validation.constraints.NotNull String email, UUID uuid);

    boolean existsByLoginAndIdNot(@Size(max = 64) @jakarta.validation.constraints.NotNull String login, UUID uuid);

    void deleteByLogin(@Size(max = 64) @jakarta.validation.constraints.NotNull String login);
}

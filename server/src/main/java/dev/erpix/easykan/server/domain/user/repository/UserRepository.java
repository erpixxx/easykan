package dev.erpix.easykan.server.domain.user.repository;

import dev.erpix.easykan.server.domain.user.model.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    @NotNull Optional<User> findByLogin(String login);

}

package dev.erpix.easykan.server.domain.user.repository;

import dev.erpix.easykan.server.domain.user.model.User;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TestUserRepository extends UserRepository {

	@Query("""
			SELECT u FROM User u
			ORDER BY u.createdAt
			LIMIT 1
			""")
	Optional<User> findFirstCreated();

	@Query("""
			SELECT u FROM User u
			ORDER BY u.createdAt DESC
			LIMIT 1
			""")
	Optional<User> findLastCreated();

}

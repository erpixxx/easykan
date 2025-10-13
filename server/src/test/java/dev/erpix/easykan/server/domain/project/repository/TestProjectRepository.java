package dev.erpix.easykan.server.domain.project.repository;

import dev.erpix.easykan.server.domain.project.model.Project;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TestProjectRepository extends ProjectRepository {

	@Query("""
			    SELECT p FROM Project p
			    	WHERE p.owner.id = :userId
			""")
	List<Project> findOwnedProjectsByUserId(@Param("userId") UUID userId);

	List<Project> findByName(@Size(max = 255) @NotNull String name);

	@Query("""
			SELECT p FROM Project p
			ORDER BY p.createdAt
			LIMIT 1
			""")
	Optional<Project> findFirstCreated();

	@Query("""
			SELECT p FROM Project p
			ORDER BY p.createdAt DESC
			LIMIT 1
			""")
	Optional<Project> findLastCreated();

}

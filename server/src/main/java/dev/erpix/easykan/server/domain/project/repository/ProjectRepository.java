package dev.erpix.easykan.server.domain.project.repository;

import dev.erpix.easykan.server.domain.project.model.Project;

import java.util.Set;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Primary
@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {

	long countByOwner_Login(@Size(max = 64) @NotNull String ownerLogin);

	@Query("""
				SELECT p FROM Project p
					JOIN FETCH p.members m
					JOIN FETCH p.userViews v
					WHERE m.user.id = :userId
			""")
	Set<Project> findAllForUserWithDetails(@Param("userId") UUID userId);

}

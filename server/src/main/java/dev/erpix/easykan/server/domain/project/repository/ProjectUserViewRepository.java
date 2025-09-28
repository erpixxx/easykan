package dev.erpix.easykan.server.domain.project.repository;

import dev.erpix.easykan.server.domain.project.model.ProjectUserView;
import dev.erpix.easykan.server.domain.project.model.ProjectUserViewId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProjectUserViewRepository extends JpaRepository<ProjectUserView, ProjectUserViewId> {

	long countByUserId(UUID userId);

	@Query("""
			SELECT puv FROM ProjectUserView puv
			    JOIN FETCH puv.project p
			    JOIN FETCH p.owner
			    WHERE puv.id.userId = :userId
			    ORDER BY puv.position
			""")
	List<ProjectUserView> findAllByUserWithDetails(@Param("userId") UUID userId);

}

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

	@Query("""
				SELECT puv FROM ProjectUserView puv
					JOIN FETCH puv.project p
					JOIN FETCH p.owner
					LEFT JOIN FETCH p.members m
					LEFT JOIN FETCH m.user
					WHERE puv.id.userId = :userId
					ORDER BY puv.position
			""")
	List<ProjectUserView> findAllByUserWithDetails(@Param("userId") UUID userId);

	@Query("""
				SELECT COALESCE(MAX(puv.position + 1), 0)
					FROM ProjectUserView puv
					WHERE puv.user.id = :userId
			""")
	Integer findNextPositionByUserId(@Param("userId") UUID userId);

}

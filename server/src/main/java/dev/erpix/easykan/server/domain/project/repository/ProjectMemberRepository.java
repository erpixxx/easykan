package dev.erpix.easykan.server.domain.project.repository;

import dev.erpix.easykan.server.domain.project.model.ProjectMember;
import dev.erpix.easykan.server.domain.project.model.ProjectMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, ProjectMemberId> {

	@Query("""
				SELECT pm.permissions FROM ProjectMember pm
					WHERE pm.user.id = :userId AND pm.project.id = :projectId
			""")
	Optional<Long> findPermissionByUserIdAndProjectId(@Param("userId") UUID userId, @Param("projectId") UUID projectId);

}

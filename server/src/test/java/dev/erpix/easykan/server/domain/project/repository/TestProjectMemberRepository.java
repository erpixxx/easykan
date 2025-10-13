package dev.erpix.easykan.server.domain.project.repository;

import dev.erpix.easykan.server.domain.project.model.Project;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TestProjectMemberRepository extends ProjectMemberRepository {

	@Query("""
			    SELECT pm.project FROM ProjectMember pm
			    	WHERE pm.user.id = :userId
			""")
	List<Project> findProjectsByUserId(@Param("userId") UUID userId);

}

package dev.erpix.easykan.server.domain.project.repository;

import dev.erpix.easykan.server.domain.project.model.ProjectUserView;
import dev.erpix.easykan.server.domain.project.model.ProjectUserViewId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@SuppressWarnings("unused")
@Repository
public interface ProjectUserViewRepository extends JpaRepository<ProjectUserView, ProjectUserViewId> {

}

package dev.erpix.easykan.repository;

import dev.erpix.easykan.model.project.EKUserProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserProjectsRepository extends JpaRepository<EKUserProject, EKUserProject.Id> {

    List<EKUserProject> findByUserId(UUID userId);

    List<EKUserProject> findByProjectId(UUID projectId);

    List<EKUserProject> findByIdUserIdAndProject_Id(UUID idUserId, UUID projectId);
}

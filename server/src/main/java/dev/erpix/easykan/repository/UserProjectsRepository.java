package dev.erpix.easykan.repository;

import dev.erpix.easykan.model.project.EKUserProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface UserProjectsRepository extends JpaRepository<EKUserProject, EKUserProject.Id> {

    Set<EKUserProject> findById_UserId(UUID userId);

    Set<EKUserProject> findById_ProjectId(UUID projectId);

    Set<EKUserProject> findById_UserIdAndProject_Id(UUID userId, UUID projectId);

}

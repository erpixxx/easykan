package dev.erpix.easykan.server.domain.project.repository;

import dev.erpix.easykan.server.domain.project.model.EKProject;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<EKProject, UUID> {

    @NotNull Set<EKProject> findByOwner_Id(UUID ownerId);

}

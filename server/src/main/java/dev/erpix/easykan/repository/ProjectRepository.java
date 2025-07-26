package dev.erpix.easykan.repository;

import dev.erpix.easykan.model.project.EKProject;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<EKProject, UUID> {

    @NotNull List<EKProject> findByOwner_Id(UUID ownerId);

}

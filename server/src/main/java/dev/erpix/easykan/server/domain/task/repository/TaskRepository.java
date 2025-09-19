package dev.erpix.easykan.server.domain.task.repository;

import dev.erpix.easykan.server.domain.task.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@SuppressWarnings("unused")
@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {

}

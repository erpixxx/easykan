package dev.erpix.easykan.server.domain.task.repository;

import dev.erpix.easykan.server.domain.task.model.Task;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@SuppressWarnings("unused")
@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {

}

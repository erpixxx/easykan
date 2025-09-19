package dev.erpix.easykan.server.domain.column.repository;

import dev.erpix.easykan.server.domain.column.model.BoardColumn;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@SuppressWarnings("unused")
@Repository
public interface BoardColumnRepository extends JpaRepository<BoardColumn, UUID> {
}

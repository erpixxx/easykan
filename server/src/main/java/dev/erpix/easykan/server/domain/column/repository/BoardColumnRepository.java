package dev.erpix.easykan.server.domain.column.repository;

import dev.erpix.easykan.server.domain.column.model.BoardColumn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@SuppressWarnings("unused")
@Repository
public interface BoardColumnRepository extends JpaRepository<BoardColumn, UUID> {

}

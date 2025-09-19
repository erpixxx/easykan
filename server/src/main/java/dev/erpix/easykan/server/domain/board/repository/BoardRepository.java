package dev.erpix.easykan.server.domain.board.repository;

import dev.erpix.easykan.server.domain.board.model.Board;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@SuppressWarnings("unused")
@Repository
public interface BoardRepository extends JpaRepository<Board, UUID> {
}

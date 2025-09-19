package dev.erpix.easykan.server.domain.board.repository;

import dev.erpix.easykan.server.domain.board.model.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@SuppressWarnings("unused")
@Repository
public interface BoardRepository extends JpaRepository<Board, UUID> {

}

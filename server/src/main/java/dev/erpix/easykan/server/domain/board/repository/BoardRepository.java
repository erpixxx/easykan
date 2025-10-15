package dev.erpix.easykan.server.domain.board.repository;

import dev.erpix.easykan.server.domain.board.model.Board;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Primary
@Repository
public interface BoardRepository extends JpaRepository<Board, UUID> {

	@Query("""
				SELECT b FROM Board b
					WHERE b.project.id = :projectId
					ORDER BY b.position
			""")
	List<Board> findAllByProjectIdOrderByPositionAsc(UUID projectId);

	@Query("""
				SELECT b FROM Board b
					WHERE b.id = :boardId
					AND b.project.id = :projectId
			""")
	Optional<Board> findByIdAndProjectId(UUID boardId, UUID projectId);

	@Query("""
				SELECT COALESCE(MAX(b.position + 1), 0)
					FROM Board b
					WHERE b.project.id = :projectId
			""")
	Integer findNextPositionByProjectId(UUID projectId);

	@Modifying(clearAutomatically = true)
	@Query("""
				UPDATE Board b
					SET b.position = b.position - 1
					WHERE b.project.id = :projectId
					AND b.position > :position
			""")
	void decrementPositionsGreaterThan(@Param("position") Integer position, @Param("projectId") UUID projectId);

}

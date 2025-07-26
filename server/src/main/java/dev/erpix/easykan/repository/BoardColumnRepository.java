package dev.erpix.easykan.repository;

import dev.erpix.easykan.model.EKColumn;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardColumnRepository extends JpaRepository<EKColumn, Integer> {

    long countByBoardId(int boardId);

    @NotNull List<EKColumn> findAllByBoardId(int boardId);

}

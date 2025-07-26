package dev.erpix.easykan.repository;

import dev.erpix.easykan.model.EKCard;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ColumnCardRepository extends JpaRepository<EKCard, Integer> {

    long countByColumnId(int columnId);

    @NotNull List<EKCard> findAllByColumnId(int columnId);

    @NotNull List<EKCard> findAllByColumnIdOrderByPositionAsc(int columnId);

}

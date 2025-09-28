package dev.erpix.easykan.server.domain.card.repository;

import dev.erpix.easykan.server.domain.card.model.CardLabel;
import dev.erpix.easykan.server.domain.card.model.CardLabelId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@SuppressWarnings("unused")
@Repository
public interface CardLabelRepository extends JpaRepository<CardLabel, CardLabelId> {

}

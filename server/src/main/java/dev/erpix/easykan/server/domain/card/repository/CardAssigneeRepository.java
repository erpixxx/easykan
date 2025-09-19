package dev.erpix.easykan.server.domain.card.repository;

import dev.erpix.easykan.server.domain.card.model.CardAssignee;
import dev.erpix.easykan.server.domain.card.model.CardAssigneeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@SuppressWarnings("unused")
@Repository
public interface CardAssigneeRepository extends JpaRepository<CardAssignee, CardAssigneeId> {

}

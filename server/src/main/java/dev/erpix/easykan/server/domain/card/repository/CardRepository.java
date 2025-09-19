package dev.erpix.easykan.server.domain.card.repository;

import dev.erpix.easykan.server.domain.card.model.Card;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@SuppressWarnings("unused")
@Repository
public interface CardRepository extends JpaRepository<Card, UUID> {
}

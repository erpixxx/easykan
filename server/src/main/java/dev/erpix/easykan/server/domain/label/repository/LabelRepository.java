package dev.erpix.easykan.server.domain.label.repository;

import dev.erpix.easykan.server.domain.label.model.Label;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@SuppressWarnings("unused")
@Repository
public interface LabelRepository extends JpaRepository<Label, UUID> {

}

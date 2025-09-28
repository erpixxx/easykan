package dev.erpix.easykan.server.domain.card.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class CardLabelId implements Serializable {

	@NotNull
	@Column(name = "card_id", nullable = false)
	private UUID cardId;

	@NotNull
	@Column(name = "label_id", nullable = false)
	private UUID labelId;

}

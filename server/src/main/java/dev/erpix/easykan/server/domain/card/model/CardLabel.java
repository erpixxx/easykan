package dev.erpix.easykan.server.domain.card.model;

import dev.erpix.easykan.server.domain.label.model.Label;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "card_labels", schema = "public")
public class CardLabel {

	@Builder.Default
	@EqualsAndHashCode.Include
	@ToString.Include
	@EmbeddedId
	private CardLabelId id = new CardLabelId();

	@MapsId("cardId")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "card_id", nullable = false)
	private Card card;

	@MapsId("labelId")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "label_id", nullable = false)
	private Label label;

}

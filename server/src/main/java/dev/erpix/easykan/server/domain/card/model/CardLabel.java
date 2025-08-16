package dev.erpix.easykan.server.domain.card.model;

import dev.erpix.easykan.server.domain.board.model.Label;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter @Setter
@Entity
@Table(name = "card_labels", schema = "public")
public class CardLabel {

    @EmbeddedId private CardLabelId id;

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
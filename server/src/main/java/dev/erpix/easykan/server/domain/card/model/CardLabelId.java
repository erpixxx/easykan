package dev.erpix.easykan.server.domain.card.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter @Setter
@Embeddable
public class CardLabelId implements Serializable {

    @Serial
    private static final long serialVersionUID = 3177568661581056011L;

    @NotNull
    @Column(name = "card_id", nullable = false)
    private UUID cardId;

    @NotNull
    @Column(name = "label_id", nullable = false)
    private UUID labelId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CardLabelId entity = (CardLabelId) o;
        return Objects.equals(this.labelId, entity.labelId) &&
                Objects.equals(this.cardId, entity.cardId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(labelId, cardId);
    }

}
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
public class CardAssigneeId implements Serializable {

    @Serial
    private static final long serialVersionUID = -8802345341397328354L;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @NotNull
    @Column(name = "card_id", nullable = false)
    private UUID cardId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CardAssigneeId entity = (CardAssigneeId) o;
        return Objects.equals(this.cardId, entity.cardId) &&
                Objects.equals(this.userId, entity.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardId, userId);
    }

}
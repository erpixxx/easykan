package dev.erpix.easykan.server.domain.column.model;

import dev.erpix.easykan.server.domain.card.model.Card;
import dev.erpix.easykan.server.domain.board.model.Board;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Getter @Setter
@Builder
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor @NoArgsConstructor
@Entity
@Table(name = "columns", schema = "public", indexes = {
        @Index(name = "columns_board_id_position_idx", columnList = "board_id, position", unique = true)
})
public class BoardColumn {

    @ToString.Include
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @ToString.Include
    @Size(max = 64)
    @NotNull
    @Column(name = "name", nullable = false, length = 64)
    private String name;

    @NotNull
    @Column(name = "position", nullable = false)
    private Integer position;

    @OneToMany(mappedBy = "column")
    private Set<Card> cards = new LinkedHashSet<>();

}
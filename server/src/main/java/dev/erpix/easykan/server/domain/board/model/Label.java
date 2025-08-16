package dev.erpix.easykan.server.domain.board.model;

import dev.erpix.easykan.server.domain.card.model.CardLabel;
import jakarta.persistence.*;
import jakarta.persistence.Column;
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
@Table(name = "labels", schema = "public", indexes = {
        @Index(name = "labels_board_id_idx", columnList = "board_id")
})
public class Label {

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
    @Size(max = 32)
    @NotNull
    @Column(name = "name", nullable = false, length = 32)
    private String name;

    @ToString.Include
    @Size(max = 6)
    @Column(name = "color", length = 6)
    private String color;

    @OneToMany(mappedBy = "label", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CardLabel> cardLinks = new LinkedHashSet<>();

}
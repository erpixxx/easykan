package dev.erpix.easykan.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@Entity
@Table(name = "easykan_columns")
public class EKColumn {

    @Id
    @Column(name = "column_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "column_name", nullable = false)
    private String name;

    @Column(name = "column_description")
    private String description;

    @Column(name = "column_position", nullable = false, columnDefinition = "TINYINT UNSIGNED")
    private int position;

    @Column(name = "column_created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "column_board_id", nullable = false)
    private EKBoard board;

    @OneToMany(
            mappedBy = "column",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<EKCard> cards;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

}

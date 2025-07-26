package dev.erpix.easykan.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@Entity
@Table(name = "easykan_cards")
public class EKCard {

    @Id
    @Column(name = "card_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "card_name", nullable = false)
    private String name;

    @Column(name = "card_description")
    private String description;

    @Column(name = "card_position", nullable = false, columnDefinition = "TINYINT UNSIGNED")
    private int position;

    @Column(name = "card_created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_column_id", nullable = false)
    private EKColumn column;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

}

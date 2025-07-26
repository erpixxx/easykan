package dev.erpix.easykan.model;

import dev.erpix.easykan.model.user.EKUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@Entity
@Table(name = "easykan_boards")
public class EKBoard {

    @Id
    @Column(name = "board_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "board_name", nullable = false, length = 64)
    private String name;

    @Column(name = "board_description")
    private String description;

    @Column(name = "board_created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_owner_uuid", nullable = false)
    private EKUser owner;

    @OneToMany(
            mappedBy = "board",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<EKColumn> columns;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

}

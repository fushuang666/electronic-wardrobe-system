package com.wardrobe.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "outfit_item", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"outfit_id", "clothing_id"})
})
@Data
public class OutfitItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "outfit_id", nullable = false)
    private Long outfitId;

    @Column(name = "clothing_id", nullable = false)
    private Long clothingId;

    private LocalDateTime createTime;

    @PrePersist
    public void prePersist() {
        if (this.createTime == null) {
            this.createTime = LocalDateTime.now();
        }
    }
}

package com.wardrobe.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "wardrobe")
@Data
public class Wardrobe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 200)
    private String description;

    /** PERSONAL / SHARED */
    @Column(length = 20)
    private String type;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    private LocalDateTime createTime;

    @PrePersist
    public void prePersist() {
        if (this.createTime == null) {
            this.createTime = LocalDateTime.now();
        }
        if (this.type == null) {
            this.type = "PERSONAL";
        }
    }
}

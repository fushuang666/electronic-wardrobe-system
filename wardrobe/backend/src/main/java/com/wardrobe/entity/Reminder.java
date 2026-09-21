package com.wardrobe.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reminder")
@Data
public class Reminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "wardrobe_id", nullable = false)
    private Long wardrobeId;

    /** IDLE / WEATHER / OUTFIT / IMBALANCE */
    @Column(nullable = false, length = 20)
    private String type;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 500)
    private String content;

    /** UNREAD / READ */
    @Column(length = 20)
    private String status;

    @Column(name = "related_id")
    private Long relatedId;

    private LocalDateTime createTime;

    @PrePersist
    public void prePersist() {
        if (this.createTime == null) this.createTime = LocalDateTime.now();
        if (this.status == null) this.status = "UNREAD";
    }
}

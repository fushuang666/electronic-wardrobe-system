package com.wardrobe.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "clothing")
@Data
public class Clothing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "wardrobe_id", nullable = false)
    private Long wardrobeId;

    @Column(nullable = false, length = 100)
    private String name;

    /** 品类：上衣/下装/鞋帽/配饰 ... */
    @Column(length = 30)
    private String category;

    /** 季节：春/夏/秋/冬/四季 */
    @Column(length = 20)
    private String season;

    @Column(length = 30)
    private String color;

    @Column(length = 30)
    private String style;

    /** 场合：日常/通勤/运动/正式 ... */
    @Column(length = 30)
    private String occasion;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(length = 50)
    private String brand;

    private java.math.BigDecimal price;

    @Column(name = "purchase_date")
    private LocalDateTime purchaseDate;

    @Column(name = "last_wear_date")
    private LocalDateTime lastWearDate;

    @Column(name = "wear_count")
    private Integer wearCount = 0;

    @Column(length = 500)
    private String note;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (this.createTime == null) this.createTime = now;
        if (this.updateTime == null) this.updateTime = now;
        if (this.wearCount == null) this.wearCount = 0;
    }

    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }
}

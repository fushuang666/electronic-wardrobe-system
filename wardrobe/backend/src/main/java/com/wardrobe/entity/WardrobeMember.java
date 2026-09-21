package com.wardrobe.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "wardrobe_member", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"wardrobe_id", "user_id"})
})
@Data
public class WardrobeMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "wardrobe_id", nullable = false)
    private Long wardrobeId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    /** OWNER / ADMIN / MEMBER */
    @Column(length = 20)
    private String role;

    private LocalDateTime createTime;

    @PrePersist
    public void prePersist() {
        if (this.createTime == null) {
            this.createTime = LocalDateTime.now();
        }
        if (this.role == null) {
            this.role = "MEMBER";
        }
    }
}

package com.wardrobe.dto;

import com.wardrobe.entity.Wardrobe;
import lombok.Data;

@Data
public class WardrobeVO {

    private Wardrobe wardrobe;

    /** 当前用户在该衣柜中的角色：OWNER / ADMIN / MEMBER */
    private String role;

    private Long memberCount;
}

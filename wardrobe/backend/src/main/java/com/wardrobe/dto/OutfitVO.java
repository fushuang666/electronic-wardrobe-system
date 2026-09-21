package com.wardrobe.dto;

import com.wardrobe.entity.Clothing;
import com.wardrobe.entity.Outfit;
import lombok.Data;

import java.util.List;

@Data
public class OutfitVO {

    private Outfit outfit;

    private List<Clothing> items;
}

package com.wardrobe.controller;

import com.wardrobe.common.Result;
import com.wardrobe.entity.Clothing;
import com.wardrobe.service.ClothingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wardrobes")
public class ClothingController {

    @Autowired
    private ClothingService clothingService;

    @PostMapping("/{wid}/clothings")
    public Result<Clothing> create(@RequestAttribute("userId") Long userId, @PathVariable("wid") Long wid,
                                   @RequestBody Clothing clothing) {
        return Result.ok(clothingService.create(userId, wid, clothing));
    }

    @PutMapping("/{wid}/clothings/{id}")
    public Result<Clothing> update(@RequestAttribute("userId") Long userId, @PathVariable("wid") Long wid,
                                   @PathVariable Long id, @RequestBody Clothing clothing) {
        return Result.ok(clothingService.update(userId, wid, id, clothing));
    }

    @DeleteMapping("/{wid}/clothings/{id}")
    public Result<Void> delete(@RequestAttribute("userId") Long userId, @PathVariable("wid") Long wid,
                               @PathVariable Long id) {
        clothingService.delete(userId, wid, id);
        return Result.ok();
    }

    @GetMapping("/{wid}/clothings")
    public Result<Page<Clothing>> page(@RequestAttribute("userId") Long userId, @PathVariable("wid") Long wid,
                                       @RequestParam(required = false) String category,
                                       @RequestParam(required = false) String season,
                                       @RequestParam(required = false) String color,
                                       @RequestParam(required = false) String style,
                                       @RequestParam(required = false) String occasion,
                                       @RequestParam(required = false) String keyword,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "20") int size) {
        return Result.ok(clothingService.page(userId, wid, category, season, color, style, occasion, keyword, page, size));
    }

    @GetMapping("/{wid}/clothings/{id}")
    public Result<Clothing> get(@RequestAttribute("userId") Long userId, @PathVariable("wid") Long wid,
                                @PathVariable Long id) {
        return Result.ok(clothingService.get(userId, wid, id));
    }

    @PostMapping("/{wid}/clothings/{id}/wear")
    public Result<Clothing> wear(@RequestAttribute("userId") Long userId, @PathVariable("wid") Long wid,
                                 @PathVariable Long id) {
        return Result.ok(clothingService.wear(userId, wid, id));
    }
}

package com.wardrobe.controller;

import com.wardrobe.common.Result;
import com.wardrobe.dto.OutfitVO;
import com.wardrobe.entity.Outfit;
import com.wardrobe.service.OutfitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wardrobes")
public class OutfitController {

    @Autowired
    private OutfitService outfitService;

    @PostMapping("/{wid}/outfits")
    public Result<Outfit> create(@RequestAttribute("userId") Long userId, @PathVariable("wid") Long wid,
                                 @RequestBody Outfit outfit,
                                 @RequestParam(value = "clothingIds", required = false) List<Long> clothingIds) {
        return Result.ok(outfitService.create(userId, wid, outfit, clothingIds));
    }

    @PutMapping("/{wid}/outfits/{id}")
    public Result<Outfit> update(@RequestAttribute("userId") Long userId, @PathVariable("wid") Long wid,
                                 @PathVariable Long id, @RequestBody Outfit outfit,
                                 @RequestParam(value = "clothingIds", required = false) List<Long> clothingIds) {
        return Result.ok(outfitService.update(userId, wid, id, outfit, clothingIds));
    }

    @DeleteMapping("/{wid}/outfits/{id}")
    public Result<Void> delete(@RequestAttribute("userId") Long userId, @PathVariable("wid") Long wid,
                               @PathVariable Long id) {
        outfitService.delete(userId, wid, id);
        return Result.ok();
    }

    @GetMapping("/{wid}/outfits")
    public Result<Page<Outfit>> page(@RequestAttribute("userId") Long userId, @PathVariable("wid") Long wid,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "20") int size) {
        return Result.ok(outfitService.page(userId, wid, page, size));
    }

    @GetMapping("/{wid}/outfits/{id}")
    public Result<OutfitVO> detail(@RequestAttribute("userId") Long userId, @PathVariable("wid") Long wid,
                                   @PathVariable Long id) {
        return Result.ok(outfitService.detail(userId, wid, id));
    }

    @PostMapping("/{wid}/outfits/{id}/apply")
    public Result<Void> apply(@RequestAttribute("userId") Long userId, @PathVariable("wid") Long wid,
                              @PathVariable Long id) {
        outfitService.apply(userId, wid, id);
        return Result.ok();
    }
}

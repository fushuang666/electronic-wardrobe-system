package com.wardrobe.service;

import com.wardrobe.common.BusinessException;
import com.wardrobe.dto.WardrobeVO;
import com.wardrobe.entity.Wardrobe;
import com.wardrobe.entity.WardrobeMember;
import com.wardrobe.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class WardrobeService {

    @Autowired
    private WardrobeRepository wardrobeRepository;

    @Autowired
    private WardrobeMemberRepository memberRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClothingRepository clothingRepository;

    @Autowired
    private OutfitRepository outfitRepository;

    @Autowired
    private OutfitItemRepository outfitItemRepository;

    @Autowired
    private ReminderRepository reminderRepository;

    @Transactional
    public Wardrobe create(Long userId, String name, String description, String type) {
        if (name == null || name.trim().isEmpty()) {
            throw new BusinessException("衣柜名称不能为空");
        }
        Wardrobe wardrobe = new Wardrobe();
        wardrobe.setName(name);
        wardrobe.setDescription(description);
        wardrobe.setType(type == null ? "PERSONAL" : type);
        wardrobe.setOwnerId(userId);
        wardrobe = wardrobeRepository.save(wardrobe);

        WardrobeMember owner = new WardrobeMember();
        owner.setWardrobeId(wardrobe.getId());
        owner.setUserId(userId);
        owner.setRole("OWNER");
        memberRepository.save(owner);
        return wardrobe;
    }

    public List<WardrobeVO> listForUser(Long userId) {
        List<WardrobeVO> result = new ArrayList<>();
        // 自己创建的
        for (Wardrobe w : wardrobeRepository.findByOwnerId(userId)) {
            WardrobeVO vo = new WardrobeVO();
            vo.setWardrobe(w);
            vo.setRole("OWNER");
            vo.setMemberCount(memberRepository.findByWardrobeId(w.getId()).size() + 0L);
            result.add(vo);
        }
        // 作为成员加入的
        for (WardrobeMember m : memberRepository.findByUserId(userId)) {
            if ("OWNER".equals(m.getRole())) {
                continue; // 已在上面包含
            }
            Wardrobe w = wardrobeRepository.findById(m.getWardrobeId()).orElse(null);
            if (w == null) {
                continue;
            }
            WardrobeVO vo = new WardrobeVO();
            vo.setWardrobe(w);
            vo.setRole(m.getRole());
            vo.setMemberCount(memberRepository.findByWardrobeId(w.getId()).size() + 0L);
            result.add(vo);
        }
        return result;
    }

    public Wardrobe get(Long wardrobeId) {
        return wardrobeRepository.findById(wardrobeId)
                .orElseThrow(() -> new BusinessException("衣柜不存在"));
    }

    @Transactional
    public Wardrobe update(Long wardrobeId, Long operatorId, String name, String description) {
        assertRole(wardrobeId, operatorId, "OWNER", "ADMIN");
        Wardrobe w = get(wardrobeId);
        if (name != null) {
            w.setName(name);
        }
        if (description != null) {
            w.setDescription(description);
        }
        return wardrobeRepository.save(w);
    }

    @Transactional
    public void delete(Long wardrobeId, Long operatorId) {
        assertRole(wardrobeId, operatorId, "OWNER");
        // 级联清理：搭配明细 -> 搭配 -> 衣物 -> 提醒 -> 成员 -> 衣柜
        List<com.wardrobe.entity.Outfit> outfits = outfitRepository.findByWardrobeIdOrderByCreateTimeDesc(
                wardrobeId, org.springframework.data.domain.PageRequest.of(0, Integer.MAX_VALUE)).getContent();
        for (com.wardrobe.entity.Outfit o : outfits) {
            outfitItemRepository.deleteAllByOutfitId(o.getId());
        }
        outfitRepository.deleteByWardrobeId(wardrobeId);
        clothingRepository.deleteByWardrobeId(wardrobeId);
        reminderRepository.deleteByWardrobeId(wardrobeId);
        memberRepository.deleteAll(memberRepository.findByWardrobeId(wardrobeId));
        wardrobeRepository.deleteById(wardrobeId);
    }

    @Transactional
    public WardrobeMember addMember(Long wardrobeId, Long operatorId, String username, String role) {
        assertRole(wardrobeId, operatorId, "OWNER", "ADMIN");
        Long targetId = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("目标用户不存在")).getId();
        if (memberRepository.findByWardrobeIdAndUserId(wardrobeId, targetId).isPresent()) {
            throw new BusinessException("该用户已在衣柜中");
        }
        if (!Arrays.asList("ADMIN", "MEMBER").contains(role)) {
            role = "MEMBER";
        }
        WardrobeMember m = new WardrobeMember();
        m.setWardrobeId(wardrobeId);
        m.setUserId(targetId);
        m.setRole(role);
        return memberRepository.save(m);
    }

    @Transactional
    public void removeMember(Long wardrobeId, Long operatorId, Long targetUserId) {
        assertRole(wardrobeId, operatorId, "OWNER", "ADMIN");
        WardrobeMember m = memberRepository.findByWardrobeIdAndUserId(wardrobeId, targetUserId)
                .orElseThrow(() -> new BusinessException("成员不存在"));
        if ("OWNER".equals(m.getRole())) {
            throw new BusinessException("不能移除衣柜所有者");
        }
        memberRepository.delete(m);
    }

    public List<WardrobeMember> listMembers(Long wardrobeId) {
        return memberRepository.findByWardrobeId(wardrobeId);
    }

    /** 校验当前用户在衣柜中的角色是否满足要求，否则抛异常 */
    public void assertRole(Long wardrobeId, Long userId, String... allowed) {
        WardrobeMember m = memberRepository.findByWardrobeIdAndUserId(wardrobeId, userId)
                .orElseThrow(() -> new BusinessException(403, "无权限访问该衣柜"));
        boolean ok = false;
        for (String role : allowed) {
            if (role.equals(m.getRole())) {
                ok = true;
                break;
            }
        }
        if (!ok) {
            throw new BusinessException(403, "权限不足");
        }
    }
}

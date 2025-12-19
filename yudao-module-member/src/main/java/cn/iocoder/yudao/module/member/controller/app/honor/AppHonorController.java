package cn.iocoder.yudao.module.member.controller.app.honor;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.member.controller.app.honor.vo.*;
import cn.iocoder.yudao.module.member.dal.dataobject.honor.*;
import cn.iocoder.yudao.module.member.dal.dataobject.user.MemberUserDO;
import cn.iocoder.yudao.module.member.enums.honor.BadgeCategoryEnum;
import cn.iocoder.yudao.module.member.service.honor.HonorService;
import cn.iocoder.yudao.module.member.service.user.MemberUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

/**
 * 用户 App - 荣誉系统
 * 
 * 设计理念：确认型系统，只记录"发生过"，不评价"好不好"
 */
@Tag(name = "用户 App - 荣誉系统")
@RestController
@RequestMapping("/member/honor")
@Validated
public class AppHonorController {

    @Resource
    private HonorService honorService;
    @Resource
    private MemberUserService memberUserService;

    @GetMapping("/summary")
    @Operation(summary = "获取荣誉概览")
    public CommonResult<AppHonorSummaryRespVO> getHonorSummary() {
        Long userId = getLoginUserId();
        
        AppHonorSummaryRespVO summary = new AppHonorSummaryRespVO();
        
        // 1. 温暖问候语
        summary.setGreeting(generateGreeting());
        
        // 2. 加入天数
        int daysWithUs = honorService.getDaysWithUs(userId);
        summary.setDaysWithUs(daysWithUs);
        summary.setDaysWithUsText("今天是你陪伴孩子的第 " + daysWithUs + " 天");
        
        // 3. 已解锁勋章
        List<UserBadgeDO> userBadges = honorService.getUserBadges(userId);
        List<AppBadgeRespVO> badges = userBadges.stream()
                .map(ub -> convertToBadgeVO(ub, honorService.getBadgeByCode(ub.getBadgeCode())))
                .filter(b -> b != null)
                .collect(Collectors.toList());
        summary.setBadges(badges);
        summary.setBadgeCount(badges.size());
        
        // 4. 接力记忆
        List<RelayMemoryDO> giveMemories = honorService.getUserGiveRelayMemories(userId);
        summary.setHasRelayMemory(!giveMemories.isEmpty());
        summary.setRelayMemories(giveMemories.stream()
                .limit(3)
                .map(this::convertToRelayMemoryVO)
                .collect(Collectors.toList()));
        
        // 5. 未读感谢数量
        summary.setUnreadThankCount(honorService.getUnreadThankCount(userId));
        
        return success(summary);
    }

    @GetMapping("/badges")
    @Operation(summary = "获取用户所有勋章")
    public CommonResult<List<AppBadgeRespVO>> getUserBadges() {
        Long userId = getLoginUserId();
        
        List<UserBadgeDO> userBadges = honorService.getUserBadges(userId);
        List<AppBadgeRespVO> result = userBadges.stream()
                .map(ub -> convertToBadgeVO(ub, honorService.getBadgeByCode(ub.getBadgeCode())))
                .filter(b -> b != null)
                .collect(Collectors.toList());
        
        return success(result);
    }

    @PutMapping("/badge/display")
    @Operation(summary = "设置勋章展示状态")
    public CommonResult<Boolean> updateBadgeDisplayStatus(
            @RequestParam("badgeCode") String badgeCode,
            @RequestParam("displayed") Boolean displayed) {
        Long userId = getLoginUserId();
        honorService.updateBadgeDisplayStatus(userId, badgeCode, displayed);
        return success(true);
    }

    @GetMapping("/relay-memories")
    @Operation(summary = "获取接力记忆列表")
    public CommonResult<List<AppRelayMemoryRespVO>> getRelayMemories(
            @RequestParam(value = "type", defaultValue = "give") String type) {
        Long userId = getLoginUserId();
        
        List<RelayMemoryDO> memories;
        if ("receive".equals(type)) {
            memories = honorService.getUserReceiveRelayMemories(userId);
        } else {
            memories = honorService.getUserGiveRelayMemories(userId);
        }
        
        List<AppRelayMemoryRespVO> result = memories.stream()
                .map(this::convertToRelayMemoryVO)
                .collect(Collectors.toList());
        
        return success(result);
    }

    @GetMapping("/thank-messages")
    @Operation(summary = "获取感谢私信列表")
    public CommonResult<List<AppThankMessageRespVO>> getThankMessages() {
        Long userId = getLoginUserId();
        
        List<RelayThankMessageDO> messages = honorService.getReceivedThankMessages(userId);
        List<AppThankMessageRespVO> result = messages.stream()
                .map(this::convertToThankMessageVO)
                .collect(Collectors.toList());
        
        return success(result);
    }

    @GetMapping("/thank-messages/unread-count")
    @Operation(summary = "获取未读感谢数量")
    public CommonResult<Long> getUnreadThankCount() {
        Long userId = getLoginUserId();
        return success(honorService.getUnreadThankCount(userId));
    }

    @PostMapping("/thank-message/send")
    @Operation(summary = "发送感谢私信")
    public CommonResult<Boolean> sendThankMessage(@Valid @RequestBody AppSendThankReqVO reqVO) {
        Long userId = getLoginUserId();
        honorService.sendThankMessage(userId, reqVO.getToUserId(), reqVO.getRelayMemoryId(), 
                reqVO.getItemId(), reqVO.getMessage());
        return success(true);
    }

    @PutMapping("/thank-message/{id}/read")
    @Operation(summary = "标记感谢私信为已读")
    public CommonResult<Boolean> markThankMessageAsRead(@PathVariable("id") Long id) {
        Long userId = getLoginUserId();
        honorService.markThankMessageAsRead(id, userId);
        return success(true);
    }

    @PutMapping("/thank-messages/read-all")
    @Operation(summary = "标记所有感谢私信为已读")
    public CommonResult<Boolean> markAllThankMessagesAsRead() {
        Long userId = getLoginUserId();
        honorService.markAllThankMessagesAsRead(userId);
        return success(true);
    }

    // ==================== 辅助方法 ====================

    /**
     * 生成温暖的问候语
     */
    private String generateGreeting() {
        int hour = LocalDateTime.now().getHour();
        if (hour < 6) {
            return "夜深了，辛苦了";
        } else if (hour < 12) {
            return "早上好，新的一天";
        } else if (hour < 18) {
            return "下午好，今天怎么样？";
        } else {
            return "晚上好，今天也辛苦了";
        }
    }

    /**
     * 格式化相对时间
     */
    private String formatRelativeTime(LocalDateTime time) {
        if (time == null) {
            return "";
        }
        
        LocalDateTime now = LocalDateTime.now();
        long days = ChronoUnit.DAYS.between(time, now);
        
        if (days == 0) {
            long hours = ChronoUnit.HOURS.between(time, now);
            if (hours == 0) {
                return "刚刚";
            }
            return hours + "小时前";
        } else if (days == 1) {
            return "昨天";
        } else if (days < 7) {
            return days + "天前";
        } else if (days < 30) {
            return (days / 7) + "周前";
        } else if (days < 365) {
            return (days / 30) + "个月前";
        } else {
            return (days / 365) + "年前";
        }
    }

    /**
     * 转换勋章VO
     */
    private AppBadgeRespVO convertToBadgeVO(UserBadgeDO userBadge, BadgeDefinitionDO definition) {
        if (definition == null) {
            return null;
        }
        
        AppBadgeRespVO vo = new AppBadgeRespVO();
        vo.setCode(definition.getCode());
        vo.setName(definition.getName());
        vo.setDescription(definition.getDescription());
        vo.setIcon(definition.getIcon());
        vo.setCategory(definition.getCategory());
        
        BadgeCategoryEnum categoryEnum = BadgeCategoryEnum.getByCode(definition.getCategory());
        vo.setCategoryName(categoryEnum != null ? categoryEnum.getName() : "");
        
        vo.setUnlocked(true);
        vo.setUnlockTime(userBadge.getFirstUnlockTime());
        vo.setUnlockTimeText(formatRelativeTime(userBadge.getFirstUnlockTime()));
        vo.setDisplayed(userBadge.getIsDisplayed());
        
        return vo;
    }

    /**
     * 转换接力记忆VO
     */
    private AppRelayMemoryRespVO convertToRelayMemoryVO(RelayMemoryDO memory) {
        AppRelayMemoryRespVO vo = new AppRelayMemoryRespVO();
        vo.setId(memory.getId());
        vo.setItemId(memory.getItemId());
        // TODO: 从RelayItem获取标题和图片
        vo.setItemTitle("接力物品");
        vo.setItemPhoto("");
        vo.setSequenceNum(memory.getSequenceNum());
        vo.setMomentMessage(memory.getMomentMessage());
        vo.setStatusText("有人接住了");
        vo.setCreateTime(memory.getCreateTime());
        vo.setTimeText(formatRelativeTime(memory.getCreateTime()));
        return vo;
    }

    /**
     * 转换感谢私信VO
     */
    private AppThankMessageRespVO convertToThankMessageVO(RelayThankMessageDO message) {
        AppThankMessageRespVO vo = new AppThankMessageRespVO();
        vo.setId(message.getId());
        
        // 获取发送者信息
        MemberUserDO fromUser = memberUserService.getUser(message.getFromUserId());
        if (fromUser != null) {
            vo.setFromUserNickname(fromUser.getNickname());
            vo.setFromUserAvatar(fromUser.getAvatar());
        } else {
            vo.setFromUserNickname("匿名家长");
            vo.setFromUserAvatar("");
        }
        
        // TODO: 从RelayItem获取标题
        vo.setItemTitle("接力物品");
        vo.setMessage(message.getMessage());
        vo.setIsRead(message.getIsRead());
        vo.setCreateTime(message.getCreateTime());
        vo.setTimeText(formatRelativeTime(message.getCreateTime()));
        
        return vo;
    }
}


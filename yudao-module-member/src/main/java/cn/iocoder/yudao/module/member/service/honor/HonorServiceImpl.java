package cn.iocoder.yudao.module.member.service.honor;

import cn.iocoder.yudao.module.member.dal.dataobject.honor.*;
import cn.iocoder.yudao.module.member.dal.dataobject.user.MemberUserDO;
import cn.iocoder.yudao.module.member.dal.mysql.honor.*;
import cn.iocoder.yudao.module.member.dal.mysql.user.MemberUserMapper;
import cn.iocoder.yudao.module.member.enums.honor.BehaviorEventTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 荣誉系统 Service 实现
 * 
 * 设计理念：确认型系统，只记录"发生过"，不评价"好不好"
 *
 * @author 星语家园
 */
@Slf4j
@Service
public class HonorServiceImpl implements HonorService {

    @Resource
    private BadgeDefinitionMapper badgeDefinitionMapper;
    @Resource
    private UserBadgeMapper userBadgeMapper;
    @Resource
    private BehaviorEventMapper behaviorEventMapper;
    @Resource
    private MemberStatsInternalMapper memberStatsInternalMapper;
    @Resource
    private RelayMemoryMapper relayMemoryMapper;
    @Resource
    private RelayThankMessageMapper relayThankMessageMapper;
    @Resource
    private MemberUserMapper memberUserMapper;

    // ==================== 勋章相关 ====================

    @Override
    public List<BadgeDefinitionDO> getAllBadgeDefinitions() {
        return badgeDefinitionMapper.selectEnabledList();
    }

    @Override
    public BadgeDefinitionDO getBadgeByCode(String code) {
        return badgeDefinitionMapper.selectByCode(code);
    }

    @Override
    public List<UserBadgeDO> getUserBadges(Long userId) {
        return userBadgeMapper.selectByUserId(userId);
    }

    @Override
    public List<BadgeDefinitionDO> getUserBadgesWithDetail(Long userId) {
        List<UserBadgeDO> userBadges = userBadgeMapper.selectByUserId(userId);
        if (userBadges.isEmpty()) {
            return new ArrayList<>();
        }
        
        return userBadges.stream()
                .map(ub -> badgeDefinitionMapper.selectByCode(ub.getBadgeCode()))
                .filter(bd -> bd != null)
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasUserBadge(Long userId, String badgeCode) {
        return userBadgeMapper.selectByUserIdAndBadgeCode(userId, badgeCode) != null;
    }

    @Override
    public void updateBadgeDisplayStatus(Long userId, String badgeCode, boolean displayed) {
        UserBadgeDO userBadge = userBadgeMapper.selectByUserIdAndBadgeCode(userId, badgeCode);
        if (userBadge != null) {
            userBadge.setIsDisplayed(displayed);
            userBadgeMapper.updateById(userBadge);
        }
    }

    // ==================== 行为事件相关 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BadgeDefinitionDO recordBehaviorEvent(Long userId, BehaviorEventTypeEnum eventType, 
                                                  Long refId, String refType, String eventData) {
        // 1. 记录行为事件
        BehaviorEventDO event = BehaviorEventDO.builder()
                .userId(userId)
                .eventType(eventType.getCode())
                .refId(refId)
                .refType(refType)
                .eventData(eventData)
                .build();
        behaviorEventMapper.insert(event);
        
        // 2. 更新后台统计
        updateUserStatsInternal(userId, eventType.getCode());
        
        // 3. 检查并授予勋章
        return checkAndGrantBadge(userId, eventType.getCode());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BadgeDefinitionDO recordBehaviorEvent(Long userId, BehaviorEventTypeEnum eventType) {
        return recordBehaviorEvent(userId, eventType, null, null, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BadgeDefinitionDO checkAndGrantBadge(Long userId, String eventType) {
        // 1. 查找该事件对应的勋章
        BehaviorEventTypeEnum eventEnum = BehaviorEventTypeEnum.getByCode(eventType);
        if (eventEnum == null || eventEnum.getBadgeCode() == null) {
            return null;
        }
        
        String badgeCode = eventEnum.getBadgeCode();
        
        // 2. 检查用户是否已有该勋章（只触发一次）
        if (hasUserBadge(userId, badgeCode)) {
            return null;
        }
        
        // 3. 特殊勋章需要额外条件检查
        if (!checkSpecialBadgeCondition(userId, eventType)) {
            return null;
        }
        
        // 4. 授予勋章
        UserBadgeDO userBadge = UserBadgeDO.builder()
                .userId(userId)
                .badgeCode(badgeCode)
                .firstUnlockTime(LocalDateTime.now())
                .isDisplayed(false)
                .build();
        userBadgeMapper.insert(userBadge);
        
        log.info("[checkAndGrantBadge][用户({})获得勋章({})]", userId, badgeCode);
        
        // 5. 返回勋章信息
        return badgeDefinitionMapper.selectByCode(badgeCode);
    }

    /**
     * 检查特殊勋章的额外条件
     */
    private boolean checkSpecialBadgeCondition(Long userId, String eventType) {
        switch (eventType) {
            case "training_resume_after_break":
                // 检查是否真的有中断过
                return checkTrainingBreakAndResume(userId);
            case "training_week_complete":
                // 检查是否完成了一周训练
                return checkWeekTrainingComplete(userId);
            case "app_visit_7_days":
                // 检查是否连续7天访问
                return checkConsecutiveVisits(userId, 7);
            default:
                return true;
        }
    }

    private boolean checkTrainingBreakAndResume(Long userId) {
        // 检查最近两次训练之间是否有超过3天的间隔
        List<BehaviorEventDO> events = behaviorEventMapper.selectRecentByUserId(userId, 30);
        List<BehaviorEventDO> trainingEvents = events.stream()
                .filter(e -> "training_complete".equals(e.getEventType()))
                .collect(Collectors.toList());
        
        if (trainingEvents.size() < 2) {
            return false;
        }
        
        for (int i = 0; i < trainingEvents.size() - 1; i++) {
            long daysBetween = ChronoUnit.DAYS.between(
                    trainingEvents.get(i + 1).getCreateTime(),
                    trainingEvents.get(i).getCreateTime());
            if (daysBetween >= 3) {
                return true;
            }
        }
        return false;
    }

    private boolean checkWeekTrainingComplete(Long userId) {
        // 检查最近7天是否每天都有训练
        List<BehaviorEventDO> events = behaviorEventMapper.selectRecentByUserId(userId, 7);
        long trainingDays = events.stream()
                .filter(e -> "training_complete".equals(e.getEventType()))
                .map(e -> e.getCreateTime().toLocalDate())
                .distinct()
                .count();
        return trainingDays >= 7;
    }

    private boolean checkConsecutiveVisits(Long userId, int days) {
        List<BehaviorEventDO> events = behaviorEventMapper.selectRecentByUserId(userId, days);
        long visitDays = events.stream()
                .filter(e -> "app_visit".equals(e.getEventType()))
                .map(e -> e.getCreateTime().toLocalDate())
                .distinct()
                .count();
        return visitDays >= days;
    }

    // ==================== 统计相关（后台用）====================

    @Override
    public MemberStatsInternalDO getUserStatsInternal(Long userId) {
        return memberStatsInternalMapper.selectByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserStatsInternal(Long userId, String eventType) {
        MemberStatsInternalDO stats = memberStatsInternalMapper.selectByUserId(userId);
        
        if (stats == null) {
            // 创建新记录
            stats = MemberStatsInternalDO.builder()
                    .userId(userId)
                    .trainingDays(0)
                    .postCount(0)
                    .commentCount(0)
                    .relayGiveCount(0)
                    .relayReceiveCount(0)
                    .daysWithUs(getDaysWithUs(userId))
                    .lastActiveTime(LocalDateTime.now())
                    .build();
            memberStatsInternalMapper.insert(stats);
        }
        
        // 更新统计
        switch (eventType) {
            case "training_complete":
            case "training_first_complete":
                stats.setTrainingDays(stats.getTrainingDays() + 1);
                break;
            case "post_first_publish":
                stats.setPostCount(stats.getPostCount() + 1);
                break;
            case "relay_item_give":
                stats.setRelayGiveCount(stats.getRelayGiveCount() + 1);
                break;
            case "relay_item_matched":
                stats.setRelayReceiveCount(stats.getRelayReceiveCount() + 1);
                break;
        }
        
        stats.setLastActiveTime(LocalDateTime.now());
        memberStatsInternalMapper.updateById(stats);
    }

    @Override
    public int getDaysWithUs(Long userId) {
        MemberUserDO user = memberUserMapper.selectById(userId);
        if (user == null || user.getCreateTime() == null) {
            return 0;
        }
        return (int) ChronoUnit.DAYS.between(user.getCreateTime(), LocalDateTime.now()) + 1;
    }

    // ==================== 接力记忆相关 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RelayMemoryDO createRelayMemory(Long itemId, Long originalItemId, Long fromUserId, 
                                            Long toUserId, Integer sequenceNum, String momentMessage) {
        RelayMemoryDO memory = RelayMemoryDO.builder()
                .itemId(itemId)
                .originalItemId(originalItemId != null ? originalItemId : itemId)
                .fromUserId(fromUserId)
                .toUserId(toUserId)
                .sequenceNum(sequenceNum != null ? sequenceNum : 1)
                .momentMessage(momentMessage)
                .build();
        relayMemoryMapper.insert(memory);
        
        // 记录行为事件
        recordBehaviorEvent(fromUserId, BehaviorEventTypeEnum.RELAY_ITEM_MATCHED, itemId, "relay_item", null);
        
        return memory;
    }

    @Override
    public List<RelayMemoryDO> getUserGiveRelayMemories(Long userId) {
        return relayMemoryMapper.selectByFromUserId(userId);
    }

    @Override
    public List<RelayMemoryDO> getUserReceiveRelayMemories(Long userId) {
        return relayMemoryMapper.selectByToUserId(userId);
    }

    @Override
    public List<RelayMemoryDO> getItemRelayChain(Long itemId) {
        return relayMemoryMapper.selectByItemId(itemId);
    }

    // ==================== 感谢私信相关 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RelayThankMessageDO sendThankMessage(Long fromUserId, Long toUserId, 
                                                 Long relayMemoryId, Long itemId, String message) {
        RelayThankMessageDO thankMessage = RelayThankMessageDO.builder()
                .fromUserId(fromUserId)
                .toUserId(toUserId)
                .relayMemoryId(relayMemoryId)
                .itemId(itemId)
                .message(message)
                .isRead(false)
                .build();
        relayThankMessageMapper.insert(thankMessage);
        
        // 记录行为事件（给接收感谢的人）
        recordBehaviorEvent(toUserId, BehaviorEventTypeEnum.THANK_RECEIVED, thankMessage.getId(), "thank_message", null);
        
        return thankMessage;
    }

    @Override
    public List<RelayThankMessageDO> getReceivedThankMessages(Long userId) {
        return relayThankMessageMapper.selectByToUserId(userId);
    }

    @Override
    public Long getUnreadThankCount(Long userId) {
        return relayThankMessageMapper.selectUnreadCountByToUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markThankMessageAsRead(Long messageId, Long userId) {
        RelayThankMessageDO message = relayThankMessageMapper.selectById(messageId);
        if (message != null && message.getToUserId().equals(userId)) {
            message.setIsRead(true);
            relayThankMessageMapper.updateById(message);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAllThankMessagesAsRead(Long userId) {
        List<RelayThankMessageDO> messages = relayThankMessageMapper.selectByToUserId(userId);
        for (RelayThankMessageDO message : messages) {
            if (!message.getIsRead()) {
                message.setIsRead(true);
                relayThankMessageMapper.updateById(message);
            }
        }
    }
}


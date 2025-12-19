package cn.iocoder.yudao.module.member.service.honor;

import cn.iocoder.yudao.module.member.dal.dataobject.honor.*;
import cn.iocoder.yudao.module.member.enums.honor.BehaviorEventTypeEnum;

import java.util.List;

/**
 * 荣誉系统 Service 接口
 * 
 * 设计理念：确认型系统，只记录"发生过"，不评价"好不好"
 *
 * @author 星语家园
 */
public interface HonorService {

    // ==================== 勋章相关 ====================

    /**
     * 获取所有勋章定义
     */
    List<BadgeDefinitionDO> getAllBadgeDefinitions();

    /**
     * 根据编码获取勋章定义
     */
    BadgeDefinitionDO getBadgeByCode(String code);

    /**
     * 获取用户已解锁的勋章
     */
    List<UserBadgeDO> getUserBadges(Long userId);

    /**
     * 获取用户已解锁的勋章（带勋章详情）
     */
    List<BadgeDefinitionDO> getUserBadgesWithDetail(Long userId);

    /**
     * 检查用户是否拥有某勋章
     */
    boolean hasUserBadge(Long userId, String badgeCode);

    /**
     * 设置勋章展示状态
     */
    void updateBadgeDisplayStatus(Long userId, String badgeCode, boolean displayed);

    // ==================== 行为事件相关 ====================

    /**
     * 记录行为事件（核心方法）
     * 会自动触发勋章检查
     * 
     * @return 如果触发了新勋章，返回勋章信息；否则返回null
     */
    BadgeDefinitionDO recordBehaviorEvent(Long userId, BehaviorEventTypeEnum eventType, Long refId, String refType, String eventData);

    /**
     * 记录行为事件（简化版）
     */
    BadgeDefinitionDO recordBehaviorEvent(Long userId, BehaviorEventTypeEnum eventType);

    /**
     * 检查并授予勋章
     * 
     * @return 如果授予了新勋章，返回勋章信息；否则返回null
     */
    BadgeDefinitionDO checkAndGrantBadge(Long userId, String eventType);

    // ==================== 统计相关（后台用）====================

    /**
     * 获取用户内部统计
     */
    MemberStatsInternalDO getUserStatsInternal(Long userId);

    /**
     * 更新用户内部统计
     */
    void updateUserStatsInternal(Long userId, String eventType);

    /**
     * 获取用户加入天数
     */
    int getDaysWithUs(Long userId);

    // ==================== 接力记忆相关 ====================

    /**
     * 创建接力记忆
     */
    RelayMemoryDO createRelayMemory(Long itemId, Long originalItemId, Long fromUserId, Long toUserId, Integer sequenceNum, String momentMessage);

    /**
     * 获取用户传递出去的接力记忆
     */
    List<RelayMemoryDO> getUserGiveRelayMemories(Long userId);

    /**
     * 获取用户接收的接力记忆
     */
    List<RelayMemoryDO> getUserReceiveRelayMemories(Long userId);

    /**
     * 获取物品的接力链
     */
    List<RelayMemoryDO> getItemRelayChain(Long itemId);

    // ==================== 感谢私信相关 ====================

    /**
     * 发送感谢私信
     */
    RelayThankMessageDO sendThankMessage(Long fromUserId, Long toUserId, Long relayMemoryId, Long itemId, String message);

    /**
     * 获取用户收到的感谢私信
     */
    List<RelayThankMessageDO> getReceivedThankMessages(Long userId);

    /**
     * 获取用户未读感谢数量
     */
    Long getUnreadThankCount(Long userId);

    /**
     * 标记感谢私信为已读
     */
    void markThankMessageAsRead(Long messageId, Long userId);

    /**
     * 标记所有感谢私信为已读
     */
    void markAllThankMessagesAsRead(Long userId);
}


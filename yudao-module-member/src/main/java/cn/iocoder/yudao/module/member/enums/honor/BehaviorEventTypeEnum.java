package cn.iocoder.yudao.module.member.enums.honor;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 行为事件类型枚举
 * 
 * 设计理念：只记录"发生过"，不评价"好不好"
 *
 * @author 星语家园
 */
@Getter
@AllArgsConstructor
public enum BehaviorEventTypeEnum {

    // ========== 训练相关 ==========
    TRAINING_FIRST_COMPLETE("training_first_complete", "首次完成训练", "first_step"),
    TRAINING_COMPLETE("training_complete", "完成训练", "today_with_you"),
    TRAINING_RESUME_AFTER_BREAK("training_resume_after_break", "中断后恢复训练", "slow_is_ok"),
    TRAINING_WEEK_COMPLETE("training_week_complete", "完成一周训练", "week_together"),

    // ========== 帖子相关 ==========
    POST_FIRST_PUBLISH("post_first_publish", "发布第一篇帖子", "left_a_mark"),
    POST_FIRST_COMMENT_RECEIVED("post_first_comment_received", "帖子收到第一条评论", "someone_sees_you"),
    POST_FIRST_COLLECTED("post_first_collected", "帖子被收藏", "someone_kept_it"),

    // ========== 接力相关 ==========
    RELAY_ITEM_GIVE("relay_item_give", "发布接力物品", null),
    RELAY_ITEM_MATCHED("relay_item_matched", "物品被领取", "someone_caught_it"),
    RELAY_CHAIN_EXTENDED("relay_chain_extended", "物品被再次传递", "warmth_flows"),
    THANK_RECEIVED("thank_received", "收到感谢", "been_thanked"),

    // ========== 小组相关 ==========
    GROUP_FIRST_JOIN("group_first_join", "加入第一个小组", "not_alone"),

    // ========== 其他 ==========
    APP_VISIT("app_visit", "访问App", null),
    APP_VISIT_7_DAYS("app_visit_7_days", "连续7天访问", "quiet_companion"),
    FIRST_SHARE("first_share", "第一次分享", "first_share");

    /**
     * 事件类型编码
     */
    private final String code;

    /**
     * 事件描述
     */
    private final String description;

    /**
     * 关联的勋章编码（可为null，表示不触发勋章）
     */
    private final String badgeCode;

    public static BehaviorEventTypeEnum getByCode(String code) {
        for (BehaviorEventTypeEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}


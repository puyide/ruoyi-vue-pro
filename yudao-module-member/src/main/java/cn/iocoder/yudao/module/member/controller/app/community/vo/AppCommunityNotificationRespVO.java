package cn.iocoder.yudao.module.member.controller.app.community.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户 APP - 社区通知 Response VO
 */
@Schema(description = "用户 APP - 社区通知 Response VO")
@Data
public class AppCommunityNotificationRespVO {

    @Schema(description = "通知ID", example = "123")
    private String nid;

    @Schema(description = "通知类型：reply-回复, like-点赞, follow-关注, mention-@提及", example = "reply")
    private String type;

    @Schema(description = "通知正文", example = "星星妈妈回复了你的帖子")
    private String bodyShort;

    @Schema(description = "相关帖子标题", example = "孩子叫名字不看我，我是这样一步步改善的")
    private String topicTitle;

    @Schema(description = "相关帖子ID", example = "123")
    private Integer tid;

    @Schema(description = "相关楼层ID", example = "456")
    private Integer pid;

    @Schema(description = "发送者信息")
    private SenderInfo sender;

    @Schema(description = "是否已读", example = "false")
    private Boolean read;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "相对时间", example = "5分钟前")
    private String timeAgo;

    /**
     * 发送者信息
     */
    @Data
    public static class SenderInfo {
        @Schema(description = "用户ID", example = "1")
        private Long uid;

        @Schema(description = "昵称", example = "星星妈妈")
        private String nickname;

        @Schema(description = "头像URL", example = "https://xxx/avatar.jpg")
        private String avatar;
    }
}


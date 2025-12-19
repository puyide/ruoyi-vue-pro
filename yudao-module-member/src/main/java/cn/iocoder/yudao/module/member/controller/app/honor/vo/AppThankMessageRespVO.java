package cn.iocoder.yudao.module.member.controller.app.honor.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户 App - 感谢私信 Response VO
 */
@Schema(description = "用户 App - 感谢私信 Response VO")
@Data
public class AppThankMessageRespVO {

    @Schema(description = "消息ID", example = "1")
    private Long id;

    @Schema(description = "发送者昵称", example = "阳阳爸爸")
    private String fromUserNickname;

    @Schema(description = "发送者头像")
    private String fromUserAvatar;

    @Schema(description = "物品标题", example = "语言图片卡（全套）")
    private String itemTitle;

    @Schema(description = "感谢内容", example = "这套卡片帮助我家宝贝从无语言到能说10个词了！")
    private String message;

    @Schema(description = "是否已读", example = "false")
    private Boolean isRead;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "时间文案", example = "2小时前")
    private String timeText;
}


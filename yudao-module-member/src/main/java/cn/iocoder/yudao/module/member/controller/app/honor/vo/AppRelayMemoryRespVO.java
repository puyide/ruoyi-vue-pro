package cn.iocoder.yudao.module.member.controller.app.honor.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户 App - 接力记忆 Response VO
 */
@Schema(description = "用户 App - 接力记忆 Response VO")
@Data
public class AppRelayMemoryRespVO {

    @Schema(description = "记忆ID", example = "1")
    private Long id;

    @Schema(description = "物品ID", example = "100")
    private Long itemId;

    @Schema(description = "物品标题", example = "语言图片卡（全套）")
    private String itemTitle;

    @Schema(description = "物品图片")
    private String itemPhoto;

    @Schema(description = "接力序号", example = "1")
    private Integer sequenceNum;

    @Schema(description = "那一刻的心情")
    private String momentMessage;

    @Schema(description = "状态文案", example = "有人接住了")
    private String statusText;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "时间文案", example = "3个月前")
    private String timeText;
}


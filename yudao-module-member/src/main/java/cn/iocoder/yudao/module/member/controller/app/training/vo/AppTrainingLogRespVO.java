package cn.iocoder.yudao.module.member.controller.app.training.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户 APP - 训练日志 Response VO
 */
@Schema(description = "用户 APP - 训练日志 Response VO")
@Data
public class AppTrainingLogRespVO {

    @Schema(description = "日志ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "会话ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long sessionId;

    @Schema(description = "孩子ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long childId;

    @Schema(description = "模板ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long templateId;

    @Schema(description = "训练名称", example = "回应名字")
    private String templateName;

    @Schema(description = "训练域", example = "social")
    private String domain;

    @Schema(description = "训练域名称", example = "社交与互动")
    private String domainName;

    @Schema(description = "打卡时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "2024-01-15 10:38:00")
    private LocalDateTime doneAt;

    @Schema(description = "是否完成", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    private Boolean completed;

    @Schema(description = "孩子情绪状态 1-还不错 2-一般 3-情绪不好", example = "1")
    private Integer childMood;

    @Schema(description = "孩子情绪名称", example = "还不错")
    private String childMoodName;

    @Schema(description = "孩子情绪表情", example = "😀")
    private String childMoodEmoji;

    @Schema(description = "成功次数", example = "5")
    private Integer successCount;

    @Schema(description = "遇到的困难标签")
    private List<String> difficulties;

    @Schema(description = "家长备注", example = "孩子今天状态不错")
    private String parentComment;

}


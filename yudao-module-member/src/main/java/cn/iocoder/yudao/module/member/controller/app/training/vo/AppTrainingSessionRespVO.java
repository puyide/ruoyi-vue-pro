package cn.iocoder.yudao.module.member.controller.app.training.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户 APP - 训练会话 Response VO
 */
@Schema(description = "用户 APP - 训练会话 Response VO")
@Data
public class AppTrainingSessionRespVO {

    @Schema(description = "会话ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "孩子ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long childId;

    @Schema(description = "模板ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long templateId;

    @Schema(description = "模板编码", example = "S01")
    private String templateCode;

    @Schema(description = "训练名称", example = "回应名字")
    private String templateName;

    @Schema(description = "训练域", example = "social")
    private String domain;

    @Schema(description = "训练域名称", example = "社交与互动")
    private String domainName;

    @Schema(description = "难度等级 1/2/3", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer difficultyLevel;

    @Schema(description = "难度等级名称", example = "L1 轻松玩一玩")
    private String difficultyLevelName;

    @Schema(description = "计划训练时间", example = "2024-01-15 10:30:00")
    private LocalDateTime scheduledAt;

    @Schema(description = "会话状态 0-已计划 1-进行中 2-已完成 3-已跳过", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    private Integer status;

    @Schema(description = "状态名称", example = "待练")
    private String statusName;

    @Schema(description = "预计时长（分钟）", example = "5")
    private Integer estimatedDurationMinutes;

    @Schema(description = "实际时长（分钟）", example = "8")
    private Integer actualDurationMinutes;

    @Schema(description = "开始时间", example = "2024-01-15 10:30:00")
    private LocalDateTime startedAt;

    @Schema(description = "完成时间", example = "2024-01-15 10:38:00")
    private LocalDateTime completedAt;

    @Schema(description = "创建时间", example = "2024-01-15 08:00:00")
    private LocalDateTime createTime;

}


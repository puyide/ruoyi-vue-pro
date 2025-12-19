package cn.iocoder.yudao.module.member.controller.app.training.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户 APP - 训练进度统计 Response VO
 */
@Schema(description = "用户 APP - 训练进度统计 Response VO")
@Data
public class AppTrainingProgressRespVO {

    // ========== 今日进度 ==========

    @Schema(description = "今日计划训练数", example = "5")
    private Integer todayTotal;

    @Schema(description = "今日已完成数", example = "3")
    private Integer todayCompleted;

    @Schema(description = "今日训练总时长（分钟）", example = "15")
    private Integer todayTotalMinutes;

    @Schema(description = "今日完成进度（百分比）", example = "60")
    private Integer todayProgressPercent;

    // ========== 累计统计 ==========

    @Schema(description = "总训练次数", example = "50")
    private Integer totalSessions;

    @Schema(description = "训练天数", example = "15")
    private Integer trainingDays;

    @Schema(description = "连续训练天数", example = "5")
    private Integer consecutiveDays;

    @Schema(description = "平均成功率（百分比）", example = "75")
    private Integer avgSuccessRatePercent;

    // ========== 鼓励文案 ==========

    @Schema(description = "鼓励文案", example = "陪孩子练了 15 分钟，已经很棒了 👍")
    private String encouragement;

}


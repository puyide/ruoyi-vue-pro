package cn.iocoder.yudao.module.member.controller.app.training.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 用户 APP - 训练会话详情 Response VO
 * 
 * 包含 AI 生成的训练卡片内容
 */
@Schema(description = "用户 APP - 训练会话详情 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AppTrainingSessionDetailRespVO extends AppTrainingSessionRespVO {

    @Schema(description = "训练卡片内容")
    private TrainingCardVO trainingCard;

    /**
     * AI 生成的训练卡片
     */
    @Schema(description = "训练卡片内容")
    @Data
    public static class TrainingCardVO {

        @Schema(description = "训练标题（面向家长）", example = "回应名字练习")
        private String title;

        @Schema(description = "目标解释", example = "这是很多社交互动的起点，让孩子知道名字=有人在找我")
        private String goalExplained;

        @Schema(description = "所需材料列表")
        private List<String> materials;

        @Schema(description = "训练步骤列表")
        private List<String> steps;

        @Schema(description = "家长台词建议")
        private List<String> parentScript;

        @Schema(description = "成功判定标准", example = "孩子在听到名字后有转头、停顿或看向家长的反应")
        private String successCriteria;

        @Schema(description = "强化方式建议", example = "立刻给予表扬和小零食")
        private String reinforcement;

        @Schema(description = "常见问题/注意事项")
        private List<String> commonPitfalls;

        @Schema(description = "预计时长（分钟）", example = "5")
        private Integer estimatedDurationMinutes;

        @Schema(description = "当前难度说明", example = "L1：大人提供大量提示，环境简化")
        private String levelHint;

    }

    @Schema(description = "模板简要目标", example = "当家长叫名字时，孩子能有转头、停顿或看向家长的反应。")
    private String briefGoal;

    @Schema(description = "推荐频率", example = "每天 5-10 次，穿插在日常生活中")
    private String recommendedFrequency;

}


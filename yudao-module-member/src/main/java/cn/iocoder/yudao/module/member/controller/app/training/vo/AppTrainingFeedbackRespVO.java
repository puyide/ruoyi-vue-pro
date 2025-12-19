package cn.iocoder.yudao.module.member.controller.app.training.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户 APP - 训练反馈 Response VO
 * 
 * AI 生成的训练反馈
 */
@Schema(description = "用户 APP - 训练反馈 Response VO")
@Data
public class AppTrainingFeedbackRespVO {

    @Schema(description = "肯定语（永远有正向）", 
            example = "你已经为孩子练了 3 次「回应名字」，这对将来的社交非常重要。")
    private String affirmation;

    @Schema(description = "客观小总结", 
            example = "从最近 3 次记录看，孩子回应次数在慢慢增加（2 → 3 → 4 次）。")
    private String summary;

    @Schema(description = "下一步建议", 
            example = "下次可以尝试把距离拉远一点，或者换一个房间练习。")
    private String nextSuggestion;

    @Schema(description = "建议的难度等级", example = "2")
    private Integer suggestedLevel;

    @Schema(description = "建议等级说明", example = "可以尝试 L2 部分独立型训练")
    private String suggestedLevelHint;

}


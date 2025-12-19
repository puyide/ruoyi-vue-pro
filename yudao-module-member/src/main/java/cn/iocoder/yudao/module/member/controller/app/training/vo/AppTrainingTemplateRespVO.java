package cn.iocoder.yudao.module.member.controller.app.training.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户 APP - 训练模板 Response VO
 */
@Schema(description = "用户 APP - 训练模板 Response VO")
@Data
public class AppTrainingTemplateRespVO {

    @Schema(description = "模板ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "模板编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "S01")
    private String code;

    @Schema(description = "训练域", requiredMode = Schema.RequiredMode.REQUIRED, example = "social")
    private String domain;

    @Schema(description = "训练域名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "社交与互动")
    private String domainName;

    @Schema(description = "训练名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "回应名字")
    private String name;

    @Schema(description = "简要目标", requiredMode = Schema.RequiredMode.REQUIRED, 
            example = "当家长叫名字时，孩子能有转头、停顿或看向家长的反应。")
    private String briefGoal;

    @Schema(description = "推荐频率", example = "每天 5-10 次，穿插在日常生活中")
    private String recommendedFrequency;

    @Schema(description = "基础场景", example = "在家中日常活动场景中...")
    private String baseScenario;

    @Schema(description = "排序", example = "1")
    private Integer sort;

}


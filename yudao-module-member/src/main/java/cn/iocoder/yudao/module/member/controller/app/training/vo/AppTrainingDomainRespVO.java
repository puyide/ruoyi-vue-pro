package cn.iocoder.yudao.module.member.controller.app.training.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户 APP - 训练域 Response VO
 */
@Schema(description = "用户 APP - 训练域 Response VO")
@Data
public class AppTrainingDomainRespVO {

    @Schema(description = "域编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "social")
    private String domain;

    @Schema(description = "域名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "社交与互动")
    private String domainName;

    @Schema(description = "模板数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    private Long count;

    @Schema(description = "域图标", example = "icon-social")
    private String icon;

    @Schema(description = "域描述", example = "训练孩子的社交互动能力")
    private String description;

}


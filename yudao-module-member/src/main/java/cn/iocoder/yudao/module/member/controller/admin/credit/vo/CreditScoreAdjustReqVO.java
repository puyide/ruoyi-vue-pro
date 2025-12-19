package cn.iocoder.yudao.module.member.controller.admin.credit.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 信用分调整 Request VO")
@Data
public class CreditScoreAdjustReqVO {

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @Schema(description = "变动类型（10=管理员调整）", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    @NotNull(message = "变动类型不能为空")
    private Integer changeType;

    @Schema(description = "变动分数（正数增加，负数扣减）", requiredMode = Schema.RequiredMode.REQUIRED, example = "5")
    @NotNull(message = "变动分数不能为空")
    private Integer changeScore;

    @Schema(description = "调整原因", requiredMode = Schema.RequiredMode.REQUIRED, example = "管理员手动调整")
    @NotNull(message = "调整原因不能为空")
    private String reason;

}


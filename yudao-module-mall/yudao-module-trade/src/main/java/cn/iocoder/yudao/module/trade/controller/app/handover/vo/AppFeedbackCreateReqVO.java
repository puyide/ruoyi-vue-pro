package cn.iocoder.yudao.module.trade.controller.app.handover.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Schema(description = "用户 APP - 创建反馈/评价 Request VO")
@Data
public class AppFeedbackCreateReqVO {

    @Schema(description = "交接会话ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "交接会话ID不能为空")
    private Long handoverId;

    @Schema(description = "反馈类型：1-感谢 2-评价", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "反馈类型不能为空")
    private Integer feedbackType;

    @Schema(description = "评分（1-5星，评价时必填）", example = "5")
    @Min(value = 1, message = "评分最低1星")
    @Max(value = 5, message = "评分最高5星")
    private Integer rating;

    @Schema(description = "反馈内容", example = "非常感谢您的帮助！")
    private String content;

    @Schema(description = "标签列表", example = "[\"准时\", \"友善\"]")
    private List<String> tags;

    @Schema(description = "是否匿名", example = "false")
    private Boolean isAnonymous;

}


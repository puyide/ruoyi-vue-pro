package cn.iocoder.yudao.module.member.controller.app.credit.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户 APP - 信用分变动记录响应 VO")
@Data
public class AppCreditLogRespVO {

    @Schema(description = "主键ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "变动类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer changeType;

    @Schema(description = "变动类型名称", example = "按时归还")
    private String changeTypeName;

    @Schema(description = "变动分数(正数为增加,负数为减少)", requiredMode = Schema.RequiredMode.REQUIRED, example = "5")
    private Integer changeScore;

    @Schema(description = "变动前分数", requiredMode = Schema.RequiredMode.REQUIRED, example = "95")
    private Integer beforeScore;

    @Schema(description = "变动后分数", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    private Integer afterScore;

    @Schema(description = "变动原因描述", example = "按时归还器材，信用良好")
    private String reason;

    @Schema(description = "关联订单ID", example = "10001")
    private Long relatedOrderId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}


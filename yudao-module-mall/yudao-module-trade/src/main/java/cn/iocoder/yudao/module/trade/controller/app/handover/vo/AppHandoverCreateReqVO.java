package cn.iocoder.yudao.module.trade.controller.app.handover.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户 APP - 创建交接会话 Request VO")
@Data
public class AppHandoverCreateReqVO {

    @Schema(description = "关联预约ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    @NotNull(message = "预约ID不能为空")
    private Long reservationId;

    @Schema(description = "交接方式：1-当面交接 2-快递配送 3-自提点 4-上门服务", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "交接方式不能为空")
    private Integer handoverMode;

    @Schema(description = "约定交接地址", example = "XX市XX区XX路XX号")
    private String handoverAddress;

    @Schema(description = "约定交接时间")
    private LocalDateTime handoverTime;

}


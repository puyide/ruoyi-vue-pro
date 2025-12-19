package cn.iocoder.yudao.module.trade.controller.admin.handover.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 发送消息 Request VO")
@Data
public class MessageSendReqVO {

    @Schema(description = "交接会话ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "交接会话ID不能为空")
    private Long handoverId;

    @Schema(description = "消息内容", requiredMode = Schema.RequiredMode.REQUIRED, example = "请双方尽快完成交接")
    @NotBlank(message = "消息内容不能为空")
    private String content;

}


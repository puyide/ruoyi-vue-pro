package cn.iocoder.yudao.module.trade.controller.app.handover.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Schema(description = "用户 APP - 发送交接消息 Request VO")
@Data
public class AppMessageSendReqVO {

    @Schema(description = "交接会话ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "交接会话ID不能为空")
    private Long handoverId;

    @Schema(description = "消息类型：2-用户消息 3-图片 4-位置 5-验收消息", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "消息类型不能为空")
    private Integer messageType;

    @Schema(description = "消息内容", example = "您好，请问什么时候方便交接？")
    private String content;

    @Schema(description = "图片/文件URL列表")
    private List<String> mediaUrls;

    @Schema(description = "位置信息（经度）", example = "116.404")
    private Double longitude;

    @Schema(description = "位置信息（纬度）", example = "39.915")
    private Double latitude;

    @Schema(description = "位置名称", example = "北京市天安门广场")
    private String locationName;

}


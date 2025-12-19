package cn.iocoder.yudao.module.member.controller.app.honor.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 用户 App - 发送感谢 Request VO
 */
@Schema(description = "用户 App - 发送感谢 Request VO")
@Data
public class AppSendThankReqVO {

    @Schema(description = "接收者用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "接收者不能为空")
    private Long toUserId;

    @Schema(description = "接力记忆ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    @NotNull(message = "接力记忆ID不能为空")
    private Long relayMemoryId;

    @Schema(description = "物品ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "200")
    @NotNull(message = "物品ID不能为空")
    private Long itemId;

    @Schema(description = "感谢内容", requiredMode = Schema.RequiredMode.REQUIRED, example = "这套卡片帮助我家宝贝从无语言到能说10个词了！")
    @NotBlank(message = "感谢内容不能为空")
    private String message;
}


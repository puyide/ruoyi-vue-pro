package cn.iocoder.yudao.module.member.controller.app.peergroup.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Schema(description = "小程序 - 加入同行小组 Request VO")
@Data
public class AppPeerGroupJoinReqVO {

    @Schema(description = "小组ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "小组ID不能为空")
    private Long groupId;

    @Schema(description = "加入理由（审核模式下使用）", example = "我的孩子3岁，正在进行语言训练...")
    private String reason;

}


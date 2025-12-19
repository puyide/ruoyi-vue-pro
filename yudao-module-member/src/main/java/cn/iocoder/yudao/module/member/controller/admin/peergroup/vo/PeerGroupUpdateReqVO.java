package cn.iocoder.yudao.module.member.controller.admin.peergroup.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import jakarta.validation.constraints.NotNull;

@Schema(description = "管理后台 - 同行小组更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PeerGroupUpdateReqVO extends PeerGroupBaseVO {

    @Schema(description = "小组编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "小组编号不能为空")
    private Long id;

}


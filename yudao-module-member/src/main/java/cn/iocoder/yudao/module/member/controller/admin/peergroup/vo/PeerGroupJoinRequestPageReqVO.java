package cn.iocoder.yudao.module.member.controller.admin.peergroup.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 同行小组加入申请分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PeerGroupJoinRequestPageReqVO extends PageParam {

    @Schema(description = "小组ID", example = "1")
    private Long groupId;

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "状态：0-待审核 1-已通过 2-已拒绝", example = "0")
    private Integer status;

}


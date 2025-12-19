package cn.iocoder.yudao.module.member.controller.admin.peergroup.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 同行小组成员分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PeerGroupMemberPageReqVO extends PageParam {

    @Schema(description = "小组ID", example = "1")
    private Long groupId;

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "角色", example = "member")
    private String role;

    @Schema(description = "状态", example = "0")
    private Integer status;

}


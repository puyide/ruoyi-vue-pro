package cn.iocoder.yudao.module.member.controller.admin.peergroup.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 同行小组成员 Response VO")
@Data
public class PeerGroupMemberRespVO {

    @Schema(description = "成员记录编号", example = "1")
    private Long id;

    @Schema(description = "小组ID", example = "1")
    private Long groupId;

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "用户昵称", example = "张三")
    private String nickname;

    @Schema(description = "用户头像")
    private String avatar;

    @Schema(description = "角色", example = "member")
    private String role;

    @Schema(description = "状态", example = "0")
    private Integer status;

    @Schema(description = "加入来源", example = "self")
    private String joinSource;

    @Schema(description = "加入时间")
    private LocalDateTime joinedAt;

    @Schema(description = "NodeBB成员同步状态")
    private Boolean nodebbSynced;

}


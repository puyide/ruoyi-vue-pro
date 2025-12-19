package cn.iocoder.yudao.module.member.controller.app.peergroup.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "小程序 - 同行小组成员 Response VO")
@Data
public class AppPeerGroupMemberRespVO {

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "用户昵称", example = "星星妈妈")
    private String nickname;

    @Schema(description = "用户头像")
    private String avatar;

    @Schema(description = "角色", example = "member")
    private String role;

    @Schema(description = "加入时间")
    private LocalDateTime joinedAt;

}


package cn.iocoder.yudao.module.member.controller.admin.peergroup.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 同行小组 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PeerGroupRespVO extends PeerGroupBaseVO {

    @Schema(description = "小组编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "当前成员数", example = "101")
    private Integer memberCount;

    @Schema(description = "二维码过期时间")
    private LocalDateTime wechatQrExpire;

    @Schema(description = "NodeBB Group ID")
    private Long nodebbGroupId;

    @Schema(description = "NodeBB 私密分类 ID")
    private Long nodebbCategoryId;

    @Schema(description = "NodeBB 同步状态：0-待同步 1-已同步 2-同步失败")
    private Integer nodebbSyncStatus;

    @Schema(description = "创建人用户ID")
    private Long creatorId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}


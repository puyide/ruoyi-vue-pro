package cn.iocoder.yudao.module.member.controller.admin.community.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 管理后台 - 社区帖子更新 Request VO
 */
@Schema(description = "管理后台 - 社区帖子更新 Request VO")
@Data
public class AdminCommunityTopicUpdateReqVO {

    @Schema(description = "帖子ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "123")
    @NotNull(message = "帖子ID不能为空")
    private Integer tid;

    @Schema(description = "是否置顶", example = "true")
    private Boolean pinned;

    @Schema(description = "是否锁定", example = "false")
    private Boolean locked;
}


package cn.iocoder.yudao.module.member.controller.admin.community.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 管理后台 - 社区内容审核 Request VO
 */
@Schema(description = "管理后台 - 社区内容审核 Request VO")
@Data
public class AdminCommunityAuditReqVO {

    @Schema(description = "内容ID（帖子ID或回帖ID）", requiredMode = Schema.RequiredMode.REQUIRED, example = "123")
    @NotNull(message = "内容ID不能为空")
    private Integer id;

    @Schema(description = "审核状态：1-通过, 2-拒绝", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "审核状态不能为空")
    private Integer auditStatus;

    @Schema(description = "审核备注", example = "内容涉及敏感信息")
    private String auditRemark;
}


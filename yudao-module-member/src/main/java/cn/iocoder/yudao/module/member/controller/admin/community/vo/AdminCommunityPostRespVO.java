package cn.iocoder.yudao.module.member.controller.admin.community.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理后台 - 社区回帖 Response VO
 */
@Schema(description = "管理后台 - 社区回帖 Response VO")
@Data
public class AdminCommunityPostRespVO {

    @Schema(description = "回帖ID", example = "456")
    private Integer pid;

    @Schema(description = "所属帖子ID", example = "123")
    private Integer tid;

    @Schema(description = "所属帖子标题", example = "孩子叫名字不看我...")
    private String topicTitle;

    @Schema(description = "楼层序号", example = "1")
    private Integer index;

    @Schema(description = "回复内容")
    private String content;

    @Schema(description = "作者信息")
    private AdminCommunityTopicRespVO.AuthorInfo author;

    @Schema(description = "点赞数量", example = "12")
    private Integer likeCount;

    @Schema(description = "审核状态：0-待审核, 1-已通过, 2-已拒绝", example = "0")
    private Integer auditStatus;

    @Schema(description = "审核备注", example = "内容合规")
    private String auditRemark;

    @Schema(description = "审核人", example = "admin")
    private String auditBy;

    @Schema(description = "审核时间")
    private LocalDateTime auditTime;

    @Schema(description = "是否删除", example = "false")
    private Boolean deleted;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}


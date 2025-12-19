package cn.iocoder.yudao.module.member.controller.admin.community.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
 * 管理后台 - 社区帖子分页 Request VO
 */
@Schema(description = "管理后台 - 社区帖子分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AdminCommunityTopicPageReqVO extends PageParam {

    @Schema(description = "帖子ID", example = "123")
    private Integer tid;

    @Schema(description = "标题关键词", example = "训练")
    private String title;

    @Schema(description = "分类ID", example = "1")
    private Integer cid;

    @Schema(description = "审核状态：0-待审核, 1-已通过, 2-已拒绝", example = "0")
    private Integer auditStatus;

    @Schema(description = "作者用户ID", example = "1")
    private Long userId;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;
}


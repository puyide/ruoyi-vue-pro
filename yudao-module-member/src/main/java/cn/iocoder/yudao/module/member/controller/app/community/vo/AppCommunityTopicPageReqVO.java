package cn.iocoder.yudao.module.member.controller.app.community.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户 APP - 社区帖子分页 Request VO
 */
@Schema(description = "用户 APP - 社区帖子分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AppCommunityTopicPageReqVO extends PageParam {

    @Schema(description = "排序方式：recent-最新, popular-热门, top-最高评价", example = "recent")
    private String sort = "recent";

    @Schema(description = "分类ID", example = "1")
    private Integer cid;

    @Schema(description = "标签名称", example = "语言训练")
    private String tag;

    @Schema(description = "搜索关键词", example = "呼名训练")
    private String keyword;
}


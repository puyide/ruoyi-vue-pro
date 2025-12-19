package cn.iocoder.yudao.module.member.controller.app.community.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 用户 APP - 社区分类 Response VO
 */
@Schema(description = "用户 APP - 社区分类 Response VO")
@Data
public class AppCommunityCategoryRespVO {

    @Schema(description = "分类ID", example = "1")
    private Integer cid;

    @Schema(description = "分类名称", example = "语言训练")
    private String name;

    @Schema(description = "分类描述", example = "关于语言训练的经验分享")
    private String description;

    @Schema(description = "分类图标", example = "💬")
    private String icon;

    @Schema(description = "分类颜色", example = "#52C41A")
    private String bgColor;

    @Schema(description = "帖子数量", example = "128")
    private Integer topicCount;

    @Schema(description = "子分类列表")
    private List<AppCommunityCategoryRespVO> children;

    @Schema(description = "排序号", example = "1")
    private Integer order;
}


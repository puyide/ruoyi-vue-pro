package cn.iocoder.yudao.module.member.controller.admin.community.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 管理后台 - 社区帖子 Response VO
 */
@Schema(description = "管理后台 - 社区帖子 Response VO")
@Data
public class AdminCommunityTopicRespVO {

    @Schema(description = "帖子ID", example = "123")
    private Integer tid;

    @Schema(description = "帖子标题", example = "孩子叫名字不看我...")
    private String title;

    @Schema(description = "帖子内容")
    private String content;

    @Schema(description = "分类ID", example = "1")
    private Integer cid;

    @Schema(description = "分类名称", example = "语言训练")
    private String categoryName;

    @Schema(description = "标签列表")
    private List<String> tags;

    @Schema(description = "作者信息")
    private AuthorInfo author;

    @Schema(description = "回帖数量", example = "56")
    private Integer postCount;

    @Schema(description = "浏览数量", example = "1024")
    private Integer viewCount;

    @Schema(description = "点赞数量", example = "28")
    private Integer likeCount;

    @Schema(description = "审核状态：0-待审核, 1-已通过, 2-已拒绝", example = "0")
    private Integer auditStatus;

    @Schema(description = "审核备注", example = "内容合规")
    private String auditRemark;

    @Schema(description = "审核人", example = "admin")
    private String auditBy;

    @Schema(description = "审核时间")
    private LocalDateTime auditTime;

    @Schema(description = "是否置顶", example = "false")
    private Boolean pinned;

    @Schema(description = "是否锁定", example = "false")
    private Boolean locked;

    @Schema(description = "是否删除", example = "false")
    private Boolean deleted;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 作者信息
     */
    @Data
    public static class AuthorInfo {
        @Schema(description = "用户ID", example = "1")
        private Long userId;

        @Schema(description = "NodeBB用户ID", example = "100")
        private Integer nodebbUid;

        @Schema(description = "昵称", example = "星星妈妈")
        private String nickname;

        @Schema(description = "头像URL")
        private String avatar;

        @Schema(description = "手机号", example = "13800138000")
        private String mobile;

        @Schema(description = "是否匿名", example = "true")
        private Boolean anonymous;
    }
}


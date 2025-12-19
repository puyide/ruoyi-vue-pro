package cn.iocoder.yudao.module.member.controller.app.community.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户 APP - 社区帖子 Response VO
 */
@Schema(description = "用户 APP - 社区帖子 Response VO")
@Data
public class AppCommunityTopicRespVO {

    @Schema(description = "帖子ID", example = "123")
    private Integer tid;

    @Schema(description = "帖子标题", example = "孩子叫名字不看我，我是这样一步步改善的")
    private String title;

    @Schema(description = "帖子内容（详情页返回完整内容，列表页可省略）")
    private String content;

    @Schema(description = "帖子摘要（用于列表展示）", example = "通过在孩子喜欢的活动中进行呼名训练...")
    private String excerpt;

    @Schema(description = "AI生成的摘要", example = "通过在孩子喜欢的活动中进行呼名训练，配合视觉提示和即时强化...")
    private String aiSummary;

    @Schema(description = "作者信息")
    private AuthorInfo author;

    @Schema(description = "分类信息")
    private CategoryInfo category;

    @Schema(description = "标签列表", example = "[\"语言训练\", \"3-6岁\"]")
    private List<String> tags;

    @Schema(description = "图片列表")
    private List<String> images;

    @Schema(description = "回帖数量", example = "56")
    private Integer postCount;

    @Schema(description = "浏览数量", example = "1024")
    private Integer viewCount;

    @Schema(description = "点赞数量", example = "28")
    private Integer likeCount;

    @Schema(description = "收藏数量", example = "15")
    private Integer bookmarkCount;

    @Schema(description = "\"我也遇到过\"数量", example = "28")
    private Integer relateCount;

    @Schema(description = "当前用户是否已点赞", example = "false")
    private Boolean liked;

    @Schema(description = "当前用户是否已收藏", example = "false")
    private Boolean bookmarked;

    @Schema(description = "当前用户是否已关注", example = "false")
    private Boolean following;

    @Schema(description = "是否置顶", example = "false")
    private Boolean pinned;

    @Schema(description = "是否锁定（禁止回复）", example = "false")
    private Boolean locked;

    @Schema(description = "发布时间")
    private LocalDateTime createTime;

    @Schema(description = "最后回复时间")
    private LocalDateTime lastReplyTime;

    @Schema(description = "相对时间", example = "2小时前")
    private String timeAgo;

    /**
     * 作者信息
     */
    @Data
    public static class AuthorInfo {
        @Schema(description = "用户ID", example = "1")
        private Long uid;

        @Schema(description = "用户名", example = "user123")
        private String username;

        @Schema(description = "昵称", example = "星星妈妈")
        private String nickname;

        @Schema(description = "头像URL", example = "https://xxx/avatar.jpg")
        private String avatar;

        @Schema(description = "是否匿名", example = "true")
        private Boolean anonymous;
    }

    /**
     * 分类信息
     */
    @Data
    public static class CategoryInfo {
        @Schema(description = "分类ID", example = "1")
        private Integer cid;

        @Schema(description = "分类名称", example = "语言训练")
        private String name;

        @Schema(description = "分类图标", example = "💬")
        private String icon;
    }
}


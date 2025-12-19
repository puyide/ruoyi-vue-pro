package cn.iocoder.yudao.module.member.controller.app.community.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户 APP - 社区楼层/回帖 Response VO
 */
@Schema(description = "用户 APP - 社区楼层/回帖 Response VO")
@Data
public class AppCommunityPostRespVO {

    @Schema(description = "楼层ID", example = "456")
    private Integer pid;

    @Schema(description = "所属帖子ID", example = "123")
    private Integer tid;

    @Schema(description = "楼层序号", example = "1")
    private Integer index;

    @Schema(description = "回复内容")
    private String content;

    @Schema(description = "作者信息")
    private AuthorInfo author;

    @Schema(description = "图片列表")
    private List<String> images;

    @Schema(description = "点赞数量", example = "12")
    private Integer likeCount;

    @Schema(description = "当前用户是否已点赞", example = "false")
    private Boolean liked;

    @Schema(description = "回复的楼层ID（如果是引用回复）", example = "123")
    private Integer toPid;

    @Schema(description = "被引用的回复信息")
    private QuotedPost quotedPost;

    @Schema(description = "发布时间")
    private LocalDateTime createTime;

    @Schema(description = "相对时间", example = "1小时前")
    private String timeAgo;

    @Schema(description = "是否是主楼（帖子正文）", example = "true")
    private Boolean isMainPost;

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
     * 被引用的回复
     */
    @Data
    public static class QuotedPost {
        @Schema(description = "楼层ID", example = "123")
        private Integer pid;

        @Schema(description = "作者昵称", example = "匿名家长")
        private String authorName;

        @Schema(description = "内容摘要", example = "感谢分享，非常有帮助...")
        private String contentExcerpt;
    }
}


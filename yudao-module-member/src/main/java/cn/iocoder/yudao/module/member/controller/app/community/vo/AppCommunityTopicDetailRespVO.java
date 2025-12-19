package cn.iocoder.yudao.module.member.controller.app.community.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 用户 APP - 社区帖子详情 Response VO
 */
@Schema(description = "用户 APP - 社区帖子详情 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AppCommunityTopicDetailRespVO extends AppCommunityTopicRespVO {

    @Schema(description = "AI分析")
    private AiAnalysis aiAnalysis;

    @Schema(description = "相关帖子推荐")
    private List<RelatedTopic> relatedTopics;

    /**
     * AI分析结果
     */
    @Data
    public static class AiAnalysis {
        @Schema(description = "关键步骤", example = "[\"在孩子喜欢的活动中叫名字建立正向关联\", \"叫名字后等待3-5秒给孩子反应时间\"]")
        private List<String> keySteps;

        @Schema(description = "注意事项", example = "[\"不要强迫孩子看，保持轻松氛围\", \"每天练习次数不宜过多\"]")
        private List<String> notes;

        @Schema(description = "适用年龄范围", example = "3-6岁，语言发展期儿童")
        private String ageRange;

        @Schema(description = "关联训练领域", example = "[\"语言沟通\", \"社交互动\"]")
        private List<String> relatedDomains;
    }

    /**
     * 相关帖子
     */
    @Data
    public static class RelatedTopic {
        @Schema(description = "帖子ID", example = "101")
        private Integer tid;

        @Schema(description = "帖子标题", example = "眼神接触训练的几个小技巧")
        private String title;

        @Schema(description = "标签列表", example = "[\"眼神训练\"]")
        private List<String> tags;

        @Schema(description = "浏览数量", example = "856")
        private Integer viewCount;
    }
}


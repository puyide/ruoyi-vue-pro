package cn.iocoder.yudao.module.member.controller.app.community.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 用户 APP - 创建社区帖子 Request VO
 */
@Schema(description = "用户 APP - 创建社区帖子 Request VO")
@Data
public class AppCommunityTopicCreateReqVO {

    @Schema(description = "帖子标题", requiredMode = Schema.RequiredMode.REQUIRED, example = "孩子叫名字不看我，我是这样一步步改善的")
    @NotBlank(message = "标题不能为空")
    @Size(min = 5, max = 100, message = "标题长度必须在5-100字符之间")
    private String title;

    @Schema(description = "帖子内容", requiredMode = Schema.RequiredMode.REQUIRED, example = "分享一下我们的训练方法...")
    @NotBlank(message = "内容不能为空")
    @Size(min = 20, max = 10000, message = "内容长度必须在20-10000字符之间")
    private String content;

    @Schema(description = "分类ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "分类不能为空")
    private Integer cid;

    @Schema(description = "标签列表", example = "[\"语言训练\", \"3-6岁\"]")
    private List<String> tags;

    @Schema(description = "图片URL列表", example = "[\"https://xxx/1.jpg\", \"https://xxx/2.jpg\"]")
    private List<String> images;

    @Schema(description = "是否匿名发布", example = "true")
    private Boolean anonymous = true;
}


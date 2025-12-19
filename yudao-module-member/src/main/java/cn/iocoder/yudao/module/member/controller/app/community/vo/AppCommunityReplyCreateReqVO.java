package cn.iocoder.yudao.module.member.controller.app.community.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 用户 APP - 创建帖子回复 Request VO
 */
@Schema(description = "用户 APP - 创建帖子回复 Request VO")
@Data
public class AppCommunityReplyCreateReqVO {

    @Schema(description = "回复内容", requiredMode = Schema.RequiredMode.REQUIRED, example = "感谢分享，非常有帮助！")
    @NotBlank(message = "回复内容不能为空")
    @Size(min = 1, max = 5000, message = "回复内容长度必须在1-5000字符之间")
    private String content;

    @Schema(description = "回复的楼层ID（引用回复）", example = "123")
    private Integer toPid;

    @Schema(description = "图片URL列表", example = "[\"https://xxx/1.jpg\"]")
    private List<String> images;
}


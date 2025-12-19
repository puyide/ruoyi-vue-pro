package cn.iocoder.yudao.module.trade.controller.app.handover.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "用户 APP - 反馈/评价响应 VO")
@Data
public class AppFeedbackRespVO {

    @Schema(description = "反馈ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "交接会话ID", example = "100")
    private Long handoverId;

    @Schema(description = "反馈类型：1-感谢 2-评价", example = "1")
    private Integer feedbackType;

    @Schema(description = "反馈类型名称", example = "感谢")
    private String feedbackTypeName;

    @Schema(description = "反馈者用户ID", example = "100")
    private Long fromUserId;

    @Schema(description = "反馈者昵称", example = "张三")
    private String fromUserNickname;

    @Schema(description = "反馈者头像", example = "http://xxx.jpg")
    private String fromUserAvatar;

    @Schema(description = "被反馈者用户ID", example = "200")
    private Long toUserId;

    @Schema(description = "被反馈者昵称", example = "李四")
    private String toUserNickname;

    @Schema(description = "评分", example = "5")
    private Integer rating;

    @Schema(description = "反馈内容", example = "非常感谢您的帮助！")
    private String content;

    @Schema(description = "标签列表")
    private List<String> tags;

    @Schema(description = "是否匿名", example = "false")
    private Boolean isAnonymous;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}


package cn.iocoder.yudao.module.member.controller.app.training.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 用户 APP - 训练日志创建（打卡） Request VO
 */
@Schema(description = "用户 APP - 训练日志创建（打卡） Request VO")
@Data
public class AppTrainingLogCreateReqVO {

    @Schema(description = "会话ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "会话ID不能为空")
    private Long sessionId;

    @Schema(description = "是否完成 true-大致完成 false-中途结束", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    @NotNull(message = "是否完成不能为空")
    private Boolean completed;

    @Schema(description = "孩子情绪状态 1-还不错 2-一般 3-情绪不好", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "孩子情绪状态不能为空")
    private Integer childMood;

    @Schema(description = "成功次数", example = "5")
    private Integer successCount;

    @Schema(description = "遇到的困难标签", example = "[\"环境太吵\", \"孩子不看我\"]")
    private List<String> difficulties;

    @Schema(description = "家长备注", example = "孩子今天状态不错")
    private String parentComment;

    @Schema(description = "实际训练时长（分钟）", example = "8")
    private Integer actualDurationMinutes;

    @Schema(description = "结构化数据（JSON）", example = "{\"max_wait_sec\": 8}")
    private Map<String, Object> structuredData;

    @Schema(description = "是否分享到社区", example = "false")
    private Boolean sharedToCommunity;

}


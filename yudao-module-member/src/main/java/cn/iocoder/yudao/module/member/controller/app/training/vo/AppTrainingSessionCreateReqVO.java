package cn.iocoder.yudao.module.member.controller.app.training.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
 * 用户 APP - 训练会话创建 Request VO
 */
@Schema(description = "用户 APP - 训练会话创建 Request VO")
@Data
public class AppTrainingSessionCreateReqVO {

    @Schema(description = "孩子ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "孩子ID不能为空")
    private Long childId;

    @Schema(description = "模板ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "模板ID不能为空")
    private Long templateId;

    @Schema(description = "难度等级 1/2/3", example = "1")
    private Integer difficultyLevel;

    @Schema(description = "计划训练时间", example = "2024-01-15 10:30:00")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime scheduledAt;

    @Schema(description = "环境信息（JSON对象）", example = "{\"available_rooms\": [\"客厅\"], \"available_materials\": [\"玩具车\"]}")
    private Map<String, Object> environment;

}


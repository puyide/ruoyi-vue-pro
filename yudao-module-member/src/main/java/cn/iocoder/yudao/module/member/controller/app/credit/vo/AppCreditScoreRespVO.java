package cn.iocoder.yudao.module.member.controller.app.credit.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户 APP - 信用分响应 VO")
@Data
public class AppCreditScoreRespVO {

    @Schema(description = "主键ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long userId;

    @Schema(description = "总信用分", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    private Integer totalScore;

    @Schema(description = "捐赠次数", example = "5")
    private Integer donateCount;

    @Schema(description = "借用次数", example = "3")
    private Integer borrowCount;

    @Schema(description = "按时归还次数", example = "3")
    private Integer returnOnTimeCount;

    @Schema(description = "逾期次数", example = "0")
    private Integer overdueCount;

    @Schema(description = "损坏次数", example = "0")
    private Integer damageCount;

    @Schema(description = "违规次数", example = "0")
    private Integer violationCount;

    @Schema(description = "信用等级", example = "优秀")
    private String creditLevel;

    @Schema(description = "最近一次分数变动原因", example = "按时归还器材")
    private String scoreChangeReason;

    @Schema(description = "最近一次分数变动时间")
    private LocalDateTime lastScoreChangeDate;

}


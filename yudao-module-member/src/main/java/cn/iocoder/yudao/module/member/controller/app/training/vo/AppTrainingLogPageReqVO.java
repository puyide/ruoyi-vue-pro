package cn.iocoder.yudao.module.member.controller.app.training.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户 APP - 训练日志分页 Request VO
 */
@Schema(description = "用户 APP - 训练日志分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AppTrainingLogPageReqVO extends PageParam {

    @Schema(description = "孩子ID", example = "1")
    private Long childId;

    @Schema(description = "训练域", example = "social")
    private String domain;

}


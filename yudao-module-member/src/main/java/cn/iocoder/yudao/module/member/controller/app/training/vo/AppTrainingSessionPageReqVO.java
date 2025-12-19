package cn.iocoder.yudao.module.member.controller.app.training.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户 APP - 训练会话分页 Request VO
 */
@Schema(description = "用户 APP - 训练会话分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AppTrainingSessionPageReqVO extends PageParam {

    @Schema(description = "孩子ID", example = "1")
    private Long childId;

    @Schema(description = "会话状态 0-已计划 1-进行中 2-已完成 3-已跳过", example = "0")
    private Integer status;

}


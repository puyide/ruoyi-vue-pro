package cn.iocoder.yudao.module.trade.controller.app.handover.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "用户 APP - 交接会话分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AppHandoverPageReqVO extends PageParam {

    @Schema(description = "交接状态", example = "1")
    private Integer status;

    @Schema(description = "角色类型：lender-出借方，borrower-借用方", example = "lender")
    private String roleType;

}


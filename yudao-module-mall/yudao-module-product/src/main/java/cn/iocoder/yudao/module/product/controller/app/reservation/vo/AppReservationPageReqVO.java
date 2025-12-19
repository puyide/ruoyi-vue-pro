package cn.iocoder.yudao.module.product.controller.app.reservation.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "用户 APP - 器材预约分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AppReservationPageReqVO extends PageParam {

    @Schema(description = "预约状态", example = "0")
    private Integer reservationStatus;

    @Schema(description = "查询类型：my-我的预约，owner-我收到的预约", example = "my")
    private String queryType;

}


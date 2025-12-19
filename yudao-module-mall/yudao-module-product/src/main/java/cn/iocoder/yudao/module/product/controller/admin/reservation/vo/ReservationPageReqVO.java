package cn.iocoder.yudao.module.product.controller.admin.reservation.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 器材预约分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ReservationPageReqVO extends PageParam {

    @Schema(description = "器材ID", example = "1")
    private Long equipmentId;

    @Schema(description = "预约用户ID", example = "1")
    private Long userId;

    @Schema(description = "器材所有者ID", example = "1")
    private Long ownerUserId;

    @Schema(description = "预约状态", example = "0")
    private Integer reservationStatus;

    @Schema(description = "押金状态", example = "0")
    private Integer depositStatus;

    @Schema(description = "预约日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] reservationDate;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}

package cn.iocoder.yudao.module.product.controller.app.reservation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Schema(description = "用户 APP - 器材预约创建 Request VO")
@Data
public class AppReservationCreateReqVO {

    @Schema(description = "器材ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "器材ID不能为空")
    private Long equipmentId;

    @Schema(description = "计划借用开始日期", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "计划借用开始日期不能为空")
    private LocalDate planBorrowDate;

    @Schema(description = "计划借用天数", requiredMode = Schema.RequiredMode.REQUIRED, example = "7")
    @NotNull(message = "计划借用天数不能为空")
    private Integer planBorrowDays;

    @Schema(description = "预约备注", example = "需要康复训练使用")
    private String remark;

}


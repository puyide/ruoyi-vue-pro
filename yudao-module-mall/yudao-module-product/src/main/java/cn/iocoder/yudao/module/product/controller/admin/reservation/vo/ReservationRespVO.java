package cn.iocoder.yudao.module.product.controller.admin.reservation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 器材预约响应 VO")
@Data
public class ReservationRespVO {

    @Schema(description = "预约ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "器材ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long equipmentId;

    @Schema(description = "器材名称", example = "轮椅")
    private String equipmentName;

    @Schema(description = "器材图片", example = "https://xxx.jpg")
    private String equipmentPicUrl;

    @Schema(description = "预约用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long userId;

    @Schema(description = "预约用户昵称", example = "张三")
    private String userNickname;

    @Schema(description = "器材所有者ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private Long ownerUserId;

    @Schema(description = "器材所有者昵称", example = "李四")
    private String ownerNickname;

    @Schema(description = "预约状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    private Integer reservationStatus;

    @Schema(description = "预约日期")
    private LocalDateTime reservationDate;

    @Schema(description = "计划借用日期")
    private LocalDate planBorrowDate;

    @Schema(description = "计划借用天数")
    private Integer planBorrowDays;

    @Schema(description = "实际借用时间")
    private LocalDateTime actualBorrowDate;

    @Schema(description = "实际归还时间")
    private LocalDateTime actualReturnDate;

    @Schema(description = "押金金额（分）")
    private Integer depositAmount;

    @Schema(description = "押金状态")
    private Integer depositStatus;

    @Schema(description = "预约备注")
    private String remark;

    @Schema(description = "取消原因")
    private String cancelReason;

    @Schema(description = "关联交接会话ID")
    private Long handoverId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}


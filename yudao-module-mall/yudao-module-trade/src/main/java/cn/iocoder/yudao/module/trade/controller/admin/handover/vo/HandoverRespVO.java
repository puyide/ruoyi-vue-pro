package cn.iocoder.yudao.module.trade.controller.admin.handover.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 交接会话响应 VO")
@Data
public class HandoverRespVO {

    @Schema(description = "交接会话ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "关联预约ID", example = "1")
    private Long reservationId;

    @Schema(description = "器材ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long equipmentId;

    @Schema(description = "器材名称", example = "轮椅")
    private String equipmentName;

    @Schema(description = "器材图片", example = "https://xxx.jpg")
    private String equipmentPicUrl;

    @Schema(description = "出借方用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long lenderUserId;

    @Schema(description = "出借方昵称", example = "张三")
    private String lenderNickname;

    @Schema(description = "借用方用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private Long borrowerUserId;

    @Schema(description = "借用方昵称", example = "李四")
    private String borrowerNickname;

    @Schema(description = "交接状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    private Integer handoverStatus;

    @Schema(description = "交接方式", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer handoverMode;

    @Schema(description = "交接地址")
    private String handoverAddress;

    @Schema(description = "约定交接时间")
    private LocalDateTime handoverTime;

    @Schema(description = "实际交接时间")
    private LocalDateTime actualHandoverTime;

    @Schema(description = "器材状态描述")
    private String equipmentCondition;

    @Schema(description = "器材照片")
    private List<String> equipmentPhotos;

    @Schema(description = "快递单号")
    private String expressNo;

    @Schema(description = "快递公司")
    private String expressCompany;

    @Schema(description = "快递状态")
    private Integer expressStatus;

    @Schema(description = "归还状态描述")
    private String returnCondition;

    @Schema(description = "归还照片")
    private List<String> returnPhotos;

    @Schema(description = "归还时间")
    private LocalDateTime returnTime;

    @Schema(description = "出借方验收结果")
    private Integer lenderVerifyResult;

    @Schema(description = "借用方验收结果")
    private Integer borrowerVerifyResult;

    @Schema(description = "最后消息时间")
    private LocalDateTime lastMessageTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}


package cn.iocoder.yudao.module.trade.controller.app.handover.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "用户 APP - 交接会话响应 VO")
@Data
public class AppHandoverRespVO {

    @Schema(description = "交接会话ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "关联预约ID", example = "100")
    private Long reservationId;

    @Schema(description = "器材ID", example = "1")
    private Long equipmentId;

    @Schema(description = "器材名称", example = "康复训练器")
    private String equipmentName;

    @Schema(description = "器材图片", example = "http://xxx.jpg")
    private String equipmentPicUrl;

    @Schema(description = "出借方用户ID", example = "100")
    private Long lenderUserId;

    @Schema(description = "出借方昵称", example = "张三")
    private String lenderNickname;

    @Schema(description = "出借方头像", example = "http://xxx.jpg")
    private String lenderAvatar;

    @Schema(description = "借用方用户ID", example = "200")
    private Long borrowerUserId;

    @Schema(description = "借用方昵称", example = "李四")
    private String borrowerNickname;

    @Schema(description = "借用方头像", example = "http://xxx.jpg")
    private String borrowerAvatar;

    @Schema(description = "交接状态", example = "1")
    private Integer handoverStatus;

    @Schema(description = "交接状态名称", example = "已确认")
    private String handoverStatusName;

    @Schema(description = "交接方式", example = "1")
    private Integer handoverMode;

    @Schema(description = "交接方式名称", example = "当面交接")
    private String handoverModeName;

    @Schema(description = "约定交接地址", example = "XX市XX区XX路XX号")
    private String handoverAddress;

    @Schema(description = "约定交接时间")
    private LocalDateTime handoverTime;

    @Schema(description = "实际交接时间")
    private LocalDateTime actualHandoverTime;

    @Schema(description = "器材状态描述（出借时）", example = "器材完好，无损坏")
    private String equipmentCondition;

    @Schema(description = "器材照片")
    private List<String> equipmentPhotos;

    @Schema(description = "快递单号", example = "SF1234567890")
    private String expressNo;

    @Schema(description = "快递公司", example = "顺丰速运")
    private String expressCompany;

    @Schema(description = "物流状态", example = "2")
    private Integer expressStatus;

    @Schema(description = "物流状态名称", example = "运输中")
    private String expressStatusName;

    @Schema(description = "归还时器材状态描述")
    private String returnCondition;

    @Schema(description = "归还时器材照片")
    private List<String> returnPhotos;

    @Schema(description = "归还时间")
    private LocalDateTime returnTime;

    @Schema(description = "出借方验收结果", example = "1")
    private Integer lenderVerifyResult;

    @Schema(description = "借用方验收结果", example = "1")
    private Integer borrowerVerifyResult;

    @Schema(description = "最后消息时间")
    private LocalDateTime lastMessageTime;

    @Schema(description = "未读消息数")
    private Integer unreadCount;

    @Schema(description = "最新消息内容预览")
    private String lastMessagePreview;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}


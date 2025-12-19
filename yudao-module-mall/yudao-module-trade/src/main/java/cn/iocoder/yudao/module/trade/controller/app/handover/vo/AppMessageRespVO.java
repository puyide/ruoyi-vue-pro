package cn.iocoder.yudao.module.trade.controller.app.handover.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "用户 APP - 交接消息响应 VO")
@Data
public class AppMessageRespVO {

    @Schema(description = "消息ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "交接会话ID", example = "100")
    private Long handoverId;

    @Schema(description = "发送者用户ID（0表示系统消息）", example = "100")
    private Long senderUserId;

    @Schema(description = "发送者昵称", example = "张三")
    private String senderNickname;

    @Schema(description = "发送者头像", example = "http://xxx.jpg")
    private String senderAvatar;

    @Schema(description = "是否是自己发送的", example = "true")
    private Boolean isSelf;

    @Schema(description = "消息类型：1-系统消息 2-用户消息 3-图片 4-位置 5-验收消息", example = "2")
    private Integer messageType;

    @Schema(description = "消息类型名称", example = "用户消息")
    private String messageTypeName;

    @Schema(description = "消息内容", example = "您好，请问什么时候方便交接？")
    private String content;

    @Schema(description = "图片/文件URL列表")
    private List<String> mediaUrls;

    @Schema(description = "位置信息（JSON）")
    private String locationInfo;

    @Schema(description = "是否已读", example = "true")
    private Boolean isRead;

    @Schema(description = "是否为敏感消息", example = "false")
    private Boolean isSensitive;

    @Schema(description = "发送时间")
    private LocalDateTime createTime;

}


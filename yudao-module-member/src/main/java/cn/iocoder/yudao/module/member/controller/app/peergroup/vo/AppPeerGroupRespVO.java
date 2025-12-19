package cn.iocoder.yudao.module.member.controller.app.peergroup.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "小程序 - 同行小组 Response VO")
@Data
public class AppPeerGroupRespVO {

    @Schema(description = "小组编号", example = "1")
    private Long id;

    @Schema(description = "小组名称", example = "3-6岁 · 语言发展")
    private String name;

    @Schema(description = "小组图标", example = "💬")
    private String icon;

    @Schema(description = "主题色", example = "orange")
    private String theme;

    @Schema(description = "小组封面图URL")
    private String coverUrl;

    @Schema(description = "小组简介")
    private String description;

    @Schema(description = "标签")
    private List<String> tags;

    @Schema(description = "当前成员数", example = "101")
    private Integer memberCount;

    @Schema(description = "最大成员数", example = "200")
    private Integer maxMembers;

    @Schema(description = "是否已加入")
    private Boolean joined;

    @Schema(description = "最后活动描述", example = "刚刚有新消息")
    private String lastActivity;

}


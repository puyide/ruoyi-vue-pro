package cn.iocoder.yudao.module.member.controller.admin.peergroup.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

/**
 * 同行小组 Base VO
 */
@Data
public class PeerGroupBaseVO {

    @Schema(description = "小组名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "3-6岁 · 语言发展")
    @NotBlank(message = "小组名称不能为空")
    private String name;

    @Schema(description = "小组图标（emoji或icon code）", example = "💬")
    private String icon;

    @Schema(description = "主题色", example = "orange")
    private String theme;

    @Schema(description = "小组封面图URL")
    private String coverUrl;

    @Schema(description = "小组简介")
    private String description;

    @Schema(description = "分类", example = "language")
    private String category;

    @Schema(description = "标签", example = "[\"语言\", \"3-6岁\"]")
    private List<String> tags;

    @Schema(description = "适用年龄组", example = "3-6")
    private String ageGroup;

    @Schema(description = "最大成员数", example = "200")
    private Integer maxMembers;

    @Schema(description = "微信群二维码图片URL")
    private String wechatQrUrl;

    @Schema(description = "企业微信客户群链接")
    private String wecomUrl;

    @Schema(description = "状态：0-开启 1-关闭", example = "0")
    private Integer status;

    @Schema(description = "加入模式：0-自由加入 1-需要审核 2-仅邀请", example = "0")
    private Integer joinMode;

    @Schema(description = "可见性：0-公开 1-仅成员可见", example = "0")
    private Integer visibility;

}


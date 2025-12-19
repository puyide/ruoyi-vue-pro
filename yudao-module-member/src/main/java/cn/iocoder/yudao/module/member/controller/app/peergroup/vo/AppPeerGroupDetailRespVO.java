package cn.iocoder.yudao.module.member.controller.app.peergroup.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "小程序 - 同行小组详情 Response VO")
@Data
public class AppPeerGroupDetailRespVO {

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

    @Schema(description = "分类", example = "language")
    private String category;

    @Schema(description = "标签")
    private List<String> tags;

    @Schema(description = "当前成员数", example = "101")
    private Integer memberCount;

    @Schema(description = "最大成员数", example = "200")
    private Integer maxMembers;

    // ========== 微信群相关 ==========

    @Schema(description = "微信群二维码图片URL")
    private String wechatQrUrl;

    @Schema(description = "二维码过期时间")
    private LocalDateTime wechatQrExpire;

    @Schema(description = "企业微信客户群链接")
    private String wecomUrl;

    // ========== NodeBB 相关 ==========

    @Schema(description = "NodeBB 私密分类 ID（用于跳转论坛）")
    private Long nodebbCategoryId;

    // ========== 当前用户相关 ==========

    @Schema(description = "是否已加入")
    private Boolean joined;

    @Schema(description = "当前用户的角色", example = "member")
    private String myRole;

    @Schema(description = "加入模式：0-自由加入 1-需要审核 2-仅邀请")
    private Integer joinMode;

}


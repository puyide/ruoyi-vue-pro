package cn.iocoder.yudao.module.member.controller.app.honor.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 用户 App - 荣誉概览 Response VO
 */
@Schema(description = "用户 App - 荣誉概览 Response VO")
@Data
public class AppHonorSummaryRespVO {

    @Schema(description = "温暖问候语", example = "晚上好，今天也辛苦了")
    private String greeting;

    @Schema(description = "加入天数", example = "23")
    private Integer daysWithUs;

    @Schema(description = "加入天数文案", example = "今天是你陪伴孩子的第 23 天")
    private String daysWithUsText;

    @Schema(description = "已解锁勋章列表")
    private List<AppBadgeRespVO> badges;

    @Schema(description = "勋章总数", example = "5")
    private Integer badgeCount;

    @Schema(description = "是否有传递过温暖", example = "true")
    private Boolean hasRelayMemory;

    @Schema(description = "接力记忆列表（最近3条）")
    private List<AppRelayMemoryRespVO> relayMemories;

    @Schema(description = "未读感谢数量", example = "2")
    private Long unreadThankCount;
}


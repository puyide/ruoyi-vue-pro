package cn.iocoder.yudao.module.member.controller.app.honor.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户 App - 勋章信息 Response VO
 */
@Schema(description = "用户 App - 勋章信息 Response VO")
@Data
public class AppBadgeRespVO {

    @Schema(description = "勋章编码", example = "first_step")
    private String code;

    @Schema(description = "勋章名称", example = "第一步")
    private String name;

    @Schema(description = "温暖文案", example = "迈出第一步，就已经很了不起")
    private String description;

    @Schema(description = "图标", example = "👣")
    private String icon;

    @Schema(description = "类别", example = "growth_memory")
    private String category;

    @Schema(description = "类别名称", example = "成长记忆")
    private String categoryName;

    @Schema(description = "是否已解锁", example = "true")
    private Boolean unlocked;

    @Schema(description = "解锁时间")
    private LocalDateTime unlockTime;

    @Schema(description = "解锁时间文案", example = "3个月前")
    private String unlockTimeText;

    @Schema(description = "是否展示在主页", example = "true")
    private Boolean displayed;
}


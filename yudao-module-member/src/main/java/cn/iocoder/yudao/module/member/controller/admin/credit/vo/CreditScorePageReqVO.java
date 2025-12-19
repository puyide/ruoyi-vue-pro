package cn.iocoder.yudao.module.member.controller.admin.credit.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 信用分分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CreditScorePageReqVO extends PageParam {

    @Schema(description = "用户ID", example = "1024")
    private Long userId;

    @Schema(description = "最低信用分", example = "60")
    private Integer minScore;

    @Schema(description = "最高信用分", example = "100")
    private Integer maxScore;

}


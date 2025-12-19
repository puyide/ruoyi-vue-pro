package cn.iocoder.yudao.module.member.controller.admin.credit.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
 * 信用分变动记录分页 Request VO
 *
 * @author 芋道源码
 */
@Schema(description = "管理后台 - 信用分变动记录分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CreditLogPageReqVO extends PageParam {

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "变动类型", example = "1")
    private Integer changeType;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}


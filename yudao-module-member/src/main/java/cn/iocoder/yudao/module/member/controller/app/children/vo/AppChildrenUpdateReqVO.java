package cn.iocoder.yudao.module.member.controller.app.children.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import jakarta.validation.constraints.NotNull;

/**
 * 用户 APP - 儿童个人信息更新 Request VO
 *
 * @author 芋道源码
 */
@Schema(description = "用户 APP - 儿童个人信息更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AppChildrenUpdateReqVO extends AppChildrenBaseVO {

    @Schema(description = "儿童信息ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "儿童信息ID不能为空")
    private Long id;

}


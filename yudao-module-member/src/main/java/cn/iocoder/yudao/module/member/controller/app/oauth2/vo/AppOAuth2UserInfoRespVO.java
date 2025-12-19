package cn.iocoder.yudao.module.member.controller.app.oauth2.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * OAuth2 用户信息响应 VO
 *
 * @author 芋道源码
 */
@Schema(description = "用户 APP - OAuth2 用户信息 Response VO")
@Data
public class AppOAuth2UserInfoRespVO {

    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED, example = "user123")
    private String username;

    @Schema(description = "用户昵称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋道")
    private String nickname;

    @Schema(description = "邮箱", example = "user@example.com")
    private String email;

    @Schema(description = "手机号", example = "13800138000")
    private String mobile;

    @Schema(description = "头像", example = "https://www.iocoder.cn/avatar.jpg")
    private String avatar;

    @Schema(description = "真实姓名", example = "张三")
    private String name;

}


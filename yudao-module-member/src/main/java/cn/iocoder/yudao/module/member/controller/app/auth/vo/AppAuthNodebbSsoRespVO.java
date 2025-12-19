package cn.iocoder.yudao.module.member.controller.app.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * NodeBB SSO 响应 VO
 *
 * @author 芋道源码
 */
@Schema(description = "用户 APP - NodeBB 单点登录 Response VO")
@Data
public class AppAuthNodebbSsoRespVO {

    @Schema(description = "SSO JWT Token", requiredMode = Schema.RequiredMode.REQUIRED, example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "NodeBB 服务地址", requiredMode = Schema.RequiredMode.REQUIRED, example = "http://localhost:4567")
    private String nodebbUrl;

    @Schema(description = "Cookie 名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "token")
    private String cookieName;

    @Schema(description = "Cookie Domain（可能为空）", example = ".example.com")
    private String cookieDomain;

    @Schema(description = "Cookie 有效期（秒）", requiredMode = Schema.RequiredMode.REQUIRED, example = "604800")
    private Integer cookieMaxAgeSeconds;

}

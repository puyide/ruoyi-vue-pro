package cn.iocoder.yudao.module.member.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * NodeBB 集成配置
 *
 * @author 芋道源码
 */
@Data
@Validated
@ConfigurationProperties(prefix = "yudao.member.nodebb")
public class NodeBBProperties {

    /**
     * 是否启用 NodeBB 集成
     */
    private Boolean enabled = false;

    /**
     * NodeBB 站点根地址，例如 http://localhost:4567
     */
    private String url;

    /**
     * NodeBB 写 API Master Token（用于创建用户）
     * 在 NodeBB 后台 Settings -> API Access 中生成
     */
    private String masterToken;

    /**
     * 使用 Master Token 调用时，需要指定管理员 uid（一般为 1）
     */
    private Integer adminUid = 1;

    /**
     * session-sharing 插件的共享密钥（HS256）
     * 必须与 NodeBB 插件配置的 secret 完全一致
     */
    private String ssoSecret;

    /**
     * session-sharing 插件期望的 cookie 名称
     * 必须与 NodeBB 插件配置的 name 一致，默认: token
     */
    private String ssoCookieName = "token";

    /**
     * cookie domain，例如 .example.com
     * 跨子域时设置，单域留空
     */
    private String ssoCookieDomain;

    /**
     * cookie 有效期（秒），默认 7 天
     */
    private Integer ssoCookieMaxAgeSeconds = 7 * 24 * 60 * 60;

    /**
     * 创建用户时的默认密码（NodeBB 要求至少 8 位）
     */
    private String defaultPassword = "DefaultPass123!";

}


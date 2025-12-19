package cn.iocoder.yudao.module.member.framework.nodebb.core.dto;

import lombok.Data;

/**
 * NodeBB 创建用户请求 DTO
 *
 * @author 芋道源码
 */
@Data
public class NodeBBCreateUserReqDTO {

    /**
     * 用户名（必填）
     */
    private String username;

    /**
     * 密码（必填，至少 8 位）
     */
    private String password;

    /**
     * 邮箱（可选）
     */
    private String email;

}


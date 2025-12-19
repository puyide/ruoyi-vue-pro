package cn.iocoder.yudao.module.member.framework.nodebb.core.dto;

import lombok.Data;

/**
 * NodeBB 登录请求 DTO
 *
 * @author 芋道源码
 */
@Data
public class NodeBBLoginReqDTO {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

}


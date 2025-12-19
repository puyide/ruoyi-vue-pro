package cn.iocoder.yudao.module.member.framework.nodebb.core.dto;

import lombok.Data;

/**
 * NodeBB 创建用户响应 DTO
 *
 * @author 芋道源码
 */
@Data
public class NodeBBCreateUserRespDTO {

    /**
     * NodeBB 用户 UID
     */
    private Integer uid;

    /**
     * NodeBB 用户名
     */
    private String username;

    /**
     * 邮箱
     */
    private String email;

}


package cn.iocoder.yudao.module.member.framework.nodebb.core.dto;

import lombok.Data;

/**
 * NodeBB 登录响应 DTO
 *
 * @author 芋道源码
 */
@Data
public class NodeBBLoginRespDTO {

    /**
     * NodeBB 用户 UID
     */
    private Integer uid;

    /**
     * NodeBB 用户名
     */
    private String username;

}


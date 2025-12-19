package cn.iocoder.yudao.module.member.dal.dataobject.user;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 会员与 NodeBB 账户映射
 */
@TableName("member_user_nodebb")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MemberUserNodebbDO extends TenantBaseDO {

    /**
     * 会员用户 ID（与 member_user.id 对应）
     */
    @TableId
    private Long userId;

    /**
     * NodeBB 用户 uid
     */
    private Integer nodebbUid;

    /**
     * NodeBB 用户名（最终落库的）
     */
    private String nodebbUsername;

}


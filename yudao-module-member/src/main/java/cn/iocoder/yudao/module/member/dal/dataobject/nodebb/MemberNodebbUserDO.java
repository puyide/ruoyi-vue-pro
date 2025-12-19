package cn.iocoder.yudao.module.member.dal.dataobject.nodebb;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 会员用户 - NodeBB 用户映射 DO
 *
 * @author 芋道源码
 */
@TableName(value = "member_nodebb_user", autoResultMap = true)
@KeySequence("member_nodebb_user_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberNodebbUserDO extends BaseDO {

    /**
     * 自增主键
     */
    @TableId
    private Long id;

    /**
     * 会员用户 ID
     *
     * 关联 member_user.id
     */
    private Long userId;

    /**
     * NodeBB 用户 UID
     */
    private Integer nodebbUid;

    /**
     * NodeBB 用户名
     */
    private String username;

    /**
     * 同步状态：1-成功，0-失败，null-未同步
     */
    private Integer syncStatus;

    /**
     * 同步失败原因
     */
    private String syncErrorMsg;

}

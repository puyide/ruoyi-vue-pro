package cn.iocoder.yudao.module.member.dal.dataobject.credit;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 会员信用分变动记录 DO
 *
 * @author 芋道源码
 */
@TableName(value = "member_credit_log", autoResultMap = true)
@KeySequence("member_credit_log_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberCreditLogDO extends BaseDO {

    /**
     * 主键ID
     */
    @TableId
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 变动类型
     * 
     * 枚举 {@link cn.iocoder.yudao.module.member.enums.credit.CreditChangeTypeEnum}
     */
    private Integer changeType;

    /**
     * 变动分数(正数为增加,负数为减少)
     */
    private Integer changeScore;

    /**
     * 变动前分数
     */
    private Integer beforeScore;

    /**
     * 变动后分数
     */
    private Integer afterScore;

    /**
     * 变动原因描述
     */
    private String reason;

    /**
     * 关联订单ID
     */
    private Long relatedOrderId;

}


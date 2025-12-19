package cn.iocoder.yudao.module.member.dal.dataobject.credit;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 会员信用分 DO
 *
 * @author 芋道源码
 */
@TableName(value = "member_credit_score", autoResultMap = true)
@KeySequence("member_credit_score_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberCreditScoreDO extends BaseDO {

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
     * 总信用分(默认100分)
     */
    private Integer totalScore;

    /**
     * 捐赠次数
     */
    private Integer donateCount;

    /**
     * 借用次数
     */
    private Integer borrowCount;

    /**
     * 按时归还次数
     */
    private Integer returnOnTimeCount;

    /**
     * 逾期次数
     */
    private Integer overdueCount;

    /**
     * 损坏次数
     */
    private Integer damageCount;

    /**
     * 违规次数
     */
    private Integer violationCount;

    /**
     * 最近一次分数变动原因
     */
    private String scoreChangeReason;

    /**
     * 最近一次分数变动时间
     */
    private LocalDateTime lastScoreChangeDate;

}


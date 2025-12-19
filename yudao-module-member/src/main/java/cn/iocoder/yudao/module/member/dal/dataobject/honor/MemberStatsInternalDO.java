package cn.iocoder.yudao.module.member.dal.dataobject.honor;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 后台统计 DO
 * 
 * 运营用，用户不可见
 *
 * @author 星语家园
 */
@TableName("member_stats_internal")
@KeySequence("member_stats_internal_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberStatsInternalDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 训练天数
     */
    private Integer trainingDays;

    /**
     * 帖子数
     */
    private Integer postCount;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 赠送物品数
     */
    private Integer relayGiveCount;

    /**
     * 接收物品数
     */
    private Integer relayReceiveCount;

    /**
     * 加入天数
     */
    private Integer daysWithUs;

    /**
     * 最后活跃时间
     */
    private LocalDateTime lastActiveTime;
}


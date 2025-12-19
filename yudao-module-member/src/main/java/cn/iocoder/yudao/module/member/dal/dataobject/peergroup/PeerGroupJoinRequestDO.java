package cn.iocoder.yudao.module.member.dal.dataobject.peergroup;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 同行小组加入申请 DO
 *
 * @author 芋道源码
 */
@TableName("peer_group_join_request")
@KeySequence("peer_group_join_request_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PeerGroupJoinRequestDO extends BaseDO {

    /**
     * 申请编号
     */
    @TableId
    private Long id;

    /**
     * 小组ID
     */
    private Long groupId;

    /**
     * 申请人用户ID
     */
    private Long userId;

    /**
     * 申请理由
     */
    private String reason;

    /**
     * 状态
     * 
     * 0-待审核 1-已通过 2-已拒绝 3-已过期
     */
    private Integer status;

    /**
     * 审核人用户ID
     */
    private Long reviewedBy;

    /**
     * 审核时间
     */
    private LocalDateTime reviewedAt;

    /**
     * 拒绝原因
     */
    private String rejectReason;

}


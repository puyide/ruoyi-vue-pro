package cn.iocoder.yudao.module.member.dal.dataobject.peergroup;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 同行小组成员 DO
 *
 * @author 芋道源码
 */
@TableName("peer_group_member")
@KeySequence("peer_group_member_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PeerGroupMemberDO extends BaseDO {

    /**
     * 成员记录编号
     */
    @TableId
    private Long id;

    /**
     * 小组ID
     */
    private Long groupId;

    /**
     * 会员用户ID
     */
    private Long userId;

    /**
     * 角色
     * 
     * 枚举：owner/admin/member
     * @see PeerGroupMemberRoleEnum
     */
    private String role;

    /**
     * 状态
     * 
     * 0-正常 1-静音 2-已退出 3-被移除 4-待审核
     * @see PeerGroupMemberStatusEnum
     */
    private Integer status;

    /**
     * 加入来源
     * 
     * 枚举：self/invite/admin/auto_match
     */
    private String joinSource;

    /**
     * 邀请人用户ID
     */
    private Long invitedBy;

    /**
     * 加入时间
     */
    private LocalDateTime joinedAt;

    /**
     * 退出时间
     */
    private LocalDateTime leftAt;

    // ========== NodeBB 同步 ==========

    /**
     * NodeBB成员同步状态
     */
    private Boolean nodebbSynced;

    /**
     * 最后同步时间
     */
    private LocalDateTime nodebbSyncedAt;

    // ========== 用户在小组内的设置 ==========

    /**
     * 是否接收小组通知
     */
    private Boolean notifyEnabled;

    /**
     * 最后阅读时间
     */
    private LocalDateTime lastReadAt;

}


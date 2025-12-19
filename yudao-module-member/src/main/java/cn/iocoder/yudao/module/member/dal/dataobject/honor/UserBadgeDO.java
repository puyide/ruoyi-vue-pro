package cn.iocoder.yudao.module.member.dal.dataobject.honor;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 用户勋章记录 DO
 * 
 * 只记录"发生过"，不统计次数
 *
 * @author 星语家园
 */
@TableName("member_user_badge")
@KeySequence("member_user_badge_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserBadgeDO extends BaseDO {

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
     * 勋章编码
     */
    private String badgeCode;

    /**
     * 第一次触发时间
     */
    private LocalDateTime firstUnlockTime;

    /**
     * 是否在主页展示
     */
    private Boolean isDisplayed;
}


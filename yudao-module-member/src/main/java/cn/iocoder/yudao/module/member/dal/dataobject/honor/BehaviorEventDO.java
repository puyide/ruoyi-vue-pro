package cn.iocoder.yudao.module.member.dal.dataobject.honor;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 行为事件 DO
 * 
 * 事件驱动，与勋章解耦
 *
 * @author 星语家园
 */
@TableName("member_behavior_event")
@KeySequence("member_behavior_event_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BehaviorEventDO extends BaseDO {

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
     * 事件类型
     * 
     * @see cn.iocoder.yudao.module.member.enums.honor.BehaviorEventTypeEnum
     */
    private String eventType;

    /**
     * 事件详情JSON
     */
    private String eventData;

    /**
     * 关联业务ID
     */
    private Long refId;

    /**
     * 关联业务类型
     */
    private String refType;
}


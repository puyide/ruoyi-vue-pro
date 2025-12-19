package cn.iocoder.yudao.module.trade.dal.dataobject.handover;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 器材交接反馈/评价 DO
 *
 * @author 芋道源码
 */
@TableName(value = "equipment_feedback", autoResultMap = true)
@KeySequence("equipment_feedback_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentFeedbackDO extends BaseDO {

    /**
     * 主键ID
     */
    @TableId
    private Long id;

    /**
     * 交接会话ID
     */
    private Long handoverId;

    /**
     * 反馈类型：1-感谢 2-评价
     */
    private Integer feedbackType;

    /**
     * 反馈者用户ID
     */
    private Long fromUserId;

    /**
     * 被反馈者用户ID
     */
    private Long toUserId;

    /**
     * 评分（1-5星）
     */
    private Integer rating;

    /**
     * 反馈内容
     */
    private String content;

    /**
     * 标签（JSON数组，如：["准时", "友善", "器材完好"]）
     */
    private String tags;

    /**
     * 是否匿名
     */
    private Boolean isAnonymous;

    /**
     * NodeBB帖子ID（如果同步到论坛）
     */
    private Long nodebbPostId;

}


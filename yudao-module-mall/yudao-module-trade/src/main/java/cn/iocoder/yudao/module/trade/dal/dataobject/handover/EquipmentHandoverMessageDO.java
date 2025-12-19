package cn.iocoder.yudao.module.trade.dal.dataobject.handover;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 交接会话消息 DO
 *
 * @author 芋道源码
 */
@TableName(value = "equipment_handover_message", autoResultMap = true)
@KeySequence("equipment_handover_message_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentHandoverMessageDO extends BaseDO {

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
     * 发送者用户ID（0表示系统消息）
     */
    private Long senderUserId;

    /**
     * 消息类型：1-系统消息 2-用户消息 3-图片 4-位置 5-验收消息
     */
    private Integer messageType;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 图片/文件URL（JSON数组）
     */
    private String mediaUrls;

    /**
     * 位置信息（JSON：经纬度、地址名称）
     */
    private String locationInfo;

    /**
     * 是否已读
     */
    private Boolean isRead;

    /**
     * 是否为敏感消息
     */
    private Boolean isSensitive;

}


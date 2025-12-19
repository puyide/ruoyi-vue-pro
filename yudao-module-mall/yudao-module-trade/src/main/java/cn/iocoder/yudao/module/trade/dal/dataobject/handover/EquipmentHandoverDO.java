package cn.iocoder.yudao.module.trade.dal.dataobject.handover;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 器材交接会话 DO
 *
 * @author 芋道源码
 */
@TableName(value = "equipment_handover", autoResultMap = true)
@KeySequence("equipment_handover_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentHandoverDO extends BaseDO {

    /**
     * 主键ID
     */
    @TableId
    private Long id;

    /**
     * 关联预约ID
     */
    private Long reservationId;

    /**
     * 器材ID
     */
    private Long equipmentId;

    /**
     * 出借方用户ID
     */
    private Long lenderUserId;

    /**
     * 借用方用户ID
     */
    private Long borrowerUserId;

    /**
     * 交接状态
     * 
     * 枚举 {@link cn.iocoder.yudao.module.trade.enums.handover.HandoverStatusEnum}
     */
    private Integer handoverStatus;

    /**
     * 交接方式
     * 
     * 枚举 {@link cn.iocoder.yudao.module.trade.enums.handover.HandoverModeEnum}
     */
    private Integer handoverMode;

    /**
     * 约定交接地址
     */
    private String handoverAddress;

    /**
     * 约定交接时间
     */
    private LocalDateTime handoverTime;

    /**
     * 实际交接时间
     */
    private LocalDateTime actualHandoverTime;

    /**
     * 器材状态描述（出借时）
     */
    private String equipmentCondition;

    /**
     * 器材照片（JSON数组）
     */
    private String equipmentPhotos;

    /**
     * 快递单号
     */
    private String expressNo;

    /**
     * 快递公司
     */
    private String expressCompany;

    /**
     * 物流状态
     * 
     * 枚举 {@link cn.iocoder.yudao.module.trade.enums.handover.ExpressStatusEnum}
     */
    private Integer expressStatus;

    /**
     * 归还时器材状态描述
     */
    private String returnCondition;

    /**
     * 归还时器材照片（JSON数组）
     */
    private String returnPhotos;

    /**
     * 归还时间
     */
    private LocalDateTime returnTime;

    /**
     * 出借方验收结果：0-待验收 1-通过 2-有异常
     */
    private Integer lenderVerifyResult;

    /**
     * 出借方验收备注
     */
    private String lenderVerifyRemark;

    /**
     * 借用方验收结果：0-待验收 1-通过 2-有异常
     */
    private Integer borrowerVerifyResult;

    /**
     * 借用方验收备注
     */
    private String borrowerVerifyRemark;

    /**
     * 最后消息时间
     */
    private LocalDateTime lastMessageTime;

    /**
     * 未读消息数（出借方）
     */
    private Integer lenderUnreadCount;

    /**
     * 未读消息数（借用方）
     */
    private Integer borrowerUnreadCount;

}


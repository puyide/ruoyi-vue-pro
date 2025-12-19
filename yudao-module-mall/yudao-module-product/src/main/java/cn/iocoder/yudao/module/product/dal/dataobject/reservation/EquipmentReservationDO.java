package cn.iocoder.yudao.module.product.dal.dataobject.reservation;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 器材预约 DO
 *
 * @author 芋道源码
 */
@TableName(value = "equipment_reservation", autoResultMap = true)
@KeySequence("equipment_reservation_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentReservationDO extends BaseDO {

    /**
     * 主键ID
     */
    @TableId
    private Long id;

    /**
     * 器材ID（关联product_spu.id）
     */
    private Long equipmentId;

    /**
     * 预约用户ID
     */
    private Long userId;

    /**
     * 器材所有者用户ID
     */
    private Long ownerUserId;

    /**
     * 预约状态：0-待确认 1-已确认 2-已取消 3-已完成
     */
    private Integer reservationStatus;

    /**
     * 预约日期
     */
    private LocalDateTime reservationDate;

    /**
     * 计划借用开始日期
     */
    private LocalDate planBorrowDate;

    /**
     * 计划借用天数
     */
    private Integer planBorrowDays;

    /**
     * 实际借用日期
     */
    private LocalDateTime actualBorrowDate;

    /**
     * 实际归还日期
     */
    private LocalDateTime actualReturnDate;

    /**
     * 押金金额（单位：分）
     */
    private Integer depositAmount;

    /**
     * 押金状态：0-待支付 1-已支付 2-已退还 3-已扣除
     */
    private Integer depositStatus;

    /**
     * 预约备注
     */
    private String remark;

    /**
     * 取消原因
     */
    private String cancelReason;

    /**
     * 关联交接会话ID
     */
    private Long handoverId;

}

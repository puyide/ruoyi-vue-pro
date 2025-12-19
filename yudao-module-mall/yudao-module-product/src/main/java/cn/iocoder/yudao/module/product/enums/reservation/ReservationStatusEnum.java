package cn.iocoder.yudao.module.product.enums.reservation;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 器材预约状态枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum ReservationStatusEnum {

    PENDING(0, "待确认"),
    CONFIRMED(1, "已确认"),
    CANCELLED(2, "已取消"),
    COMPLETED(3, "已完成");

    /**
     * 状态值
     */
    private final Integer status;

    /**
     * 状态名称
     */
    private final String name;

    public static ReservationStatusEnum valueOf(Integer status) {
        for (ReservationStatusEnum statusEnum : values()) {
            if (statusEnum.getStatus().equals(status)) {
                return statusEnum;
            }
        }
        return null;
    }

}

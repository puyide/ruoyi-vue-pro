package cn.iocoder.yudao.module.product.enums.reservation;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 押金状态枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum DepositStatusEnum {

    PENDING(0, "待支付"),
    PAID(1, "已支付"),
    REFUNDED(2, "已退还"),
    DEDUCTED(3, "已扣除");

    /**
     * 状态值
     */
    private final Integer status;

    /**
     * 状态名称
     */
    private final String name;

    public static DepositStatusEnum valueOf(Integer status) {
        for (DepositStatusEnum statusEnum : values()) {
            if (statusEnum.getStatus().equals(status)) {
                return statusEnum;
            }
        }
        return null;
    }

}


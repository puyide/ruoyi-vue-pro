package cn.iocoder.yudao.module.product.enums.relay;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 器材交易方式枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum RelayMethodEnum {

    GIVE(1, "give", "赠送"),
    BORROW(2, "borrow", "借用"),
    SELL(3, "sell", "低价转让");

    /**
     * 状态值
     */
    private final Integer status;

    /**
     * 代码（用于小程序）
     */
    private final String code;

    /**
     * 名称
     */
    private final String name;

    public static RelayMethodEnum valueOf(Integer status) {
        for (RelayMethodEnum value : values()) {
            if (value.getStatus().equals(status)) {
                return value;
            }
        }
        return null;
    }

    public static RelayMethodEnum valueOfCode(String code) {
        for (RelayMethodEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }

}


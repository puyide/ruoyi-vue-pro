package cn.iocoder.yudao.module.trade.enums.handover;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 物流状态枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum ExpressStatusEnum implements ArrayValuable<Integer> {

    PENDING(0, "待发货"),
    SHIPPED(1, "已发货"),
    IN_TRANSIT(2, "运输中"),
    OUT_FOR_DELIVERY(3, "派送中"),
    DELIVERED(4, "已签收");

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(ExpressStatusEnum::getStatus).toArray(Integer[]::new);

    /**
     * 状态
     */
    private final Integer status;
    /**
     * 状态名
     */
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

    public static ExpressStatusEnum valueOf(Integer status) {
        return Arrays.stream(values())
                .filter(item -> item.getStatus().equals(status))
                .findFirst()
                .orElse(null);
    }

}


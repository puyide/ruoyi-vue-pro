package cn.iocoder.yudao.module.product.enums.equipment;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 康复训练器材状态枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum EquipmentStatusEnum implements ArrayValuable<Integer> {

    NEW(1, "全新"),
    NINETY_PERCENT_NEW(2, "九成新"),
    EIGHTY_PERCENT_NEW(3, "八成新"),
    SEVENTY_PERCENT_NEW(4, "七成新以下");

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(EquipmentStatusEnum::getStatus).toArray(Integer[]::new);

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

    public static EquipmentStatusEnum valueOf(Integer status) {
        return Arrays.stream(values())
                .filter(item -> item.getStatus().equals(status))
                .findFirst()
                .orElse(null);
    }

}


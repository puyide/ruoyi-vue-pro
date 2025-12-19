package cn.iocoder.yudao.module.product.enums.equipment;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 器材共享类型枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum ShareTypeEnum implements ArrayValuable<Integer> {

    DONATE_ONLY(1, "仅赠送"),
    BORROW_ONLY(2, "仅借用"),
    BOTH(3, "赠送或借用");

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(ShareTypeEnum::getType).toArray(Integer[]::new);

    /**
     * 类型
     */
    private final Integer type;
    /**
     * 类型名
     */
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

    public static ShareTypeEnum valueOf(Integer type) {
        return Arrays.stream(values())
                .filter(item -> item.getType().equals(type))
                .findFirst()
                .orElse(null);
    }

    /**
     * 判断是否支持赠送
     */
    public boolean supportDonate() {
        return this == DONATE_ONLY || this == BOTH;
    }

    /**
     * 判断是否支持借用
     */
    public boolean supportBorrow() {
        return this == BORROW_ONLY || this == BOTH;
    }

}


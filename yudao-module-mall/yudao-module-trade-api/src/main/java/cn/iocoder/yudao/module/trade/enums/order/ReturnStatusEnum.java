package cn.iocoder.yudao.module.trade.enums.order;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 器材归还状态枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum ReturnStatusEnum implements ArrayValuable<Integer> {

    NOT_RETURNED(0, "未归还"),
    RETURNED(1, "已归还"),
    OVERDUE(2, "逾期未还"),
    DAMAGED(3, "已损坏");

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(ReturnStatusEnum::getStatus).toArray(Integer[]::new);

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

    public static ReturnStatusEnum valueOf(Integer status) {
        return Arrays.stream(values())
                .filter(item -> item.getStatus().equals(status))
                .findFirst()
                .orElse(null);
    }

}


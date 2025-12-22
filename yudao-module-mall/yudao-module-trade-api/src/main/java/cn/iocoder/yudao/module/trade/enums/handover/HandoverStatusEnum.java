package cn.iocoder.yudao.module.trade.enums.handover;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 交接状态枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum HandoverStatusEnum implements ArrayValuable<Integer> {

    COMMUNICATING(0, "沟通中"),
    ARRANGED(1, "约见/寄出"),
    COMPLETED(2, "完成"),
    IN_PROGRESS(3, "进行中"),
    PENDING (4, "待办");

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(HandoverStatusEnum::getStatus).toArray(Integer[]::new);

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

    public static HandoverStatusEnum valueOf(Integer status) {
        return Arrays.stream(values())
                .filter(item -> item.getStatus().equals(status))
                .findFirst()
                .orElse(null);
    }

}


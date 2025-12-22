package cn.iocoder.yudao.module.trade.enums.handover;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 交接模式枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum HandoverModeEnum implements ArrayValuable<Integer> {

    EXPRESS(1, "快递模式"),
    FACE_TO_FACE(2, "当面模式");

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(HandoverModeEnum::getMode).toArray(Integer[]::new);

    /**
     * 模式
     */
    private final Integer mode;
    /**
     * 模式名
     */
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

    public static HandoverModeEnum valueOf(Integer mode) {
        return Arrays.stream(values())
                .filter(item -> item.getMode().equals(mode))
                .findFirst()
                .orElse(null);
    }

}


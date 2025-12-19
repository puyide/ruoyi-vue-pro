package cn.iocoder.yudao.module.product.enums.equipment;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 康复训练类型枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum TrainingTypeEnum {

    SENSORY("sensory", "感统训练"),
    LANGUAGE("language", "语言训练"),
    COGNITIVE("cognitive", "认知训练"),
    SOCIAL("social", "社交训练"),
    FINE_MOTOR("fine_motor", "精细动作"),
    GROSS_MOTOR("gross_motor", "大运动");

    /**
     * 类型代码
     */
    private final String code;
    /**
     * 类型名称
     */
    private final String name;

    public static TrainingTypeEnum valueOfCode(String code) {
        return Arrays.stream(values())
                .filter(item -> item.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }

}


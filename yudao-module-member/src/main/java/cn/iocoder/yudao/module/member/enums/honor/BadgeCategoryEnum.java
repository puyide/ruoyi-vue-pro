package cn.iocoder.yudao.module.member.enums.honor;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 勋章类别枚举
 *
 * @author 星语家园
 */
@Getter
@AllArgsConstructor
public enum BadgeCategoryEnum {

    GROWTH_MEMORY("growth_memory", "成长记忆"),
    CONNECTION("connection", "连接确认"),
    PRESENCE("presence", "存在确认");

    private final String code;
    private final String name;

    public static BadgeCategoryEnum getByCode(String code) {
        for (BadgeCategoryEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}


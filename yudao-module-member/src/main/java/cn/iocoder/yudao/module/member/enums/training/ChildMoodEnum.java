package cn.iocoder.yudao.module.member.enums.training;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 孩子情绪状态枚举
 * 
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum ChildMoodEnum {

    GOOD(1, "还不错", "😀"),
    NORMAL(2, "一般", "😐"),
    BAD(3, "情绪不好", "😣");

    /**
     * 状态值
     */
    private final Integer value;
    /**
     * 状态名称
     */
    private final String name;
    /**
     * 表情
     */
    private final String emoji;

    /**
     * 根据状态值获取枚举
     */
    public static ChildMoodEnum getByValue(Integer value) {
        for (ChildMoodEnum mood : values()) {
            if (mood.getValue().equals(value)) {
                return mood;
            }
        }
        return null;
    }

}


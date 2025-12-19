package cn.iocoder.yudao.module.member.enums.training;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 训练难度等级枚举
 * 
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum TrainingDifficultyLevelEnum {

    LEVEL_1(1, "L1", "引导型", "大人提供大量提示，环境简化、干扰少，成功标准宽松"),
    LEVEL_2(2, "L2", "部分独立型", "减少物理帮助，主要用语言/眼神提示，环境略放宽"),
    LEVEL_3(3, "L3", "泛化挑战型", "在更多场景中使用，提示减少，成功标准提高");

    /**
     * 等级值
     */
    private final Integer level;
    /**
     * 等级编码
     */
    private final String code;
    /**
     * 等级名称
     */
    private final String name;
    /**
     * 等级描述
     */
    private final String description;

    /**
     * 根据等级值获取枚举
     */
    public static TrainingDifficultyLevelEnum getByLevel(Integer level) {
        for (TrainingDifficultyLevelEnum levelEnum : values()) {
            if (levelEnum.getLevel().equals(level)) {
                return levelEnum;
            }
        }
        return null;
    }

}


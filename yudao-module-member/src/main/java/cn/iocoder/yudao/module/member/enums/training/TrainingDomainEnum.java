package cn.iocoder.yudao.module.member.enums.training;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 训练域枚举
 * 
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum TrainingDomainEnum {

    SOCIAL("social", "社交与互动"),
    LANGUAGE("language", "语言与沟通"),
    EMOTION("emotion", "情绪与行为"),
    COGNITION("cognition", "认知与规则"),
    SENSORY("sensory", "感觉与运动"),
    DAILY_LIVING("daily_living", "生活自理");

    /**
     * 域编码
     */
    private final String code;
    /**
     * 域名称
     */
    private final String name;

    /**
     * 根据编码获取枚举
     */
    public static TrainingDomainEnum getByCode(String code) {
        for (TrainingDomainEnum domain : values()) {
            if (domain.getCode().equals(code)) {
                return domain;
            }
        }
        return null;
    }

}


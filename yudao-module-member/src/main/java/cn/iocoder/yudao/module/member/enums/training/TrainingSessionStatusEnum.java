package cn.iocoder.yudao.module.member.enums.training;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 训练会话状态枚举
 * 
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum TrainingSessionStatusEnum {

    PLANNED(0, "已计划"),
    IN_PROGRESS(1, "进行中"),
    DONE(2, "已完成"),
    SKIPPED(3, "已跳过");

    /**
     * 状态值
     */
    private final Integer status;
    /**
     * 状态名称
     */
    private final String name;

    /**
     * 根据状态值获取枚举
     */
    public static TrainingSessionStatusEnum getByStatus(Integer status) {
        for (TrainingSessionStatusEnum statusEnum : values()) {
            if (statusEnum.getStatus().equals(status)) {
                return statusEnum;
            }
        }
        return null;
    }

}


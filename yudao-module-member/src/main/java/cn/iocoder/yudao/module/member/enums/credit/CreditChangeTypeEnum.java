package cn.iocoder.yudao.module.member.enums.credit;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 信用分变动类型枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum CreditChangeTypeEnum implements ArrayValuable<Integer> {

    DONATE(1, "捐赠器材", 10),
    RETURN_ON_TIME(2, "按时归还", 5),
    OVERDUE(3, "逾期归还", -10),
    DAMAGE(4, "器材损坏", -20),
    VIOLATION(5, "违规行为", -30),
    GOOD_REVIEW(6, "优质评价", 3);

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(CreditChangeTypeEnum::getType).toArray(Integer[]::new);

    /**
     * 类型
     */
    private final Integer type;
    /**
     * 类型名
     */
    private final String name;
    /**
     * 默认分数变动值
     */
    private final Integer defaultScore;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

    public static CreditChangeTypeEnum valueOf(Integer type) {
        return Arrays.stream(values())
                .filter(item -> item.getType().equals(type))
                .findFirst()
                .orElse(null);
    }

}


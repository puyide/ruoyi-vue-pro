package cn.iocoder.yudao.module.product.enums.relay;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 接力好物状态枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum RelayItemStatusEnum {

    PENDING_REVIEW(0, "pending_review", "待审核"),
    AVAILABLE(1, "available", "接力中"),
    MATCHED(2, "matched", "已匹配"),
    TRANSFERRING(3, "transferring", "交接中"),
    COMPLETED(4, "completed", "已完成"),
    REJECTED(5, "rejected", "审核拒绝"),
    OFFLINE(6, "offline", "已下架");

    /**
     * 状态值
     */
    private final Integer status;

    /**
     * 代码（用于小程序）
     */
    private final String code;

    /**
     * 名称
     */
    private final String name;

    public static RelayItemStatusEnum valueOf(Integer status) {
        for (RelayItemStatusEnum value : values()) {
            if (value.getStatus().equals(status)) {
                return value;
            }
        }
        return null;
    }

}


package cn.iocoder.yudao.module.product.enums.relay;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 接力申请状态枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum RelayRequestStatusEnum {

    PENDING(0, "pending", "待处理"),
    ACCEPTED(1, "accepted", "已同意"),
    REJECTED(2, "rejected", "已拒绝"),
    CANCELLED(3, "cancelled", "已取消"),
    COMPLETED(4, "completed", "已完成");

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

    public static RelayRequestStatusEnum valueOf(Integer status) {
        for (RelayRequestStatusEnum value : values()) {
            if (value.getStatus().equals(status)) {
                return value;
            }
        }
        return null;
    }

}


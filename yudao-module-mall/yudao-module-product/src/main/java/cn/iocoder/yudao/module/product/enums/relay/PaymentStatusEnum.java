package cn.iocoder.yudao.module.product.enums.relay;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 低价转让支付状态枚举
 * 
 * 注意：平台不参与资金流转，仅记录支付凭证
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum PaymentStatusEnum {

    PENDING(0, "pending", "待支付"),
    PROOF_UPLOADED(1, "proof_uploaded", "已上传凭证"),
    CONFIRMED(2, "confirmed", "卖家已确认"),
    DISPUTED(3, "disputed", "有争议");

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

    public static PaymentStatusEnum valueOf(Integer status) {
        for (PaymentStatusEnum value : values()) {
            if (value.getStatus().equals(status)) {
                return value;
            }
        }
        return null;
    }

}


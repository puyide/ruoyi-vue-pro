package cn.iocoder.yudao.module.product.enums.relay;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 交付方式枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum DeliveryMethodEnum {

    FACE_TO_FACE(1, "face_to_face", "当面交接"),
    EXPRESS(2, "express", "快递寄送");

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

    public static DeliveryMethodEnum valueOf(Integer status) {
        for (DeliveryMethodEnum value : values()) {
            if (value.getStatus().equals(status)) {
                return value;
            }
        }
        return null;
    }

}


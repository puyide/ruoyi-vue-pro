package cn.iocoder.yudao.module.member.enums.peergroup;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * NodeBB 同步状态枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum NodebbSyncStatusEnum {

    PENDING(0, "待处理"),
    PROCESSING(1, "处理中"),
    SUCCESS(2, "成功"),
    FAILED(3, "失败");

    private final Integer status;
    private final String name;

}


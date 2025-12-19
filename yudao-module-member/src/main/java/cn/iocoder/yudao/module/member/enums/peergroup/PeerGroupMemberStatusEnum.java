package cn.iocoder.yudao.module.member.enums.peergroup;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 同行小组成员状态枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum PeerGroupMemberStatusEnum {

    NORMAL(0, "正常"),
    MUTED(1, "静音"),
    LEFT(2, "已退出"),
    REMOVED(3, "被移除"),
    PENDING(4, "待审核");

    private final Integer status;
    private final String name;

    public static boolean isActive(Integer status) {
        return NORMAL.getStatus().equals(status) || MUTED.getStatus().equals(status);
    }

}


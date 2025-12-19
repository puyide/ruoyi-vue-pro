package cn.iocoder.yudao.module.member.enums.peergroup;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 同行小组加入模式枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum PeerGroupJoinModeEnum {

    FREE(0, "自由加入"),
    APPROVAL(1, "需要审核"),
    INVITE_ONLY(2, "仅邀请");

    private final Integer mode;
    private final String name;

}


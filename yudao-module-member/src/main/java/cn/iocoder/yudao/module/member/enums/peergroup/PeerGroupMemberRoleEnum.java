package cn.iocoder.yudao.module.member.enums.peergroup;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 同行小组成员角色枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum PeerGroupMemberRoleEnum {

    OWNER("owner", "群主"),
    ADMIN("admin", "管理员"),
    MEMBER("member", "普通成员");

    private final String role;
    private final String name;

    public static PeerGroupMemberRoleEnum valueOf(String role, PeerGroupMemberRoleEnum defaultRole) {
        for (PeerGroupMemberRoleEnum roleEnum : values()) {
            if (roleEnum.getRole().equals(role)) {
                return roleEnum;
            }
        }
        return defaultRole;
    }

}


package cn.iocoder.yudao.module.member.enums.peergroup;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * NodeBB 同步类型枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum NodebbSyncTypeEnum {

    // 小组相关
    GROUP_CREATE("GROUP_CREATE", "创建小组"),
    GROUP_UPDATE("GROUP_UPDATE", "更新小组"),
    GROUP_DELETE("GROUP_DELETE", "删除小组"),
    
    // 分类相关
    CATEGORY_CREATE("CATEGORY_CREATE", "创建私密分类"),
    CATEGORY_UPDATE("CATEGORY_UPDATE", "更新分类权限"),
    
    // 成员相关
    MEMBER_JOIN("MEMBER_JOIN", "成员加入"),
    MEMBER_LEAVE("MEMBER_LEAVE", "成员离开"),
    MEMBER_BAN("MEMBER_BAN", "成员封禁"),
    
    // 帖子相关
    TOPIC_CREATE("TOPIC_CREATE", "创建帖子（如感谢贴）");

    private final String type;
    private final String name;

}


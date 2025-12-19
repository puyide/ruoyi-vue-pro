package cn.iocoder.yudao.module.member.dal.dataobject.peergroup;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.framework.mybatis.core.type.StringListTypeHandler;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 同行小组 DO
 * 
 * 说明：这是业务数据库中的"唯一真相源"，NodeBB 只是同步的"投影"
 *
 * @author 芋道源码
 */
@TableName(value = "peer_group", autoResultMap = true)
@KeySequence("peer_group_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PeerGroupDO extends BaseDO {

    /**
     * 小组编号
     */
    @TableId
    private Long id;

    /**
     * 小组名称
     */
    private String name;

    /**
     * 小组图标（emoji或icon code）
     */
    private String icon;

    /**
     * 主题色
     * 
     * 枚举：blue/orange/purple/green/yellow
     */
    private String theme;

    /**
     * 小组封面图URL
     */
    private String coverUrl;

    /**
     * 小组简介
     */
    private String description;

    /**
     * 分类
     * 
     * 枚举：language/emotion/sensory/social/kindergarten
     */
    private String category;

    /**
     * 标签，JSON数组
     * 
     * 例如：["语言", "3-6岁"]
     */
    @TableField(typeHandler = StringListTypeHandler.class)
    private List<String> tags;

    /**
     * 适用年龄组
     * 
     * 枚举：0-3/3-6/6-12
     */
    private String ageGroup;

    /**
     * 最大成员数
     */
    private Integer maxMembers;

    /**
     * 当前成员数（冗余计数，提升查询性能）
     */
    private Integer memberCount;

    // ========== 微信群相关 ==========

    /**
     * 微信群二维码图片URL
     */
    private String wechatQrUrl;

    /**
     * 二维码过期时间
     */
    private LocalDateTime wechatQrExpire;

    /**
     * 企业微信客户群链接（可选）
     */
    private String wecomUrl;

    // ========== NodeBB 关联 ==========

    /**
     * NodeBB Group ID（同步后回填）
     */
    private Long nodebbGroupId;

    /**
     * NodeBB 私密分类 ID
     */
    private Long nodebbCategoryId;

    /**
     * NodeBB 同步状态
     * 
     * 0-待同步 1-已同步 2-同步失败
     */
    private Integer nodebbSyncStatus;

    /**
     * 最后同步时间
     */
    private LocalDateTime nodebbSyncedAt;

    // ========== 状态与审核 ==========

    /**
     * 状态
     * 
     * 0-开启 1-关闭
     */
    private Integer status;

    /**
     * 加入模式
     * 
     * 0-自由加入 1-需要审核 2-仅邀请
     */
    private Integer joinMode;

    /**
     * 可见性
     * 
     * 0-公开 1-仅成员可见
     */
    private Integer visibility;

    /**
     * 创建人用户ID
     */
    private Long creatorId;

}


package cn.iocoder.yudao.module.member.dal.dataobject.peergroup;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * NodeBB 同步发件箱 DO
 * 
 * 实现 Outbox 模式，避免直接同步导致的请求阻塞
 * NodeBB API 偶发失败很正常，不能直接在用户请求里硬同步
 *
 * @author 芋道源码
 */
@TableName("nodebb_sync_outbox")
@KeySequence("nodebb_sync_outbox_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NodebbSyncOutboxDO extends BaseDO {

    /**
     * 消息编号
     */
    @TableId
    private Long id;

    /**
     * 同步类型
     * 
     * GROUP_CREATE - 创建小组
     * GROUP_UPDATE - 更新小组
     * GROUP_DELETE - 删除小组
     * CATEGORY_CREATE - 创建私密分类
     * MEMBER_JOIN - 成员加入
     * MEMBER_LEAVE - 成员离开
     * MEMBER_BAN - 成员封禁
     */
    private String syncType;

    /**
     * 操作
     * 
     * CREATE/UPDATE/DELETE
     */
    private String operation;

    /**
     * 业务类型
     * 
     * PEER_GROUP / PEER_GROUP_MEMBER
     */
    private String bizType;

    /**
     * 业务ID
     */
    private Long bizId;

    /**
     * 同步数据JSON
     */
    private String payload;

    /**
     * 状态
     * 
     * 0-待处理 1-处理中 2-成功 3-失败
     */
    private Integer status;

    /**
     * 重试次数
     */
    private Integer retryCount;

    /**
     * 最大重试次数
     */
    private Integer maxRetries;

    /**
     * 下次重试时间
     */
    private LocalDateTime nextRetryAt;

    /**
     * 处理结果/错误信息
     */
    private String result;

    /**
     * 处理完成时间
     */
    private LocalDateTime processedAt;

}


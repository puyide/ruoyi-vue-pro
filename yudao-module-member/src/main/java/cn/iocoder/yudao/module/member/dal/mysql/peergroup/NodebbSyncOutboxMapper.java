package cn.iocoder.yudao.module.member.dal.mysql.peergroup;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.member.dal.dataobject.peergroup.NodebbSyncOutboxDO;
import cn.iocoder.yudao.module.member.enums.peergroup.NodebbSyncStatusEnum;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * NodeBB 同步发件箱 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface NodebbSyncOutboxMapper extends BaseMapperX<NodebbSyncOutboxDO> {

    /**
     * 查询待处理的同步任务
     * 条件：待处理 或 (失败 且 重试次数未超过最大值 且 下次重试时间已到)
     */
    default List<NodebbSyncOutboxDO> selectPendingTasks(int limit) {
        LocalDateTime now = LocalDateTime.now();
        return selectList(new LambdaQueryWrapperX<NodebbSyncOutboxDO>()
                .and(wrapper -> wrapper
                    .eq(NodebbSyncOutboxDO::getStatus, NodebbSyncStatusEnum.PENDING.getStatus())
                    .or(orWrapper -> orWrapper
                        .eq(NodebbSyncOutboxDO::getStatus, NodebbSyncStatusEnum.FAILED.getStatus())
                        .lt(NodebbSyncOutboxDO::getRetryCount, 5)
                        .le(NodebbSyncOutboxDO::getNextRetryAt, now)
                    )
                )
                .orderByAsc(NodebbSyncOutboxDO::getCreateTime)
                .last("LIMIT " + limit));
    }

    /**
     * 查询指定业务的同步记录
     */
    default List<NodebbSyncOutboxDO> selectByBiz(String bizType, Long bizId) {
        return selectList(new LambdaQueryWrapperX<NodebbSyncOutboxDO>()
                .eq(NodebbSyncOutboxDO::getBizType, bizType)
                .eq(NodebbSyncOutboxDO::getBizId, bizId)
                .orderByDesc(NodebbSyncOutboxDO::getCreateTime));
    }

    /**
     * 更新任务状态为处理中
     */
    default void updateStatusToProcessing(Long id) {
        NodebbSyncOutboxDO updateObj = new NodebbSyncOutboxDO();
        updateObj.setId(id);
        updateObj.setStatus(NodebbSyncStatusEnum.PROCESSING.getStatus());
        updateById(updateObj);
    }

    /**
     * 更新任务为成功
     */
    default void updateStatusToSuccess(Long id, String result) {
        NodebbSyncOutboxDO updateObj = new NodebbSyncOutboxDO();
        updateObj.setId(id);
        updateObj.setStatus(NodebbSyncStatusEnum.SUCCESS.getStatus());
        updateObj.setResult(result);
        updateObj.setProcessedAt(LocalDateTime.now());
        updateById(updateObj);
    }

    /**
     * 更新任务为失败，并设置重试
     */
    default void updateStatusToFailed(Long id, String errorMsg, int retryCount) {
        NodebbSyncOutboxDO updateObj = new NodebbSyncOutboxDO();
        updateObj.setId(id);
        updateObj.setStatus(NodebbSyncStatusEnum.FAILED.getStatus());
        updateObj.setResult(errorMsg);
        updateObj.setRetryCount(retryCount);
        // 指数退避重试：1分钟、2分钟、4分钟、8分钟、16分钟
        long delayMinutes = (long) Math.pow(2, retryCount - 1);
        updateObj.setNextRetryAt(LocalDateTime.now().plusMinutes(delayMinutes));
        updateById(updateObj);
    }

}


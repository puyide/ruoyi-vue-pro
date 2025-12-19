package cn.iocoder.yudao.module.member.service.peergroup;

import cn.iocoder.yudao.module.member.dal.dataobject.peergroup.NodebbSyncOutboxDO;

/**
 * NodeBB 小组同步 Service 接口
 * 
 * 实现 Outbox 模式：
 * 1. 业务操作时，将同步任务写入 Outbox 表
 * 2. 定时 Job 扫描 Outbox 表，执行同步
 * 3. 失败时指数退避重试
 *
 * @author 芋道源码
 */
public interface NodebbGroupSyncService {

    /**
     * 处理单个同步任务
     *
     * @param outbox 同步任务
     */
    void processSyncTask(NodebbSyncOutboxDO outbox);

    /**
     * 扫描并处理待同步任务（由定时 Job 调用）
     *
     * @param batchSize 每批处理数量
     * @return 处理的任务数量
     */
    int processPendingTasks(int batchSize);

}


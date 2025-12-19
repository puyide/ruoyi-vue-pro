package cn.iocoder.yudao.module.member.service.peergroup;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.module.member.config.NodeBBProperties;
import cn.iocoder.yudao.module.member.dal.dataobject.peergroup.NodebbSyncOutboxDO;
import cn.iocoder.yudao.module.member.dal.dataobject.peergroup.PeerGroupDO;
import cn.iocoder.yudao.module.member.dal.dataobject.peergroup.PeerGroupMemberDO;
import cn.iocoder.yudao.module.member.dal.mysql.peergroup.NodebbSyncOutboxMapper;
import cn.iocoder.yudao.module.member.dal.mysql.peergroup.PeerGroupMapper;
import cn.iocoder.yudao.module.member.dal.mysql.peergroup.PeerGroupMemberMapper;
import cn.iocoder.yudao.module.member.enums.peergroup.NodebbSyncStatusEnum;
import cn.iocoder.yudao.module.member.enums.peergroup.NodebbSyncTypeEnum;
import cn.iocoder.yudao.module.member.framework.nodebb.core.NodeBBApiClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * NodeBB 小组同步 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Slf4j
public class NodebbGroupSyncServiceImpl implements NodebbGroupSyncService {

    @Resource
    private NodebbSyncOutboxMapper outboxMapper;

    @Resource
    private PeerGroupMapper peerGroupMapper;

    @Resource
    private PeerGroupMemberMapper peerGroupMemberMapper;

    @Resource
    private NodeBBApiClient nodeBBApiClient;

    @Resource
    private NodeBBProperties nodeBBProperties;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processSyncTask(NodebbSyncOutboxDO outbox) {
        // 1. 检查 NodeBB 是否启用
        if (!isNodeBBEnabled()) {
            log.debug("[processSyncTask] NodeBB 未启用，跳过同步任务: {}", outbox.getId());
            outboxMapper.updateStatusToSuccess(outbox.getId(), "NodeBB 未启用，跳过");
            return;
        }

        // 2. 更新状态为处理中
        outboxMapper.updateStatusToProcessing(outbox.getId());

        try {
            // 3. 根据同步类型分发处理
            String syncType = outbox.getSyncType();
            
            if (NodebbSyncTypeEnum.GROUP_CREATE.getType().equals(syncType)) {
                handleGroupCreate(outbox);
            } else if (NodebbSyncTypeEnum.GROUP_UPDATE.getType().equals(syncType)) {
                handleGroupUpdate(outbox);
            } else if (NodebbSyncTypeEnum.GROUP_DELETE.getType().equals(syncType)) {
                handleGroupDelete(outbox);
            } else if (NodebbSyncTypeEnum.MEMBER_JOIN.getType().equals(syncType)) {
                handleMemberJoin(outbox);
            } else if (NodebbSyncTypeEnum.MEMBER_LEAVE.getType().equals(syncType) ||
                       NodebbSyncTypeEnum.MEMBER_BAN.getType().equals(syncType)) {
                handleMemberLeave(outbox);
            } else {
                log.warn("[processSyncTask] 未知的同步类型: {}", syncType);
                outboxMapper.updateStatusToSuccess(outbox.getId(), "未知同步类型，跳过");
            }
            
        } catch (Exception e) {
            log.error("[processSyncTask] 同步任务失败: id={}, type={}", outbox.getId(), outbox.getSyncType(), e);
            
            // 更新为失败，设置重试
            int newRetryCount = (outbox.getRetryCount() == null ? 0 : outbox.getRetryCount()) + 1;
            outboxMapper.updateStatusToFailed(outbox.getId(), e.getMessage(), newRetryCount);
        }
    }

    @Override
    public int processPendingTasks(int batchSize) {
        // 1. 查询待处理任务
        List<NodebbSyncOutboxDO> tasks = outboxMapper.selectPendingTasks(batchSize);
        if (tasks.isEmpty()) {
            return 0;
        }

        log.info("[processPendingTasks] 开始处理 {} 个同步任务", tasks.size());

        // 2. 逐个处理
        int successCount = 0;
        for (NodebbSyncOutboxDO task : tasks) {
            try {
                processSyncTask(task);
                successCount++;
            } catch (Exception e) {
                log.error("[processPendingTasks] 处理任务异常: id={}", task.getId(), e);
            }
        }

        log.info("[processPendingTasks] 完成处理，成功: {}/{}", successCount, tasks.size());
        return successCount;
    }

    // ========== 私有方法：同步处理 ==========

    private void handleGroupCreate(NodebbSyncOutboxDO outbox) throws Exception {
        PeerGroupDO peerGroup = JSONUtil.toBean(outbox.getPayload(), PeerGroupDO.class);
        
        // 1. 调用 NodeBB API 创建 Group
        Map<String, Object> groupResult = createNodeBBGroup(peerGroup);
        Long nodebbGroupId = ((Number) groupResult.get("groupId")).longValue();
        
        // 2. 调用 NodeBB API 创建私密分类
        Long nodebbCategoryId = createNodeBBPrivateCategory(peerGroup, nodebbGroupId);
        
        // 3. 更新业务表
        peerGroupMapper.updateNodebbSyncStatus(peerGroup.getId(), 
                NodebbSyncStatusEnum.SUCCESS.getStatus(), nodebbGroupId, nodebbCategoryId);
        
        // 4. 更新 Outbox 状态
        String result = String.format("nodebbGroupId=%d, nodebbCategoryId=%d", nodebbGroupId, nodebbCategoryId);
        outboxMapper.updateStatusToSuccess(outbox.getId(), result);
        
        log.info("[handleGroupCreate] 小组创建同步成功: bizId={}, nodebbGroupId={}", peerGroup.getId(), nodebbGroupId);
    }

    private void handleGroupUpdate(NodebbSyncOutboxDO outbox) throws Exception {
        PeerGroupDO peerGroup = JSONUtil.toBean(outbox.getPayload(), PeerGroupDO.class);
        
        if (peerGroup.getNodebbGroupId() == null) {
            log.warn("[handleGroupUpdate] 小组尚未同步到 NodeBB，跳过更新: bizId={}", peerGroup.getId());
            outboxMapper.updateStatusToSuccess(outbox.getId(), "小组尚未同步到 NodeBB");
            return;
        }
        
        // 调用 NodeBB API 更新 Group
        updateNodeBBGroup(peerGroup);
        
        outboxMapper.updateStatusToSuccess(outbox.getId(), "更新成功");
        log.info("[handleGroupUpdate] 小组更新同步成功: bizId={}", peerGroup.getId());
    }

    private void handleGroupDelete(NodebbSyncOutboxDO outbox) throws Exception {
        PeerGroupDO peerGroup = JSONUtil.toBean(outbox.getPayload(), PeerGroupDO.class);
        
        if (peerGroup.getNodebbGroupId() == null) {
            outboxMapper.updateStatusToSuccess(outbox.getId(), "小组尚未同步到 NodeBB，无需删除");
            return;
        }
        
        // 调用 NodeBB API 删除 Group
        deleteNodeBBGroup(peerGroup.getNodebbGroupId());
        
        outboxMapper.updateStatusToSuccess(outbox.getId(), "删除成功");
        log.info("[handleGroupDelete] 小组删除同步成功: bizId={}", peerGroup.getId());
    }

    private void handleMemberJoin(NodebbSyncOutboxDO outbox) throws Exception {
        PeerGroupMemberDO member = JSONUtil.toBean(outbox.getPayload(), PeerGroupMemberDO.class);
        
        // 获取小组信息
        PeerGroupDO peerGroup = peerGroupMapper.selectById(member.getGroupId());
        if (peerGroup == null || peerGroup.getNodebbGroupId() == null) {
            log.warn("[handleMemberJoin] 小组不存在或未同步: groupId={}", member.getGroupId());
            outboxMapper.updateStatusToSuccess(outbox.getId(), "小组未同步到 NodeBB");
            return;
        }
        
        // 调用 NodeBB API 添加成员到 Group
        addMemberToNodeBBGroup(peerGroup.getNodebbGroupId(), member.getUserId());
        
        // 更新成员同步状态
        member.setNodebbSynced(true);
        member.setNodebbSyncedAt(LocalDateTime.now());
        peerGroupMemberMapper.updateById(member);
        
        outboxMapper.updateStatusToSuccess(outbox.getId(), "成员加入同步成功");
        log.info("[handleMemberJoin] 成员加入同步成功: groupId={}, userId={}", member.getGroupId(), member.getUserId());
    }

    private void handleMemberLeave(NodebbSyncOutboxDO outbox) throws Exception {
        PeerGroupMemberDO member = JSONUtil.toBean(outbox.getPayload(), PeerGroupMemberDO.class);
        
        // 获取小组信息
        PeerGroupDO peerGroup = peerGroupMapper.selectById(member.getGroupId());
        if (peerGroup == null || peerGroup.getNodebbGroupId() == null) {
            outboxMapper.updateStatusToSuccess(outbox.getId(), "小组未同步到 NodeBB");
            return;
        }
        
        // 调用 NodeBB API 从 Group 移除成员
        removeMemberFromNodeBBGroup(peerGroup.getNodebbGroupId(), member.getUserId());
        
        outboxMapper.updateStatusToSuccess(outbox.getId(), "成员离开同步成功");
        log.info("[handleMemberLeave] 成员离开同步成功: groupId={}, userId={}", member.getGroupId(), member.getUserId());
    }

    // ========== NodeBB API 调用 ==========

    private boolean isNodeBBEnabled() {
        return nodeBBProperties != null && Boolean.TRUE.equals(nodeBBProperties.getEnabled()) && nodeBBApiClient != null;
    }

    private Map<String, Object> createNodeBBGroup(PeerGroupDO peerGroup) throws Exception {
        // TODO: 实现实际的 NodeBB API 调用
        // POST /api/admin/groups
        log.info("[createNodeBBGroup] 模拟创建 NodeBB Group: name={}", peerGroup.getName());
        
        // 模拟返回
        return Map.of("groupId", System.currentTimeMillis() % 10000);
    }

    private Long createNodeBBPrivateCategory(PeerGroupDO peerGroup, Long nodebbGroupId) throws Exception {
        // TODO: 实现实际的 NodeBB API 调用
        // POST /api/admin/categories
        // 设置权限：只有该 Group 成员可见
        log.info("[createNodeBBPrivateCategory] 模拟创建私密分类: groupName={}, nodebbGroupId={}", 
                peerGroup.getName(), nodebbGroupId);
        
        return System.currentTimeMillis() % 10000;
    }

    private void updateNodeBBGroup(PeerGroupDO peerGroup) throws Exception {
        // TODO: 实现实际的 NodeBB API 调用
        // PUT /api/admin/groups/{groupId}
        log.info("[updateNodeBBGroup] 模拟更新 NodeBB Group: nodebbGroupId={}", peerGroup.getNodebbGroupId());
    }

    private void deleteNodeBBGroup(Long nodebbGroupId) throws Exception {
        // TODO: 实现实际的 NodeBB API 调用
        // DELETE /api/admin/groups/{groupId}
        log.info("[deleteNodeBBGroup] 模拟删除 NodeBB Group: nodebbGroupId={}", nodebbGroupId);
    }

    private void addMemberToNodeBBGroup(Long nodebbGroupId, Long userId) throws Exception {
        // TODO: 实现实际的 NodeBB API 调用
        // 需要先获取用户的 NodeBB UID
        // PUT /api/admin/groups/{groupId}/members
        log.info("[addMemberToNodeBBGroup] 模拟添加成员: nodebbGroupId={}, userId={}", nodebbGroupId, userId);
    }

    private void removeMemberFromNodeBBGroup(Long nodebbGroupId, Long userId) throws Exception {
        // TODO: 实现实际的 NodeBB API 调用
        // DELETE /api/admin/groups/{groupId}/members/{uid}
        log.info("[removeMemberFromNodeBBGroup] 模拟移除成员: nodebbGroupId={}, userId={}", nodebbGroupId, userId);
    }

}


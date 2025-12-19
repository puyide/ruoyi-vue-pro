package cn.iocoder.yudao.module.member.service.peergroup;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.member.controller.admin.peergroup.vo.PeerGroupCreateReqVO;
import cn.iocoder.yudao.module.member.controller.admin.peergroup.vo.PeerGroupPageReqVO;
import cn.iocoder.yudao.module.member.controller.admin.peergroup.vo.PeerGroupUpdateReqVO;
import cn.iocoder.yudao.module.member.controller.app.peergroup.vo.AppPeerGroupJoinReqVO;
import cn.iocoder.yudao.module.member.convert.peergroup.PeerGroupConvert;
import cn.iocoder.yudao.module.member.dal.dataobject.peergroup.*;
import cn.iocoder.yudao.module.member.dal.mysql.peergroup.*;
import cn.iocoder.yudao.module.member.enums.peergroup.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.member.enums.ErrorCodeConstants.*;

/**
 * 同行小组 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
@Slf4j
public class PeerGroupServiceImpl implements PeerGroupService {

    @Resource
    private PeerGroupMapper peerGroupMapper;

    @Resource
    private PeerGroupMemberMapper peerGroupMemberMapper;

    @Resource
    private PeerGroupJoinRequestMapper joinRequestMapper;

    @Resource
    private NodebbSyncOutboxMapper outboxMapper;

    // ========== 管理后台：小组管理 ==========

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPeerGroup(PeerGroupCreateReqVO createReqVO) {
        // 1. 插入小组
        PeerGroupDO peerGroup = PeerGroupConvert.INSTANCE.convert(createReqVO);
        peerGroup.setMemberCount(0);
        peerGroup.setNodebbSyncStatus(NodebbSyncStatusEnum.PENDING.getStatus());
        peerGroupMapper.insert(peerGroup);

        // 2. 创建 Outbox 同步任务
        createSyncOutbox(NodebbSyncTypeEnum.GROUP_CREATE.getType(), "CREATE",
                "PEER_GROUP", peerGroup.getId(), peerGroup);

        return peerGroup.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePeerGroup(PeerGroupUpdateReqVO updateReqVO) {
        // 1. 校验存在
        validatePeerGroupExists(updateReqVO.getId());

        // 2. 更新
        PeerGroupDO updateObj = PeerGroupConvert.INSTANCE.convert(updateReqVO);
        peerGroupMapper.updateById(updateObj);

        // 3. 创建 Outbox 同步任务
        PeerGroupDO peerGroup = peerGroupMapper.selectById(updateReqVO.getId());
        createSyncOutbox(NodebbSyncTypeEnum.GROUP_UPDATE.getType(), "UPDATE",
                "PEER_GROUP", peerGroup.getId(), peerGroup);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePeerGroup(Long id) {
        // 1. 校验存在
        PeerGroupDO peerGroup = validatePeerGroupExists(id);

        // 2. 删除小组
        peerGroupMapper.deleteById(id);

        // 3. 删除所有成员关系
        // 实际上由于是逻辑删除，这里不需要真正删除成员

        // 4. 创建 Outbox 同步任务
        createSyncOutbox(NodebbSyncTypeEnum.GROUP_DELETE.getType(), "DELETE",
                "PEER_GROUP", id, peerGroup);
    }

    @Override
    public PeerGroupDO getPeerGroup(Long id) {
        return peerGroupMapper.selectById(id);
    }

    @Override
    public PageResult<PeerGroupDO> getPeerGroupPage(PeerGroupPageReqVO pageReqVO) {
        return peerGroupMapper.selectPage(pageReqVO);
    }

    @Override
    public List<PeerGroupDO> getEnabledPeerGroupList() {
        return peerGroupMapper.selectListByStatus(0); // 0-开启
    }

    @Override
    public List<PeerGroupDO> getPeerGroupListByCategory(String category) {
        return peerGroupMapper.selectListByCategory(category);
    }

    // ========== 小程序端：用户操作 ==========

    @Override
    public List<PeerGroupDO> getMyJoinedGroups(Long userId) {
        // 1. 获取用户加入的小组ID列表
        List<Long> groupIds = peerGroupMemberMapper.selectActiveGroupIdsByUserId(userId);
        if (CollUtil.isEmpty(groupIds)) {
            return Collections.emptyList();
        }

        // 2. 获取小组详情
        return peerGroupMapper.selectBatchIds(groupIds);
    }

    @Override
    public PeerGroupMemberDO getMembership(Long groupId, Long userId) {
        return peerGroupMemberMapper.selectByGroupIdAndUserId(groupId, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean joinGroup(Long userId, AppPeerGroupJoinReqVO joinReqVO) {
        Long groupId = joinReqVO.getGroupId();

        // 1. 校验小组存在且开启
        PeerGroupDO peerGroup = validatePeerGroupExists(groupId);
        if (peerGroup.getStatus() != 0) {
            throw exception(PEER_GROUP_NOT_ENABLED);
        }

        // 2. 校验是否已是成员
        PeerGroupMemberDO existMember = peerGroupMemberMapper.selectByGroupIdAndUserId(groupId, userId);
        if (existMember != null && PeerGroupMemberStatusEnum.isActive(existMember.getStatus())) {
            throw exception(PEER_GROUP_ALREADY_JOINED);
        }

        // 3. 校验成员数量限制
        Long currentCount = peerGroupMemberMapper.selectActiveCountByGroupId(groupId);
        if (peerGroup.getMaxMembers() != null && currentCount >= peerGroup.getMaxMembers()) {
            throw exception(PEER_GROUP_MEMBER_FULL);
        }

        // 4. 根据加入模式处理
        Integer joinMode = peerGroup.getJoinMode();
        if (joinMode == null) {
            joinMode = PeerGroupJoinModeEnum.FREE.getMode();
        }

        if (joinMode.equals(PeerGroupJoinModeEnum.FREE.getMode())) {
            // 自由加入 - 直接添加成员
            doAddMember(groupId, userId, PeerGroupMemberRoleEnum.MEMBER.getRole(), "self", null);
            return true;
        } else if (joinMode.equals(PeerGroupJoinModeEnum.APPROVAL.getMode())) {
            // 需要审核 - 创建申请记录
            createJoinRequest(groupId, userId, joinReqVO.getReason());
            return false;
        } else {
            // 仅邀请
            throw exception(PEER_GROUP_INVITE_ONLY);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void leaveGroup(Long userId, Long groupId) {
        // 1. 校验是否是成员
        PeerGroupMemberDO member = peerGroupMemberMapper.selectByGroupIdAndUserId(groupId, userId);
        if (member == null || !PeerGroupMemberStatusEnum.isActive(member.getStatus())) {
            throw exception(PEER_GROUP_NOT_MEMBER);
        }

        // 2. 群主不能退出（需要先转让群主）
        if (PeerGroupMemberRoleEnum.OWNER.getRole().equals(member.getRole())) {
            throw exception(PEER_GROUP_OWNER_CANNOT_LEAVE);
        }

        // 3. 更新成员状态为已退出
        member.setStatus(PeerGroupMemberStatusEnum.LEFT.getStatus());
        member.setLeftAt(LocalDateTime.now());
        peerGroupMemberMapper.updateById(member);

        // 4. 减少成员计数
        peerGroupMapper.decrementMemberCount(groupId);

        // 5. 创建 Outbox 同步任务
        createSyncOutbox(NodebbSyncTypeEnum.MEMBER_LEAVE.getType(), "DELETE",
                "PEER_GROUP_MEMBER", member.getId(), member);
    }

    @Override
    public List<PeerGroupDO> getRecommendGroups(Long userId, String category, int limit) {
        // 1. 获取用户已加入的小组ID
        List<Long> joinedGroupIds = peerGroupMemberMapper.selectActiveGroupIdsByUserId(userId);

        // 2. 获取推荐小组（公开且未加入的）
        List<PeerGroupDO> allGroups = (category != null && !category.isEmpty())
                ? peerGroupMapper.selectListByCategory(category)
                : peerGroupMapper.selectListByStatus(0);

        // 3. 过滤已加入的
        return allGroups.stream()
                .filter(g -> !joinedGroupIds.contains(g.getId()))
                .filter(g -> g.getVisibility() == null || g.getVisibility() == 0) // 公开
                .limit(limit)
                .collect(Collectors.toList());
    }

    // ========== 管理后台：成员管理 ==========

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addMember(Long groupId, Long userId, String role) {
        // 1. 校验小组存在
        validatePeerGroupExists(groupId);

        // 2. 校验是否已是成员
        PeerGroupMemberDO existMember = peerGroupMemberMapper.selectByGroupIdAndUserId(groupId, userId);
        if (existMember != null && PeerGroupMemberStatusEnum.isActive(existMember.getStatus())) {
            throw exception(PEER_GROUP_ALREADY_JOINED);
        }

        // 3. 添加成员
        doAddMember(groupId, userId, role, "admin", null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeMember(Long groupId, Long userId) {
        // 1. 校验是否是成员
        PeerGroupMemberDO member = peerGroupMemberMapper.selectByGroupIdAndUserId(groupId, userId);
        if (member == null || !PeerGroupMemberStatusEnum.isActive(member.getStatus())) {
            throw exception(PEER_GROUP_NOT_MEMBER);
        }

        // 2. 群主不能被移除
        if (PeerGroupMemberRoleEnum.OWNER.getRole().equals(member.getRole())) {
            throw exception(PEER_GROUP_CANNOT_REMOVE_OWNER);
        }

        // 3. 更新成员状态为被移除
        member.setStatus(PeerGroupMemberStatusEnum.REMOVED.getStatus());
        member.setLeftAt(LocalDateTime.now());
        peerGroupMemberMapper.updateById(member);

        // 4. 减少成员计数
        peerGroupMapper.decrementMemberCount(groupId);

        // 5. 创建 Outbox 同步任务
        createSyncOutbox(NodebbSyncTypeEnum.MEMBER_BAN.getType(), "DELETE",
                "PEER_GROUP_MEMBER", member.getId(), member);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMemberRole(Long groupId, Long userId, String role) {
        // 1. 校验是否是成员
        PeerGroupMemberDO member = peerGroupMemberMapper.selectByGroupIdAndUserId(groupId, userId);
        if (member == null || !PeerGroupMemberStatusEnum.isActive(member.getStatus())) {
            throw exception(PEER_GROUP_NOT_MEMBER);
        }

        // 2. 更新角色
        member.setRole(role);
        peerGroupMemberMapper.updateById(member);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reviewJoinRequest(Long requestId, boolean approved, String rejectReason, Long reviewerId) {
        // 1. 获取申请记录
        PeerGroupJoinRequestDO request = joinRequestMapper.selectById(requestId);
        if (request == null) {
            throw exception(PEER_GROUP_JOIN_REQUEST_NOT_FOUND);
        }
        if (request.getStatus() != 0) { // 0-待审核
            throw exception(PEER_GROUP_JOIN_REQUEST_ALREADY_PROCESSED);
        }

        // 2. 更新申请状态
        request.setStatus(approved ? 1 : 2); // 1-已通过 2-已拒绝
        request.setReviewedBy(reviewerId);
        request.setReviewedAt(LocalDateTime.now());
        if (!approved) {
            request.setRejectReason(rejectReason);
        }
        joinRequestMapper.updateById(request);

        // 3. 如果通过，添加成员
        if (approved) {
            doAddMember(request.getGroupId(), request.getUserId(),
                    PeerGroupMemberRoleEnum.MEMBER.getRole(), "self", null);
        }
    }

    // ========== 私有方法 ==========

    private PeerGroupDO validatePeerGroupExists(Long id) {
        PeerGroupDO peerGroup = peerGroupMapper.selectById(id);
        if (peerGroup == null) {
            throw exception(PEER_GROUP_NOT_EXISTS);
        }
        return peerGroup;
    }

    private void doAddMember(Long groupId, Long userId, String role, String joinSource, Long invitedBy) {
        // 1. 检查是否有旧的成员记录（可能是之前退出的）
        PeerGroupMemberDO existMember = peerGroupMemberMapper.selectByGroupIdAndUserId(groupId, userId);
        
        if (existMember != null) {
            // 更新旧记录
            existMember.setRole(role);
            existMember.setStatus(PeerGroupMemberStatusEnum.NORMAL.getStatus());
            existMember.setJoinSource(joinSource);
            existMember.setInvitedBy(invitedBy);
            existMember.setJoinedAt(LocalDateTime.now());
            existMember.setLeftAt(null);
            existMember.setNodebbSynced(false);
            peerGroupMemberMapper.updateById(existMember);
        } else {
            // 创建新记录
            PeerGroupMemberDO member = PeerGroupMemberDO.builder()
                    .groupId(groupId)
                    .userId(userId)
                    .role(role)
                    .status(PeerGroupMemberStatusEnum.NORMAL.getStatus())
                    .joinSource(joinSource)
                    .invitedBy(invitedBy)
                    .joinedAt(LocalDateTime.now())
                    .nodebbSynced(false)
                    .notifyEnabled(true)
                    .build();
            peerGroupMemberMapper.insert(member);
            existMember = member;
        }

        // 2. 增加成员计数
        peerGroupMapper.incrementMemberCount(groupId);

        // 3. 创建 Outbox 同步任务
        createSyncOutbox(NodebbSyncTypeEnum.MEMBER_JOIN.getType(), "CREATE",
                "PEER_GROUP_MEMBER", existMember.getId(), existMember);
    }

    private void createJoinRequest(Long groupId, Long userId, String reason) {
        // 检查是否已有待审核的申请
        PeerGroupJoinRequestDO existRequest = joinRequestMapper.selectPendingByGroupIdAndUserId(groupId, userId);
        if (existRequest != null) {
            throw exception(PEER_GROUP_JOIN_REQUEST_EXISTS);
        }

        // 创建申请
        PeerGroupJoinRequestDO request = PeerGroupJoinRequestDO.builder()
                .groupId(groupId)
                .userId(userId)
                .reason(reason)
                .status(0) // 待审核
                .build();
        joinRequestMapper.insert(request);
    }

    private void createSyncOutbox(String syncType, String operation, String bizType, Long bizId, Object payload) {
        NodebbSyncOutboxDO outbox = NodebbSyncOutboxDO.builder()
                .syncType(syncType)
                .operation(operation)
                .bizType(bizType)
                .bizId(bizId)
                .payload(JSONUtil.toJsonStr(payload))
                .status(NodebbSyncStatusEnum.PENDING.getStatus())
                .retryCount(0)
                .maxRetries(5)
                .build();
        outboxMapper.insert(outbox);
    }

}


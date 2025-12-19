package cn.iocoder.yudao.module.member.service.peergroup;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.member.controller.admin.peergroup.vo.PeerGroupCreateReqVO;
import cn.iocoder.yudao.module.member.controller.admin.peergroup.vo.PeerGroupPageReqVO;
import cn.iocoder.yudao.module.member.controller.admin.peergroup.vo.PeerGroupUpdateReqVO;
import cn.iocoder.yudao.module.member.controller.app.peergroup.vo.AppPeerGroupJoinReqVO;
import cn.iocoder.yudao.module.member.dal.dataobject.peergroup.PeerGroupDO;
import cn.iocoder.yudao.module.member.dal.dataobject.peergroup.PeerGroupMemberDO;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 同行小组 Service 接口
 *
 * @author 芋道源码
 */
public interface PeerGroupService {

    // ========== 管理后台：小组管理 ==========

    /**
     * 创建同行小组
     *
     * @param createReqVO 创建信息
     * @return 小组编号
     */
    Long createPeerGroup(@Valid PeerGroupCreateReqVO createReqVO);

    /**
     * 更新同行小组
     *
     * @param updateReqVO 更新信息
     */
    void updatePeerGroup(@Valid PeerGroupUpdateReqVO updateReqVO);

    /**
     * 删除同行小组
     *
     * @param id 小组编号
     */
    void deletePeerGroup(Long id);

    /**
     * 获得同行小组
     *
     * @param id 小组编号
     * @return 同行小组
     */
    PeerGroupDO getPeerGroup(Long id);

    /**
     * 获得同行小组分页
     *
     * @param pageReqVO 分页查询
     * @return 同行小组分页
     */
    PageResult<PeerGroupDO> getPeerGroupPage(PeerGroupPageReqVO pageReqVO);

    /**
     * 获得启用状态的同行小组列表
     *
     * @return 同行小组列表
     */
    List<PeerGroupDO> getEnabledPeerGroupList();

    /**
     * 按分类获取小组列表
     *
     * @param category 分类
     * @return 同行小组列表
     */
    List<PeerGroupDO> getPeerGroupListByCategory(String category);

    // ========== 小程序端：用户操作 ==========

    /**
     * 获取用户已加入的小组列表
     *
     * @param userId 用户ID
     * @return 小组列表
     */
    List<PeerGroupDO> getMyJoinedGroups(Long userId);

    /**
     * 获取用户在小组中的成员信息
     *
     * @param groupId 小组ID
     * @param userId  用户ID
     * @return 成员信息，未加入返回 null
     */
    PeerGroupMemberDO getMembership(Long groupId, Long userId);

    /**
     * 用户加入小组
     *
     * @param userId    用户ID
     * @param joinReqVO 加入请求
     * @return 是否直接加入成功（审核模式返回 false，表示进入待审核状态）
     */
    boolean joinGroup(Long userId, @Valid AppPeerGroupJoinReqVO joinReqVO);

    /**
     * 用户退出小组
     *
     * @param userId  用户ID
     * @param groupId 小组ID
     */
    void leaveGroup(Long userId, Long groupId);

    /**
     * 获取推荐小组列表（用户未加入的公开小组）
     *
     * @param userId   用户ID
     * @param category 分类（可选）
     * @param limit    数量限制
     * @return 推荐小组列表
     */
    List<PeerGroupDO> getRecommendGroups(Long userId, String category, int limit);

    // ========== 管理后台：成员管理 ==========

    /**
     * 添加成员（管理员操作）
     *
     * @param groupId 小组ID
     * @param userId  用户ID
     * @param role    角色
     */
    void addMember(Long groupId, Long userId, String role);

    /**
     * 移除成员（管理员操作）
     *
     * @param groupId 小组ID
     * @param userId  用户ID
     */
    void removeMember(Long groupId, Long userId);

    /**
     * 更新成员角色（管理员操作）
     *
     * @param groupId 小组ID
     * @param userId  用户ID
     * @param role    新角色
     */
    void updateMemberRole(Long groupId, Long userId, String role);

    /**
     * 审核加入申请
     *
     * @param requestId    申请ID
     * @param approved     是否通过
     * @param rejectReason 拒绝原因
     * @param reviewerId   审核人ID
     */
    void reviewJoinRequest(Long requestId, boolean approved, String rejectReason, Long reviewerId);

}


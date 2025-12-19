package cn.iocoder.yudao.module.member.controller.app.peergroup;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.member.controller.app.peergroup.vo.*;
import cn.iocoder.yudao.module.member.convert.peergroup.PeerGroupConvert;
import cn.iocoder.yudao.module.member.dal.dataobject.peergroup.PeerGroupDO;
import cn.iocoder.yudao.module.member.dal.dataobject.peergroup.PeerGroupMemberDO;
import cn.iocoder.yudao.module.member.service.peergroup.PeerGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "小程序 - 同行小组")
@RestController
@RequestMapping("/member/peer-group")
@Validated
public class AppPeerGroupController {

    @Resource
    private PeerGroupService peerGroupService;

    // ========== 我的小组 ==========

    @GetMapping("/my-groups")
    @Operation(summary = "获取我加入的小组列表")
    public CommonResult<List<AppPeerGroupRespVO>> getMyGroups() {
        Long userId = getLoginUserId();
        List<PeerGroupDO> groups = peerGroupService.getMyJoinedGroups(userId);
        
        // 转换并标记已加入
        List<AppPeerGroupRespVO> result = groups.stream().map(g -> {
            AppPeerGroupRespVO vo = PeerGroupConvert.INSTANCE.convertToApp(g);
            vo.setJoined(true);
            // TODO: 根据实际活动设置 lastActivity
            vo.setLastActivity("刚刚有新消息");
            return vo;
        }).collect(Collectors.toList());
        
        return success(result);
    }

    // ========== 推荐小组 ==========

    @GetMapping("/recommend")
    @Operation(summary = "获取推荐小组列表")
    public CommonResult<List<AppPeerGroupRespVO>> getRecommendGroups(
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "limit", defaultValue = "10") Integer limit) {
        Long userId = getLoginUserId();
        List<PeerGroupDO> groups = peerGroupService.getRecommendGroups(userId, category, limit);
        
        // 转换并标记未加入
        List<AppPeerGroupRespVO> result = groups.stream().map(g -> {
            AppPeerGroupRespVO vo = PeerGroupConvert.INSTANCE.convertToApp(g);
            vo.setJoined(false);
            return vo;
        }).collect(Collectors.toList());
        
        return success(result);
    }

    // ========== 全部小组（按分类） ==========

    @GetMapping("/list")
    @Operation(summary = "获取小组列表（按分类）")
    public CommonResult<List<AppPeerGroupRespVO>> getGroupList(
            @RequestParam(value = "category", required = false) String category) {
        Long userId = getLoginUserId();
        
        // 获取用户已加入的小组ID
        List<PeerGroupDO> myGroups = peerGroupService.getMyJoinedGroups(userId);
        Set<Long> joinedIds = myGroups.stream().map(PeerGroupDO::getId).collect(Collectors.toSet());
        
        // 获取指定分类的小组
        List<PeerGroupDO> groups;
        if (category != null && !category.isEmpty() && !"all".equals(category)) {
            groups = peerGroupService.getPeerGroupListByCategory(category);
        } else {
            groups = peerGroupService.getEnabledPeerGroupList();
        }
        
        // 转换并标记加入状态
        List<AppPeerGroupRespVO> result = groups.stream().map(g -> {
            AppPeerGroupRespVO vo = PeerGroupConvert.INSTANCE.convertToApp(g);
            vo.setJoined(joinedIds.contains(g.getId()));
            return vo;
        }).collect(Collectors.toList());
        
        return success(result);
    }

    // ========== 小组详情 ==========

    @GetMapping("/detail")
    @Operation(summary = "获取小组详情")
    @Parameter(name = "id", description = "小组ID", required = true)
    public CommonResult<AppPeerGroupDetailRespVO> getGroupDetail(@RequestParam("id") Long id) {
        Long userId = getLoginUserId();
        
        // 获取小组信息
        PeerGroupDO group = peerGroupService.getPeerGroup(id);
        if (group == null) {
            return success(null);
        }
        
        // 转换
        AppPeerGroupDetailRespVO result = PeerGroupConvert.INSTANCE.convertToAppDetail(group);
        
        // 获取用户成员信息
        PeerGroupMemberDO membership = peerGroupService.getMembership(id, userId);
        if (membership != null && (membership.getStatus() == 0 || membership.getStatus() == 1)) {
            result.setJoined(true);
            result.setMyRole(membership.getRole());
        } else {
            result.setJoined(false);
            result.setMyRole(null);
        }
        
        return success(result);
    }

    // ========== 加入/退出小组 ==========

    @PostMapping("/join")
    @Operation(summary = "加入小组")
    public CommonResult<Boolean> joinGroup(@Valid @RequestBody AppPeerGroupJoinReqVO joinReqVO) {
        Long userId = getLoginUserId();
        boolean directJoined = peerGroupService.joinGroup(userId, joinReqVO);
        return success(directJoined);
    }

    @PostMapping("/leave")
    @Operation(summary = "退出小组")
    @Parameter(name = "groupId", description = "小组ID", required = true)
    public CommonResult<Boolean> leaveGroup(@RequestParam("groupId") Long groupId) {
        Long userId = getLoginUserId();
        peerGroupService.leaveGroup(userId, groupId);
        return success(true);
    }

}


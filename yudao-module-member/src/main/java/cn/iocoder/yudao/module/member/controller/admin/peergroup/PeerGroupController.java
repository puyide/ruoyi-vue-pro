package cn.iocoder.yudao.module.member.controller.admin.peergroup;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.member.controller.admin.peergroup.vo.*;
import cn.iocoder.yudao.module.member.convert.peergroup.PeerGroupConvert;
import cn.iocoder.yudao.module.member.dal.dataobject.peergroup.PeerGroupDO;
import cn.iocoder.yudao.module.member.service.peergroup.PeerGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 同行小组")
@RestController
@RequestMapping("/member/peer-group")
@Validated
public class PeerGroupController {

    @Resource
    private PeerGroupService peerGroupService;

    @PostMapping("/create")
    @Operation(summary = "创建同行小组")
    @PreAuthorize("@ss.hasPermission('member:peer-group:create')")
    public CommonResult<Long> createPeerGroup(@Valid @RequestBody PeerGroupCreateReqVO createReqVO) {
        return success(peerGroupService.createPeerGroup(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新同行小组")
    @PreAuthorize("@ss.hasPermission('member:peer-group:update')")
    public CommonResult<Boolean> updatePeerGroup(@Valid @RequestBody PeerGroupUpdateReqVO updateReqVO) {
        peerGroupService.updatePeerGroup(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除同行小组")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('member:peer-group:delete')")
    public CommonResult<Boolean> deletePeerGroup(@RequestParam("id") Long id) {
        peerGroupService.deletePeerGroup(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得同行小组")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('member:peer-group:query')")
    public CommonResult<PeerGroupRespVO> getPeerGroup(@RequestParam("id") Long id) {
        PeerGroupDO peerGroup = peerGroupService.getPeerGroup(id);
        return success(PeerGroupConvert.INSTANCE.convert(peerGroup));
    }

    @GetMapping("/page")
    @Operation(summary = "获得同行小组分页")
    @PreAuthorize("@ss.hasPermission('member:peer-group:query')")
    public CommonResult<PageResult<PeerGroupRespVO>> getPeerGroupPage(@Valid PeerGroupPageReqVO pageVO) {
        PageResult<PeerGroupDO> pageResult = peerGroupService.getPeerGroupPage(pageVO);
        return success(PeerGroupConvert.INSTANCE.convertPage(pageResult));
    }

    @GetMapping("/list-all-simple")
    @Operation(summary = "获取同行小组精简列表", description = "用于下拉选择")
    public CommonResult<List<PeerGroupRespVO>> getSimplePeerGroupList() {
        List<PeerGroupDO> list = peerGroupService.getEnabledPeerGroupList();
        return success(PeerGroupConvert.INSTANCE.convertList(list));
    }

    // ========== 成员管理 ==========

    @PostMapping("/member/add")
    @Operation(summary = "添加成员")
    @PreAuthorize("@ss.hasPermission('member:peer-group:update')")
    public CommonResult<Boolean> addMember(@RequestParam("groupId") Long groupId,
                                           @RequestParam("userId") Long userId,
                                           @RequestParam(value = "role", defaultValue = "member") String role) {
        peerGroupService.addMember(groupId, userId, role);
        return success(true);
    }

    @DeleteMapping("/member/remove")
    @Operation(summary = "移除成员")
    @PreAuthorize("@ss.hasPermission('member:peer-group:update')")
    public CommonResult<Boolean> removeMember(@RequestParam("groupId") Long groupId,
                                              @RequestParam("userId") Long userId) {
        peerGroupService.removeMember(groupId, userId);
        return success(true);
    }

    @PutMapping("/member/role")
    @Operation(summary = "更新成员角色")
    @PreAuthorize("@ss.hasPermission('member:peer-group:update')")
    public CommonResult<Boolean> updateMemberRole(@RequestParam("groupId") Long groupId,
                                                   @RequestParam("userId") Long userId,
                                                   @RequestParam("role") String role) {
        peerGroupService.updateMemberRole(groupId, userId, role);
        return success(true);
    }

    // ========== 加入申请审核 ==========

    @PutMapping("/join-request/review")
    @Operation(summary = "审核加入申请")
    @PreAuthorize("@ss.hasPermission('member:peer-group:update')")
    public CommonResult<Boolean> reviewJoinRequest(@RequestParam("requestId") Long requestId,
                                                    @RequestParam("approved") Boolean approved,
                                                    @RequestParam(value = "rejectReason", required = false) String rejectReason) {
        // TODO: 获取当前登录用户ID作为审核人
        Long reviewerId = 1L;
        peerGroupService.reviewJoinRequest(requestId, approved, rejectReason, reviewerId);
        return success(true);
    }

}


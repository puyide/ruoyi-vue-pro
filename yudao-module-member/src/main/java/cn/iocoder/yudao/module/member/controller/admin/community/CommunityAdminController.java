package cn.iocoder.yudao.module.member.controller.admin.community;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.member.controller.admin.community.vo.*;
import cn.iocoder.yudao.module.member.service.community.CommunityAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 社区内容审核
 */
@Tag(name = "管理后台 - 社区内容审核")
@RestController
@RequestMapping("/member/community-admin")
@Validated
public class CommunityAdminController {

    @Resource
    private CommunityAdminService communityAdminService;

    // ==================== 帖子管理 ====================

    @GetMapping("/topic/page")
    @Operation(summary = "获取帖子分页列表")
    @PreAuthorize("@ss.hasPermission('member:community:query')")
    public CommonResult<PageResult<AdminCommunityTopicRespVO>> getTopicPage(@Valid AdminCommunityTopicPageReqVO reqVO) {
        return success(communityAdminService.getTopicPage(reqVO));
    }

    @GetMapping("/topic/get")
    @Operation(summary = "获取帖子详情")
    @Parameter(name = "tid", description = "帖子ID", required = true, example = "123")
    @PreAuthorize("@ss.hasPermission('member:community:query')")
    public CommonResult<AdminCommunityTopicRespVO> getTopicDetail(@RequestParam("tid") Integer tid) {
        return success(communityAdminService.getTopicDetail(tid));
    }

    @PutMapping("/topic/audit")
    @Operation(summary = "审核帖子")
    @PreAuthorize("@ss.hasPermission('member:community:audit')")
    public CommonResult<Boolean> auditTopic(@Valid @RequestBody AdminCommunityAuditReqVO reqVO) {
        communityAdminService.auditTopic(reqVO);
        return success(true);
    }

    @PutMapping("/topic/update-status")
    @Operation(summary = "更新帖子状态（置顶/锁定）")
    @PreAuthorize("@ss.hasPermission('member:community:update')")
    public CommonResult<Boolean> updateTopicStatus(@Valid @RequestBody AdminCommunityTopicUpdateReqVO reqVO) {
        communityAdminService.updateTopicStatus(reqVO);
        return success(true);
    }

    @DeleteMapping("/topic/delete")
    @Operation(summary = "删除帖子")
    @Parameter(name = "tid", description = "帖子ID", required = true, example = "123")
    @PreAuthorize("@ss.hasPermission('member:community:delete')")
    public CommonResult<Boolean> deleteTopic(@RequestParam("tid") Integer tid) {
        communityAdminService.deleteTopic(tid);
        return success(true);
    }

    // ==================== 回帖管理 ====================

    @GetMapping("/post/page")
    @Operation(summary = "获取回帖分页列表")
    @PreAuthorize("@ss.hasPermission('member:community:query')")
    public CommonResult<PageResult<AdminCommunityPostRespVO>> getPostPage(@Valid AdminCommunityTopicPageReqVO reqVO) {
        return success(communityAdminService.getPostPage(reqVO));
    }

    @PutMapping("/post/audit")
    @Operation(summary = "审核回帖")
    @PreAuthorize("@ss.hasPermission('member:community:audit')")
    public CommonResult<Boolean> auditPost(@Valid @RequestBody AdminCommunityAuditReqVO reqVO) {
        communityAdminService.auditPost(reqVO);
        return success(true);
    }

    @DeleteMapping("/post/delete")
    @Operation(summary = "删除回帖")
    @Parameter(name = "pid", description = "回帖ID", required = true, example = "456")
    @PreAuthorize("@ss.hasPermission('member:community:delete')")
    public CommonResult<Boolean> deletePost(@RequestParam("pid") Integer pid) {
        communityAdminService.deletePost(pid);
        return success(true);
    }

    // ==================== 统计相关 ====================

    @GetMapping("/statistics/pending-count")
    @Operation(summary = "获取待审核数量")
    @PreAuthorize("@ss.hasPermission('member:community:query')")
    public CommonResult<PendingCountVO> getPendingCount() {
        PendingCountVO vo = new PendingCountVO();
        vo.setTopicCount(communityAdminService.getPendingTopicCount());
        vo.setPostCount(communityAdminService.getPendingPostCount());
        return success(vo);
    }

    /**
     * 待审核数量 VO
     */
    @lombok.Data
    public static class PendingCountVO {
        private Integer topicCount;
        private Integer postCount;
    }
}


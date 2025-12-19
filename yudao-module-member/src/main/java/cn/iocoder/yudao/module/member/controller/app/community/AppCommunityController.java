package cn.iocoder.yudao.module.member.controller.app.community;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

import cn.iocoder.yudao.module.member.controller.app.community.vo.*;
import cn.iocoder.yudao.module.member.service.community.CommunityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

/**
 * 用户 APP - 社区模块
 */
@Tag(name = "用户 APP - 社区模块")
@RestController
@RequestMapping("/member/community")
@Validated
public class AppCommunityController {

    @Resource
    private CommunityService communityService;

    // ==================== 分类相关 ====================

    @GetMapping("/categories")
    @Operation(summary = "获取社区分类列表")
    public CommonResult<List<AppCommunityCategoryRespVO>> getCategories() {
        return success(communityService.getCategories());
    }

    // ==================== 帖子相关 ====================

    @GetMapping("/topics")
    @Operation(summary = "获取帖子列表")
    public CommonResult<PageResult<AppCommunityTopicRespVO>> getTopicPage(@Valid AppCommunityTopicPageReqVO reqVO) {
        return success(communityService.getTopicPage(reqVO, getLoginUserId()));
    }

    @GetMapping("/topics/{tid}")
    @Operation(summary = "获取帖子详情")
    @Parameter(name = "tid", description = "帖子ID", required = true, example = "123")
    public CommonResult<AppCommunityTopicDetailRespVO> getTopicDetail(@PathVariable("tid") Integer tid) {
        return success(communityService.getTopicDetail(tid, getLoginUserId()));
    }

    @GetMapping("/topics/{tid}/posts")
    @Operation(summary = "获取帖子回帖列表")
    @Parameter(name = "tid", description = "帖子ID", required = true, example = "123")
    public CommonResult<PageResult<AppCommunityPostRespVO>> getTopicPosts(
            @PathVariable("tid") Integer tid,
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return success(communityService.getTopicPosts(tid, pageNo, pageSize, getLoginUserId()));
    }

    @PostMapping("/topics")
    @Operation(summary = "创建帖子")
    public CommonResult<Integer> createTopic(@Valid @RequestBody AppCommunityTopicCreateReqVO reqVO) {
        return success(communityService.createTopic(reqVO, getLoginUserId()));
    }

    @PostMapping("/topics/{tid}/reply")
    @Operation(summary = "创建回帖")
    @Parameter(name = "tid", description = "帖子ID", required = true, example = "123")
    public CommonResult<Integer> createReply(
            @PathVariable("tid") Integer tid,
            @Valid @RequestBody AppCommunityReplyCreateReqVO reqVO) {
        return success(communityService.createReply(tid, reqVO, getLoginUserId()));
    }

    // ==================== 互动相关 ====================

    @PostMapping("/posts/{pid}/like")
    @Operation(summary = "点赞帖子/回帖")
    @Parameter(name = "pid", description = "楼层ID", required = true, example = "456")
    public CommonResult<Boolean> likePost(@PathVariable("pid") Integer pid) {
        communityService.likePost(pid, getLoginUserId());
        return success(true);
    }

    @DeleteMapping("/posts/{pid}/like")
    @Operation(summary = "取消点赞")
    @Parameter(name = "pid", description = "楼层ID", required = true, example = "456")
    public CommonResult<Boolean> unlikePost(@PathVariable("pid") Integer pid) {
        communityService.unlikePost(pid, getLoginUserId());
        return success(true);
    }

    @PostMapping("/topics/{tid}/favorite")
    @Operation(summary = "收藏帖子")
    @Parameter(name = "tid", description = "帖子ID", required = true, example = "123")
    public CommonResult<Boolean> favoriteTopic(@PathVariable("tid") Integer tid) {
        communityService.favoriteTopic(tid, getLoginUserId());
        return success(true);
    }

    @DeleteMapping("/topics/{tid}/favorite")
    @Operation(summary = "取消收藏")
    @Parameter(name = "tid", description = "帖子ID", required = true, example = "123")
    public CommonResult<Boolean> unfavoriteTopic(@PathVariable("tid") Integer tid) {
        communityService.unfavoriteTopic(tid, getLoginUserId());
        return success(true);
    }

    @PostMapping("/topics/{tid}/follow")
    @Operation(summary = "关注帖子")
    @Parameter(name = "tid", description = "帖子ID", required = true, example = "123")
    public CommonResult<Boolean> followTopic(@PathVariable("tid") Integer tid) {
        communityService.followTopic(tid, getLoginUserId());
        return success(true);
    }

    @DeleteMapping("/topics/{tid}/follow")
    @Operation(summary = "取消关注")
    @Parameter(name = "tid", description = "帖子ID", required = true, example = "123")
    public CommonResult<Boolean> unfollowTopic(@PathVariable("tid") Integer tid) {
        communityService.unfollowTopic(tid, getLoginUserId());
        return success(true);
    }

    @PostMapping("/topics/{tid}/relate")
    @Operation(summary = "\"我也遇到过\"共情互动")
    @Parameter(name = "tid", description = "帖子ID", required = true, example = "123")
    public CommonResult<Boolean> relateTopic(@PathVariable("tid") Integer tid) {
        communityService.relateTopic(tid, getLoginUserId());
        return success(true);
    }

    // ==================== 我的相关 ====================

    @GetMapping("/me/topics")
    @Operation(summary = "获取我发布的帖子列表")
    public CommonResult<PageResult<AppCommunityTopicRespVO>> getMyTopics(
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return success(communityService.getMyTopics(pageNo, pageSize, getLoginUserId()));
    }

    @GetMapping("/me/replies")
    @Operation(summary = "获取我的回复列表")
    public CommonResult<PageResult<AppCommunityPostRespVO>> getMyReplies(
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return success(communityService.getMyReplies(pageNo, pageSize, getLoginUserId()));
    }

    @GetMapping("/me/favorites")
    @Operation(summary = "获取我收藏的帖子列表")
    public CommonResult<PageResult<AppCommunityTopicRespVO>> getMyFavorites(
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return success(communityService.getMyFavorites(pageNo, pageSize, getLoginUserId()));
    }

    // ==================== 通知相关 ====================

    @GetMapping("/notifications/unread-count")
    @Operation(summary = "获取未读通知数量")
    public CommonResult<Integer> getUnreadNotificationCount() {
        return success(communityService.getUnreadNotificationCount(getLoginUserId()));
    }

    @GetMapping("/notifications")
    @Operation(summary = "获取通知列表")
    public CommonResult<PageResult<AppCommunityNotificationRespVO>> getNotifications(
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return success(communityService.getNotifications(pageNo, pageSize, getLoginUserId()));
    }
}

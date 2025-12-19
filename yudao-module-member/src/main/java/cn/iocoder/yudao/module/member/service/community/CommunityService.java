package cn.iocoder.yudao.module.member.service.community;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.member.controller.app.community.vo.*;

import java.util.List;

/**
 * 社区服务接口
 */
public interface CommunityService {

    // ==================== 分类相关 ====================

    /**
     * 获取社区分类列表
     *
     * @return 分类列表
     */
    List<AppCommunityCategoryRespVO> getCategories();

    // ==================== 帖子相关 ====================

    /**
     * 获取帖子列表（分页）
     *
     * @param reqVO  分页请求
     * @param userId 当前用户ID（可选，用于判断是否已点赞/收藏）
     * @return 帖子分页结果
     */
    PageResult<AppCommunityTopicRespVO> getTopicPage(AppCommunityTopicPageReqVO reqVO, Long userId);

    /**
     * 获取帖子详情
     *
     * @param tid    帖子ID
     * @param userId 当前用户ID（可选）
     * @return 帖子详情
     */
    AppCommunityTopicDetailRespVO getTopicDetail(Integer tid, Long userId);

    /**
     * 获取帖子的回帖列表（分页）
     *
     * @param tid      帖子ID
     * @param pageNo   页码
     * @param pageSize 每页数量
     * @param userId   当前用户ID（可选）
     * @return 回帖列表
     */
    PageResult<AppCommunityPostRespVO> getTopicPosts(Integer tid, Integer pageNo, Integer pageSize, Long userId);

    /**
     * 创建帖子
     *
     * @param reqVO  创建请求
     * @param userId 当前用户ID
     * @return 新帖子ID
     */
    Integer createTopic(AppCommunityTopicCreateReqVO reqVO, Long userId);

    /**
     * 创建回帖
     *
     * @param tid    帖子ID
     * @param reqVO  创建请求
     * @param userId 当前用户ID
     * @return 新回帖ID
     */
    Integer createReply(Integer tid, AppCommunityReplyCreateReqVO reqVO, Long userId);

    // ==================== 互动相关 ====================

    /**
     * 点赞帖子/回帖
     *
     * @param pid    楼层ID
     * @param userId 当前用户ID
     */
    void likePost(Integer pid, Long userId);

    /**
     * 取消点赞
     *
     * @param pid    楼层ID
     * @param userId 当前用户ID
     */
    void unlikePost(Integer pid, Long userId);

    /**
     * 收藏帖子
     *
     * @param tid    帖子ID
     * @param userId 当前用户ID
     */
    void favoriteTopic(Integer tid, Long userId);

    /**
     * 取消收藏
     *
     * @param tid    帖子ID
     * @param userId 当前用户ID
     */
    void unfavoriteTopic(Integer tid, Long userId);

    /**
     * 关注帖子
     *
     * @param tid    帖子ID
     * @param userId 当前用户ID
     */
    void followTopic(Integer tid, Long userId);

    /**
     * 取消关注帖子
     *
     * @param tid    帖子ID
     * @param userId 当前用户ID
     */
    void unfollowTopic(Integer tid, Long userId);

    /**
     * "我也遇到过" - 特殊的共情互动
     *
     * @param tid    帖子ID
     * @param userId 当前用户ID
     */
    void relateTopic(Integer tid, Long userId);

    // ==================== 我的相关 ====================

    /**
     * 获取我发布的帖子列表
     *
     * @param pageNo   页码
     * @param pageSize 每页数量
     * @param userId   当前用户ID
     * @return 帖子列表
     */
    PageResult<AppCommunityTopicRespVO> getMyTopics(Integer pageNo, Integer pageSize, Long userId);

    /**
     * 获取我的回复列表
     *
     * @param pageNo   页码
     * @param pageSize 每页数量
     * @param userId   当前用户ID
     * @return 回复列表
     */
    PageResult<AppCommunityPostRespVO> getMyReplies(Integer pageNo, Integer pageSize, Long userId);

    /**
     * 获取我收藏的帖子列表
     *
     * @param pageNo   页码
     * @param pageSize 每页数量
     * @param userId   当前用户ID
     * @return 帖子列表
     */
    PageResult<AppCommunityTopicRespVO> getMyFavorites(Integer pageNo, Integer pageSize, Long userId);

    // ==================== 通知相关 ====================

    /**
     * 获取未读通知数量
     *
     * @param userId 当前用户ID
     * @return 未读数量
     */
    Integer getUnreadNotificationCount(Long userId);

    /**
     * 获取通知列表
     *
     * @param pageNo   页码
     * @param pageSize 每页数量
     * @param userId   当前用户ID
     * @return 通知列表
     */
    PageResult<AppCommunityNotificationRespVO> getNotifications(Integer pageNo, Integer pageSize, Long userId);
}


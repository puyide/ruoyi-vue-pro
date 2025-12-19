package cn.iocoder.yudao.module.member.service.community;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.member.controller.admin.community.vo.*;

/**
 * 社区管理后台服务接口
 */
public interface CommunityAdminService {

    // ==================== 帖子管理 ====================

    /**
     * 获取帖子分页列表
     *
     * @param reqVO 分页请求
     * @return 帖子分页结果
     */
    PageResult<AdminCommunityTopicRespVO> getTopicPage(AdminCommunityTopicPageReqVO reqVO);

    /**
     * 获取帖子详情
     *
     * @param tid 帖子ID
     * @return 帖子详情
     */
    AdminCommunityTopicRespVO getTopicDetail(Integer tid);

    /**
     * 审核帖子
     *
     * @param reqVO 审核请求
     */
    void auditTopic(AdminCommunityAuditReqVO reqVO);

    /**
     * 更新帖子状态（置顶/锁定）
     *
     * @param reqVO 更新请求
     */
    void updateTopicStatus(AdminCommunityTopicUpdateReqVO reqVO);

    /**
     * 删除帖子
     *
     * @param tid 帖子ID
     */
    void deleteTopic(Integer tid);

    // ==================== 回帖管理 ====================

    /**
     * 获取回帖分页列表
     *
     * @param reqVO 分页请求
     * @return 回帖分页结果
     */
    PageResult<AdminCommunityPostRespVO> getPostPage(AdminCommunityTopicPageReqVO reqVO);

    /**
     * 审核回帖
     *
     * @param reqVO 审核请求
     */
    void auditPost(AdminCommunityAuditReqVO reqVO);

    /**
     * 删除回帖
     *
     * @param pid 回帖ID
     */
    void deletePost(Integer pid);

    // ==================== 统计相关 ====================

    /**
     * 获取待审核帖子数量
     *
     * @return 待审核数量
     */
    Integer getPendingTopicCount();

    /**
     * 获取待审核回帖数量
     *
     * @return 待审核数量
     */
    Integer getPendingPostCount();
}


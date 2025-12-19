package cn.iocoder.yudao.module.member.service.community;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.member.controller.admin.community.vo.*;
import cn.iocoder.yudao.module.member.dal.dataobject.nodebb.MemberNodebbUserDO;
import cn.iocoder.yudao.module.member.dal.dataobject.user.MemberUserDO;
import cn.iocoder.yudao.module.member.dal.mysql.nodebb.MemberNodebbUserMapper;
import cn.iocoder.yudao.module.member.dal.mysql.user.MemberUserMapper;
import cn.iocoder.yudao.module.member.framework.nodebb.core.NodeBBCommunityClient;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.member.enums.ErrorCodeConstants.*;

/**
 * 社区管理后台服务实现
 * 
 * 注意：由于 NodeBB 本身没有"审核队列"概念，这里的实现有两种模式：
 * 1. 直接对接 NodeBB：使用 NodeBB 的帖子列表，管理员可以删除/锁定帖子
 * 2. 审核队列模式：需要在本地数据库建立审核队列表
 * 
 * 当前实现采用模式1，直接使用 NodeBB 的帖子管理功能
 */
@Service
@Slf4j
public class CommunityAdminServiceImpl implements CommunityAdminService {

    @Resource
    private NodeBBCommunityClient nodeBBClient;

    @Resource
    private MemberNodebbUserMapper memberNodebbUserMapper;

    @Resource
    private MemberUserMapper memberUserMapper;

    // ==================== 帖子管理 ====================

    @Override
    public PageResult<AdminCommunityTopicRespVO> getTopicPage(AdminCommunityTopicPageReqVO reqVO) {
        // 从 NodeBB 获取最新帖子列表
        JsonNode result = nodeBBClient.getRecentTopics(reqVO.getPageNo());
        
        if (result == null) {
            return PageResult.empty();
        }

        JsonNode topics = result.path("topics");
        List<AdminCommunityTopicRespVO> list = new ArrayList<>();
        
        if (topics.isArray()) {
            for (JsonNode topic : topics) {
                AdminCommunityTopicRespVO vo = convertAdminTopic(topic);
                if (vo != null) {
                    // 过滤条件
                    if (reqVO.getTid() != null && !reqVO.getTid().equals(vo.getTid())) {
                        continue;
                    }
                    if (StrUtil.isNotBlank(reqVO.getTitle()) && 
                            !vo.getTitle().contains(reqVO.getTitle())) {
                        continue;
                    }
                    if (reqVO.getCid() != null && !reqVO.getCid().equals(vo.getCid())) {
                        continue;
                    }
                    list.add(vo);
                }
            }
        }

        JsonNode pagination = result.path("pagination");
        long total = pagination.path("topicCount").asLong(list.size());

        return new PageResult<>(list, total);
    }

    @Override
    public AdminCommunityTopicRespVO getTopicDetail(Integer tid) {
        JsonNode result = nodeBBClient.getTopicDetail(tid, 1);
        if (result == null) {
            throw exception(COMMUNITY_TOPIC_NOT_EXISTS);
        }
        return convertAdminTopic(result);
    }

    @Override
    public void auditTopic(AdminCommunityAuditReqVO reqVO) {
        // NodeBB 没有原生审核功能，这里通过删除来实现"拒绝"
        if (reqVO.getAuditStatus() == 2) {
            // 拒绝 = 删除帖子
            deleteTopic(reqVO.getId());
        }
        // 通过 = 不做处理，保持帖子可见
        log.info("[auditTopic] 帖子审核完成: tid={}, status={}, remark={}", 
                reqVO.getId(), reqVO.getAuditStatus(), reqVO.getAuditRemark());
    }

    @Override
    public void updateTopicStatus(AdminCommunityTopicUpdateReqVO reqVO) {
        // 调用 NodeBB API 更新帖子状态
        // 注意：需要使用管理员 token
        log.info("[updateTopicStatus] 更新帖子状态: tid={}, pinned={}, locked={}", 
                reqVO.getTid(), reqVO.getPinned(), reqVO.getLocked());
        
        // TODO: 实现 NodeBB 置顶/锁定 API 调用
        // POST /api/v3/topics/{tid}/pin
        // PUT /api/v3/topics/{tid}/lock
    }

    @Override
    public void deleteTopic(Integer tid) {
        // 调用 NodeBB API 删除帖子
        // DELETE /api/v3/topics/{tid}
        log.info("[deleteTopic] 删除帖子: tid={}", tid);
        
        // TODO: 实现 NodeBB 删除帖子 API 调用
    }

    // ==================== 回帖管理 ====================

    @Override
    public PageResult<AdminCommunityPostRespVO> getPostPage(AdminCommunityTopicPageReqVO reqVO) {
        // 获取最近的回帖
        // NodeBB 的 /api/recent/posts 可以获取最新回帖
        List<AdminCommunityPostRespVO> list = new ArrayList<>();
        
        // 简单实现：获取最新帖子的回复
        JsonNode topics = nodeBBClient.getRecentTopics(1);
        if (topics != null && topics.path("topics").isArray()) {
            for (JsonNode topic : topics.path("topics")) {
                int tid = topic.path("tid").asInt();
                JsonNode topicDetail = nodeBBClient.getTopicDetail(tid, 1);
                if (topicDetail != null && topicDetail.path("posts").isArray()) {
                    for (JsonNode post : topicDetail.path("posts")) {
                        if (!post.path("isMainPost").asBoolean()) {
                            AdminCommunityPostRespVO vo = convertAdminPost(post, topic.path("title").asText());
                            if (vo != null) {
                                list.add(vo);
                            }
                        }
                    }
                }
                // 只获取前几个帖子的回复
                if (list.size() >= reqVO.getPageSize()) {
                    break;
                }
            }
        }

        return new PageResult<>(list, (long) list.size());
    }

    @Override
    public void auditPost(AdminCommunityAuditReqVO reqVO) {
        if (reqVO.getAuditStatus() == 2) {
            // 拒绝 = 删除回帖
            deletePost(reqVO.getId());
        }
        log.info("[auditPost] 回帖审核完成: pid={}, status={}", reqVO.getId(), reqVO.getAuditStatus());
    }

    @Override
    public void deletePost(Integer pid) {
        // 调用 NodeBB API 删除回帖
        // DELETE /api/v3/posts/{pid}
        log.info("[deletePost] 删除回帖: pid={}", pid);
        
        // TODO: 实现 NodeBB 删除回帖 API 调用
    }

    // ==================== 统计相关 ====================

    @Override
    public Integer getPendingTopicCount() {
        // NodeBB 没有审核队列，返回 0
        // 如果需要审核功能，需要在本地数据库实现
        return 0;
    }

    @Override
    public Integer getPendingPostCount() {
        return 0;
    }

    // ==================== 内部方法 ====================

    private AdminCommunityTopicRespVO convertAdminTopic(JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }

        AdminCommunityTopicRespVO vo = new AdminCommunityTopicRespVO();
        vo.setTid(node.path("tid").asInt());
        vo.setTitle(node.path("title").asText());
        vo.setCid(node.path("cid").asInt());
        vo.setCategoryName(node.path("category").path("name").asText());
        
        // 获取完整内容
        JsonNode posts = node.path("posts");
        if (posts.isArray() && posts.size() > 0) {
            vo.setContent(posts.get(0).path("content").asText());
        }

        // 标签
        JsonNode tagsNode = node.path("tags");
        if (tagsNode.isArray()) {
            List<String> tags = new ArrayList<>();
            for (JsonNode tag : tagsNode) {
                tags.add(tag.path("value").asText());
            }
            vo.setTags(tags);
        }

        // 作者信息
        AdminCommunityTopicRespVO.AuthorInfo author = new AdminCommunityTopicRespVO.AuthorInfo();
        JsonNode userNode = node.path("user");
        int nodebbUid = userNode.path("uid").asInt();
        author.setNodebbUid(nodebbUid);
        author.setNickname(StrUtil.blankToDefault(
                userNode.path("displayname").asText(), 
                userNode.path("username").asText()
        ));
        author.setAvatar(userNode.path("picture").asText());
        author.setAnonymous(false);

        // 查找本地用户信息
        MemberNodebbUserDO nodeBBUser = memberNodebbUserMapper.selectByNodebbUid(nodebbUid);
        if (nodeBBUser != null) {
            author.setUserId(nodeBBUser.getUserId());
            MemberUserDO memberUser = memberUserMapper.selectById(nodeBBUser.getUserId());
            if (memberUser != null) {
                author.setMobile(memberUser.getMobile());
            }
        }
        vo.setAuthor(author);

        // 统计数据
        vo.setPostCount(node.path("postcount").asInt());
        vo.setViewCount(node.path("viewcount").asInt());
        vo.setLikeCount(node.path("upvotes").asInt());

        // 状态
        vo.setPinned(node.path("pinned").asBoolean());
        vo.setLocked(node.path("locked").asBoolean());
        vo.setDeleted(node.path("deleted").asBoolean());

        // 审核状态（NodeBB 没有原生审核，默认已通过）
        vo.setAuditStatus(1);

        // 时间
        long timestamp = node.path("timestamp").asLong();
        if (timestamp > 0) {
            vo.setCreateTime(LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(timestamp), ZoneId.systemDefault()));
        }

        return vo;
    }

    private AdminCommunityPostRespVO convertAdminPost(JsonNode node, String topicTitle) {
        if (node == null || node.isNull()) {
            return null;
        }

        AdminCommunityPostRespVO vo = new AdminCommunityPostRespVO();
        vo.setPid(node.path("pid").asInt());
        vo.setTid(node.path("tid").asInt());
        vo.setTopicTitle(topicTitle);
        vo.setIndex(node.path("index").asInt());
        vo.setContent(node.path("content").asText());
        vo.setLikeCount(node.path("upvotes").asInt());
        vo.setDeleted(node.path("deleted").asBoolean());
        vo.setAuditStatus(1); // 默认已通过

        // 作者信息
        AdminCommunityTopicRespVO.AuthorInfo author = new AdminCommunityTopicRespVO.AuthorInfo();
        JsonNode userNode = node.path("user");
        int nodebbUid = userNode.path("uid").asInt();
        author.setNodebbUid(nodebbUid);
        author.setNickname(StrUtil.blankToDefault(
                userNode.path("displayname").asText(),
                userNode.path("username").asText()
        ));
        author.setAvatar(userNode.path("picture").asText());

        MemberNodebbUserDO nodeBBUser = memberNodebbUserMapper.selectByNodebbUid(nodebbUid);
        if (nodeBBUser != null) {
            author.setUserId(nodeBBUser.getUserId());
            MemberUserDO memberUser = memberUserMapper.selectById(nodeBBUser.getUserId());
            if (memberUser != null) {
                author.setMobile(memberUser.getMobile());
            }
        }
        vo.setAuthor(author);

        // 时间
        long timestamp = node.path("timestamp").asLong();
        if (timestamp > 0) {
            vo.setCreateTime(LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(timestamp), ZoneId.systemDefault()));
        }

        return vo;
    }
}


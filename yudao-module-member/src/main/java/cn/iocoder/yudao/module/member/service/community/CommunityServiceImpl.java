package cn.iocoder.yudao.module.member.service.community;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HtmlUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.member.controller.app.community.vo.*;
import cn.iocoder.yudao.module.member.dal.dataobject.nodebb.MemberNodebbUserDO;
import cn.iocoder.yudao.module.member.dal.dataobject.user.MemberUserDO;
import cn.iocoder.yudao.module.member.dal.mysql.nodebb.MemberNodebbUserMapper;
import cn.iocoder.yudao.module.member.dal.mysql.user.MemberUserMapper;
import cn.iocoder.yudao.module.member.framework.nodebb.core.NodeBBCommunityClient;
import cn.iocoder.yudao.module.member.service.nodebb.NodebbService;
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
 * 社区服务实现
 */
@Service
@Slf4j
public class CommunityServiceImpl implements CommunityService {

    @Resource
    private NodeBBCommunityClient nodeBBClient;

    @Resource
    private NodebbService nodeBBService;

    @Resource
    private ContentModerationService contentModerationService;

    @Resource
    private MemberNodebbUserMapper memberNodebbUserMapper;

    @Resource
    private MemberUserMapper memberUserMapper;

    // ==================== 分类相关 ====================

    @Override
    public List<AppCommunityCategoryRespVO> getCategories() {
        JsonNode result = nodeBBClient.getCategories();
        if (result == null) {
            return Collections.emptyList();
        }

        JsonNode categories = result.path("categories");
        if (!categories.isArray()) {
            return Collections.emptyList();
        }

        List<AppCommunityCategoryRespVO> list = new ArrayList<>();
        for (JsonNode cat : categories) {
            AppCommunityCategoryRespVO vo = convertCategory(cat);
            if (vo != null) {
                list.add(vo);
            }
        }
        return list;
    }

    // ==================== 帖子相关 ====================

    @Override
    public PageResult<AppCommunityTopicRespVO> getTopicPage(AppCommunityTopicPageReqVO reqVO, Long userId) {
        JsonNode result;
        int page = reqVO.getPageNo();

        // 根据筛选条件选择不同的 API
        if (reqVO.getCid() != null) {
            // 按分类筛选
            result = nodeBBClient.getCategoryTopics(reqVO.getCid(), page);
        } else if (StrUtil.isNotBlank(reqVO.getTag())) {
            // 按标签筛选
            result = nodeBBClient.getTagTopics(reqVO.getTag(), page);
        } else if (StrUtil.isNotBlank(reqVO.getKeyword())) {
            // 搜索
            result = nodeBBClient.search(reqVO.getKeyword(), page);
        } else {
            // 默认按排序获取
            String sort = StrUtil.blankToDefault(reqVO.getSort(), "recent");
            switch (sort) {
                case "popular":
                    result = nodeBBClient.getPopularTopics(page);
                    break;
                case "top":
                    result = nodeBBClient.getTopTopics(page);
                    break;
                default:
                    result = nodeBBClient.getRecentTopics(page);
            }
        }

        if (result == null) {
            return PageResult.empty();
        }

        // 解析帖子列表
        JsonNode topics = result.path("topics");
        if (!topics.isArray()) {
            topics = result.path("posts"); // 搜索结果可能在 posts 字段
        }

        List<AppCommunityTopicRespVO> list = new ArrayList<>();
        if (topics.isArray()) {
            for (JsonNode topic : topics) {
                AppCommunityTopicRespVO vo = convertTopic(topic, false);
                if (vo != null) {
                    list.add(vo);
                }
            }
        }

        // 获取分页信息
        JsonNode pagination = result.path("pagination");
        long total = pagination.path("topicCount").asLong(
                pagination.path("pageCount").asLong(1) * reqVO.getPageSize()
        );

        return new PageResult<>(list, total);
    }

    @Override
    public AppCommunityTopicDetailRespVO getTopicDetail(Integer tid, Long userId) {
        JsonNode result = nodeBBClient.getTopicDetail(tid, 1);
        if (result == null) {
            throw exception(COMMUNITY_TOPIC_NOT_EXISTS);
        }

        AppCommunityTopicDetailRespVO vo = new AppCommunityTopicDetailRespVO();
        // 复制基础信息
        copyTopicFields(result, vo, true);

        // 添加 AI 分析（如果有）
        // TODO: 这里可以调用 AI 服务生成摘要和分析
        vo.setAiAnalysis(null);

        // 获取相关帖子（简单实现：使用相同标签的帖子）
        vo.setRelatedTopics(getRelatedTopics(result));

        return vo;
    }

    @Override
    public PageResult<AppCommunityPostRespVO> getTopicPosts(Integer tid, Integer pageNo, Integer pageSize, Long userId) {
        JsonNode result = nodeBBClient.getTopicDetail(tid, pageNo);
        if (result == null) {
            return PageResult.empty();
        }

        JsonNode posts = result.path("posts");
        List<AppCommunityPostRespVO> list = new ArrayList<>();
        if (posts.isArray()) {
            for (JsonNode post : posts) {
                AppCommunityPostRespVO vo = convertPost(post);
                if (vo != null) {
                    list.add(vo);
                }
            }
        }

        JsonNode pagination = result.path("pagination");
        long total = pagination.path("postCount").asLong(list.size());

        return new PageResult<>(list, total);
    }

    @Override
    public Integer createTopic(AppCommunityTopicCreateReqVO reqVO, Long userId) {
        // 1. 内容审核
        ContentModerationService.ModerationResult moderationResult =
                contentModerationService.moderatePost(reqVO.getTitle(), reqVO.getContent());
        if (!moderationResult.isPassed()) {
            throw exception(COMMUNITY_CONTENT_MODERATION_FAILED, moderationResult.getReason());
        }

        // 2. 获取或创建 NodeBB 用户
        MemberNodebbUserDO nodeBBUser = nodeBBService.createUserIfAbsent(userId);
        if (nodeBBUser == null) {
            throw exception(COMMUNITY_NODEBB_USER_NOT_EXISTS);
        }

        // 3. 处理图片（将图片URL插入到内容末尾）
        String finalContent = contentModerationService.appendImagesToContent(
                moderationResult.getSanitizedContent(),
                reqVO.getImages()
        );

        // 4. 调用 NodeBB 创建帖子
        JsonNode result = nodeBBClient.createTopic(
                nodeBBUser.getNodebbUid(),
                reqVO.getCid(),
                reqVO.getTitle(),
                finalContent,
                reqVO.getTags()
        );

        if (result == null || !result.path("status").path("code").asText().equals("ok")) {
            String errorMsg = result != null ? result.path("status").path("message").asText() : "未知错误";
            log.error("[createTopic] NodeBB 创建帖子失败: {}", errorMsg);
            throw exception(COMMUNITY_TOPIC_CREATE_FAILED);
        }

        Integer tid = result.path("response").path("tid").asInt();
        log.info("[createTopic] 帖子创建成功: tid={}, userId={}", tid, userId);

        return tid;
    }

    @Override
    public Integer createReply(Integer tid, AppCommunityReplyCreateReqVO reqVO, Long userId) {
        // 1. 内容审核
        ContentModerationService.ModerationResult moderationResult =
                contentModerationService.moderateReply(reqVO.getContent());
        if (!moderationResult.isPassed()) {
            throw exception(COMMUNITY_CONTENT_MODERATION_FAILED, moderationResult.getReason());
        }

        // 2. 获取 NodeBB 用户
        MemberNodebbUserDO nodeBBUser = nodeBBService.createUserIfAbsent(userId);
        if (nodeBBUser == null) {
            throw exception(COMMUNITY_NODEBB_USER_NOT_EXISTS);
        }

        // 3. 处理图片
        String finalContent = contentModerationService.appendImagesToContent(
                moderationResult.getSanitizedContent(),
                reqVO.getImages()
        );

        // 4. 调用 NodeBB 创建回帖
        JsonNode result = nodeBBClient.createReply(
                nodeBBUser.getNodebbUid(),
                tid,
                finalContent,
                reqVO.getToPid()
        );

        if (result == null || !result.path("status").path("code").asText().equals("ok")) {
            String errorMsg = result != null ? result.path("status").path("message").asText() : "未知错误";
            log.error("[createReply] NodeBB 创建回帖失败: {}", errorMsg);
            throw exception(COMMUNITY_REPLY_CREATE_FAILED);
        }

        Integer pid = result.path("response").path("pid").asInt();
        log.info("[createReply] 回帖创建成功: pid={}, tid={}, userId={}", pid, tid, userId);

        return pid;
    }

    // ==================== 互动相关 ====================

    @Override
    public void likePost(Integer pid, Long userId) {
        MemberNodebbUserDO nodeBBUser = getNodeBBUserOrThrow(userId);
        JsonNode result = nodeBBClient.votePost(nodeBBUser.getNodebbUid(), pid, 1);
        checkResult(result, "点赞失败");
    }

    @Override
    public void unlikePost(Integer pid, Long userId) {
        MemberNodebbUserDO nodeBBUser = getNodeBBUserOrThrow(userId);
        JsonNode result = nodeBBClient.votePost(nodeBBUser.getNodebbUid(), pid, 0);
        checkResult(result, "取消点赞失败");
    }

    @Override
    public void favoriteTopic(Integer tid, Long userId) {
        MemberNodebbUserDO nodeBBUser = getNodeBBUserOrThrow(userId);
        // 获取帖子的主楼 pid
        JsonNode topicDetail = nodeBBClient.getTopicDetail(tid, 1);
        if (topicDetail == null) {
            throw exception(COMMUNITY_TOPIC_NOT_EXISTS);
        }
        int mainPid = topicDetail.path("mainPid").asInt();
        JsonNode result = nodeBBClient.bookmarkPost(nodeBBUser.getNodebbUid(), mainPid);
        checkResult(result, "收藏失败");
    }

    @Override
    public void unfavoriteTopic(Integer tid, Long userId) {
        MemberNodebbUserDO nodeBBUser = getNodeBBUserOrThrow(userId);
        JsonNode topicDetail = nodeBBClient.getTopicDetail(tid, 1);
        if (topicDetail == null) {
            throw exception(COMMUNITY_TOPIC_NOT_EXISTS);
        }
        int mainPid = topicDetail.path("mainPid").asInt();
        JsonNode result = nodeBBClient.unbookmarkPost(nodeBBUser.getNodebbUid(), mainPid);
        checkResult(result, "取消收藏失败");
    }

    @Override
    public void followTopic(Integer tid, Long userId) {
        MemberNodebbUserDO nodeBBUser = getNodeBBUserOrThrow(userId);
        JsonNode result = nodeBBClient.followTopic(nodeBBUser.getNodebbUid(), tid);
        checkResult(result, "关注失败");
    }

    @Override
    public void unfollowTopic(Integer tid, Long userId) {
        MemberNodebbUserDO nodeBBUser = getNodeBBUserOrThrow(userId);
        JsonNode result = nodeBBClient.unfollowTopic(nodeBBUser.getNodebbUid(), tid);
        checkResult(result, "取消关注失败");
    }

    @Override
    public void relateTopic(Integer tid, Long userId) {
        // "我也遇到过" 功能 - 可以实现为特殊的点赞
        // 这里简单地用点赞主楼来实现
        favoriteTopic(tid, userId);
    }

    // ==================== 我的相关 ====================

    @Override
    public PageResult<AppCommunityTopicRespVO> getMyTopics(Integer pageNo, Integer pageSize, Long userId) {
        MemberNodebbUserDO nodeBBUser = getNodeBBUserOrThrow(userId);
        JsonNode result = nodeBBClient.getUserTopics(nodeBBUser.getUsername(), pageNo);
        return parseTopicList(result);
    }

    @Override
    public PageResult<AppCommunityPostRespVO> getMyReplies(Integer pageNo, Integer pageSize, Long userId) {
        MemberNodebbUserDO nodeBBUser = getNodeBBUserOrThrow(userId);
        JsonNode result = nodeBBClient.getUserPosts(nodeBBUser.getUsername(), pageNo);

        if (result == null) {
            return PageResult.empty();
        }

        JsonNode posts = result.path("posts");
        List<AppCommunityPostRespVO> list = new ArrayList<>();
        if (posts.isArray()) {
            for (JsonNode post : posts) {
                AppCommunityPostRespVO vo = convertPost(post);
                if (vo != null) {
                    list.add(vo);
                }
            }
        }

        JsonNode pagination = result.path("pagination");
        long total = pagination.path("postCount").asLong(list.size());

        return new PageResult<>(list, total);
    }

    @Override
    public PageResult<AppCommunityTopicRespVO> getMyFavorites(Integer pageNo, Integer pageSize, Long userId) {
        MemberNodebbUserDO nodeBBUser = getNodeBBUserOrThrow(userId);
        JsonNode result = nodeBBClient.getUserBookmarks(nodeBBUser.getUsername(), pageNo);
        return parseTopicList(result);
    }

    // ==================== 通知相关 ====================

    @Override
    public Integer getUnreadNotificationCount(Long userId) {
        MemberNodebbUserDO nodeBBUser = memberNodebbUserMapper.selectByUserId(userId);
        if (nodeBBUser == null) {
            return 0;
        }

        JsonNode result = nodeBBClient.getNotifications(nodeBBUser.getNodebbUid());
        if (result == null) {
            return 0;
        }

        // 计算未读通知数量
        JsonNode notifications = result.path("notifications");
        int unreadCount = 0;
        if (notifications.isArray()) {
            for (JsonNode notification : notifications) {
                if (!notification.path("read").asBoolean()) {
                    unreadCount++;
                }
            }
        }
        return unreadCount;
    }

    @Override
    public PageResult<AppCommunityNotificationRespVO> getNotifications(Integer pageNo, Integer pageSize, Long userId) {
        MemberNodebbUserDO nodeBBUser = getNodeBBUserOrThrow(userId);
        JsonNode result = nodeBBClient.getNotifications(nodeBBUser.getNodebbUid());

        if (result == null) {
            return PageResult.empty();
        }

        JsonNode notifications = result.path("notifications");
        List<AppCommunityNotificationRespVO> list = new ArrayList<>();
        if (notifications.isArray()) {
            for (JsonNode notification : notifications) {
                AppCommunityNotificationRespVO vo = convertNotification(notification);
                if (vo != null) {
                    list.add(vo);
                }
            }
        }

        return new PageResult<>(list, (long) list.size());
    }

    // ==================== 内部方法 ====================

    private MemberNodebbUserDO getNodeBBUserOrThrow(Long userId) {
        MemberNodebbUserDO nodeBBUser = nodeBBService.createUserIfAbsent(userId);
        if (nodeBBUser == null) {
            throw exception(COMMUNITY_NODEBB_USER_NOT_EXISTS);
        }
        return nodeBBUser;
    }

    private void checkResult(JsonNode result, String errorMsg) {
        if (result == null || !result.path("status").path("code").asText().equals("ok")) {
            log.error("[Community] 操作失败: {}", errorMsg);
            throw exception(COMMUNITY_OPERATION_FAILED);
        }
    }

    private PageResult<AppCommunityTopicRespVO> parseTopicList(JsonNode result) {
        if (result == null) {
            return PageResult.empty();
        }

        JsonNode topics = result.path("topics");
        List<AppCommunityTopicRespVO> list = new ArrayList<>();
        if (topics.isArray()) {
            for (JsonNode topic : topics) {
                AppCommunityTopicRespVO vo = convertTopic(topic, false);
                if (vo != null) {
                    list.add(vo);
                }
            }
        }

        JsonNode pagination = result.path("pagination");
        long total = pagination.path("topicCount").asLong(list.size());

        return new PageResult<>(list, total);
    }

    private AppCommunityCategoryRespVO convertCategory(JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }

        AppCommunityCategoryRespVO vo = new AppCommunityCategoryRespVO();
        vo.setCid(node.path("cid").asInt());
        vo.setName(node.path("name").asText());
        vo.setDescription(node.path("description").asText());
        vo.setIcon(node.path("icon").asText());
        vo.setBgColor(node.path("bgColor").asText());
        vo.setTopicCount(node.path("totalTopicCount").asInt());
        vo.setOrder(node.path("order").asInt());

        // 处理子分类
        JsonNode children = node.path("children");
        if (children.isArray() && children.size() > 0) {
            List<AppCommunityCategoryRespVO> childList = new ArrayList<>();
            for (JsonNode child : children) {
                AppCommunityCategoryRespVO childVO = convertCategory(child);
                if (childVO != null) {
                    childList.add(childVO);
                }
            }
            vo.setChildren(childList);
        }

        return vo;
    }

    private AppCommunityTopicRespVO convertTopic(JsonNode node, boolean includeContent) {
        if (node == null || node.isNull()) {
            return null;
        }

        AppCommunityTopicRespVO vo = new AppCommunityTopicRespVO();
        vo.setTid(node.path("tid").asInt());
        vo.setTitle(node.path("title").asText());

        if (includeContent) {
            vo.setContent(node.path("posts").path(0).path("content").asText());
        }

        // 生成摘要
        String fullContent = node.path("posts").path(0).path("content").asText(
                node.path("teaser").path("content").asText("")
        );
        vo.setExcerpt(StrUtil.maxLength(HtmlUtil.cleanHtmlTag(fullContent), 100));

        // 作者信息
        AppCommunityTopicRespVO.AuthorInfo author = new AppCommunityTopicRespVO.AuthorInfo();
        JsonNode userNode = node.path("user");
        author.setUid((long) userNode.path("uid").asInt());
        author.setUsername(userNode.path("username").asText());
        author.setNickname(StrUtil.blankToDefault(userNode.path("displayname").asText(), userNode.path("username").asText()));
        author.setAvatar(userNode.path("picture").asText());
        author.setAnonymous(false); // TODO: 处理匿名
        vo.setAuthor(author);

        // 分类信息
        AppCommunityTopicRespVO.CategoryInfo category = new AppCommunityTopicRespVO.CategoryInfo();
        JsonNode catNode = node.path("category");
        category.setCid(catNode.path("cid").asInt(node.path("cid").asInt()));
        category.setName(catNode.path("name").asText());
        category.setIcon(catNode.path("icon").asText());
        vo.setCategory(category);

        // 标签
        JsonNode tagsNode = node.path("tags");
        if (tagsNode.isArray()) {
            List<String> tags = new ArrayList<>();
            for (JsonNode tag : tagsNode) {
                tags.add(tag.path("value").asText());
            }
            vo.setTags(tags);
        }

        // 统计数据
        vo.setPostCount(node.path("postcount").asInt());
        vo.setViewCount(node.path("viewcount").asInt());
        vo.setLikeCount(node.path("upvotes").asInt());
        vo.setBookmarkCount(node.path("bookmarks").asInt());

        // 状态
        vo.setPinned(node.path("pinned").asBoolean());
        vo.setLocked(node.path("locked").asBoolean());

        // 时间
        long timestamp = node.path("timestamp").asLong();
        if (timestamp > 0) {
            vo.setCreateTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault()));
            vo.setTimeAgo(formatTimeAgo(timestamp));
        }

        long lastposttime = node.path("lastposttime").asLong();
        if (lastposttime > 0) {
            vo.setLastReplyTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(lastposttime), ZoneId.systemDefault()));
        }

        return vo;
    }

    private void copyTopicFields(JsonNode node, AppCommunityTopicDetailRespVO vo, boolean includeContent) {
        AppCommunityTopicRespVO base = convertTopic(node, includeContent);
        if (base == null) return;

        vo.setTid(base.getTid());
        vo.setTitle(base.getTitle());
        vo.setContent(base.getContent());
        vo.setExcerpt(base.getExcerpt());
        vo.setAuthor(base.getAuthor());
        vo.setCategory(base.getCategory());
        vo.setTags(base.getTags());
        vo.setPostCount(base.getPostCount());
        vo.setViewCount(base.getViewCount());
        vo.setLikeCount(base.getLikeCount());
        vo.setBookmarkCount(base.getBookmarkCount());
        vo.setPinned(base.getPinned());
        vo.setLocked(base.getLocked());
        vo.setCreateTime(base.getCreateTime());
        vo.setLastReplyTime(base.getLastReplyTime());
        vo.setTimeAgo(base.getTimeAgo());
    }

    private List<AppCommunityTopicDetailRespVO.RelatedTopic> getRelatedTopics(JsonNode topicNode) {
        // 简单实现：返回空列表，后续可以通过标签匹配获取相关帖子
        return new ArrayList<>();
    }

    private AppCommunityPostRespVO convertPost(JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }

        AppCommunityPostRespVO vo = new AppCommunityPostRespVO();
        vo.setPid(node.path("pid").asInt());
        vo.setTid(node.path("tid").asInt());
        vo.setIndex(node.path("index").asInt());
        vo.setContent(node.path("content").asText());
        vo.setLikeCount(node.path("upvotes").asInt());
        vo.setIsMainPost(node.path("isMainPost").asBoolean());

        // 作者信息
        AppCommunityPostRespVO.AuthorInfo author = new AppCommunityPostRespVO.AuthorInfo();
        JsonNode userNode = node.path("user");
        author.setUid((long) userNode.path("uid").asInt());
        author.setUsername(userNode.path("username").asText());
        author.setNickname(StrUtil.blankToDefault(userNode.path("displayname").asText(), userNode.path("username").asText()));
        author.setAvatar(userNode.path("picture").asText());
        vo.setAuthor(author);

        // 时间
        long timestamp = node.path("timestamp").asLong();
        if (timestamp > 0) {
            vo.setCreateTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault()));
            vo.setTimeAgo(formatTimeAgo(timestamp));
        }

        // 引用回复
        int toPid = node.path("toPid").asInt();
        if (toPid > 0) {
            vo.setToPid(toPid);
        }

        return vo;
    }

    private AppCommunityNotificationRespVO convertNotification(JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }

        AppCommunityNotificationRespVO vo = new AppCommunityNotificationRespVO();
        vo.setNid(node.path("nid").asText());
        vo.setType(node.path("type").asText());
        vo.setBodyShort(node.path("bodyShort").asText());
        vo.setTopicTitle(node.path("topicTitle").asText());
        vo.setTid(node.path("tid").asInt());
        vo.setPid(node.path("pid").asInt());
        vo.setRead(node.path("read").asBoolean());

        // 发送者信息
        AppCommunityNotificationRespVO.SenderInfo sender = new AppCommunityNotificationRespVO.SenderInfo();
        JsonNode fromNode = node.path("from");
        sender.setUid((long) fromNode.path("uid").asInt());
        sender.setNickname(fromNode.path("username").asText());
        sender.setAvatar(fromNode.path("picture").asText());
        vo.setSender(sender);

        // 时间
        long datetime = node.path("datetime").asLong();
        if (datetime > 0) {
            vo.setCreateTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(datetime), ZoneId.systemDefault()));
            vo.setTimeAgo(formatTimeAgo(datetime));
        }

        return vo;
    }

    private String formatTimeAgo(long timestamp) {
        long now = System.currentTimeMillis();
        long diff = now - timestamp;

        if (diff < 60 * 1000) {
            return "刚刚";
        } else if (diff < 60 * 60 * 1000) {
            return (diff / (60 * 1000)) + "分钟前";
        } else if (diff < 24 * 60 * 60 * 1000) {
            return (diff / (60 * 60 * 1000)) + "小时前";
        } else if (diff < 7 * 24 * 60 * 60 * 1000) {
            return (diff / (24 * 60 * 60 * 1000)) + "天前";
        } else {
            return DateUtil.format(new Date(timestamp), "MM-dd");
        }
    }
}


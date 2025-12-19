package cn.iocoder.yudao.module.member.framework.nodebb.core;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.iocoder.yudao.framework.common.util.http.HttpUtils;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.module.member.config.NodeBBProperties;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * NodeBB 社区 API 客户端
 * 封装对 NodeBB 社区相关 API 的调用
 *
 * @author 芋道源码
 */
@Component
@ConditionalOnProperty(prefix = "yudao.member.nodebb", name = "enabled", havingValue = "true")
@RequiredArgsConstructor
@Slf4j
public class NodeBBCommunityClient {

    private final NodeBBProperties nodeBBProperties;

    // ==================== 分类相关 API ====================

    /**
     * 获取分类列表
     *
     * @return 分类列表 JSON
     */
    public JsonNode getCategories() {
        String url = getBaseUrl() + "/api/categories";
        return doGet(url, null);
    }

    /**
     * 获取分类下的帖子列表
     *
     * @param cid  分类ID
     * @param page 页码（从1开始）
     * @return 分类信息及帖子列表
     */
    public JsonNode getCategoryTopics(int cid, int page) {
        String slug = "category"; // NodeBB 需要 slug，但 API 可以用任意值
        String url = getBaseUrl() + "/api/category/" + cid + "/" + slug;
        Map<String, Object> params = new HashMap<>();
        if (page > 1) {
            params.put("page", page);
        }
        return doGet(url, params);
    }

    // ==================== 标签相关 API ====================

    /**
     * 获取标签列表
     *
     * @return 标签列表
     */
    public JsonNode getTags() {
        String url = getBaseUrl() + "/api/tags";
        return doGet(url, null);
    }

    /**
     * 获取标签下的帖子列表
     *
     * @param tag  标签名
     * @param page 页码
     * @return 标签信息及帖子列表
     */
    public JsonNode getTagTopics(String tag, int page) {
        String url = getBaseUrl() + "/api/tags/" + tag;
        Map<String, Object> params = new HashMap<>();
        if (page > 1) {
            params.put("page", page);
        }
        return doGet(url, params);
    }

    // ==================== 帖子相关 API ====================

    /**
     * 获取最新帖子列表
     *
     * @param page 页码
     * @return 帖子列表
     */
    public JsonNode getRecentTopics(int page) {
        String url = getBaseUrl() + "/api/recent";
        Map<String, Object> params = new HashMap<>();
        if (page > 1) {
            params.put("page", page);
        }
        return doGet(url, params);
    }

    /**
     * 获取热门帖子列表
     *
     * @param page 页码
     * @return 帖子列表
     */
    public JsonNode getPopularTopics(int page) {
        String url = getBaseUrl() + "/api/popular";
        Map<String, Object> params = new HashMap<>();
        if (page > 1) {
            params.put("page", page);
        }
        return doGet(url, params);
    }

    /**
     * 获取最高评价帖子列表
     *
     * @param page 页码
     * @return 帖子列表
     */
    public JsonNode getTopTopics(int page) {
        String url = getBaseUrl() + "/api/top";
        Map<String, Object> params = new HashMap<>();
        if (page > 1) {
            params.put("page", page);
        }
        return doGet(url, params);
    }

    /**
     * 获取帖子详情（含回帖列表）
     *
     * @param tid  帖子ID
     * @param page 回帖页码
     * @return 帖子详情
     */
    public JsonNode getTopicDetail(int tid, int page) {
        String slug = "topic"; // slug 可以是任意值
        String url = getBaseUrl() + "/api/topic/" + tid + "/" + slug;
        Map<String, Object> params = new HashMap<>();
        if (page > 1) {
            params.put("page", page);
        }
        return doGet(url, params);
    }

    /**
     * 创建帖子
     *
     * @param uid     操作用户的 NodeBB uid
     * @param cid     分类ID
     * @param title   标题
     * @param content 内容
     * @param tags    标签列表
     * @return 创建结果
     */
    public JsonNode createTopic(int uid, int cid, String title, String content, List<String> tags) {
        String url = getBaseUrl() + "/api/v3/topics";
        Map<String, Object> body = new HashMap<>();
        body.put("cid", cid);
        body.put("title", title);
        body.put("content", content);
        if (tags != null && !tags.isEmpty()) {
            body.put("tags", tags);
        }
        return doPost(url, body, uid);
    }

    /**
     * 创建回帖
     *
     * @param uid     操作用户的 NodeBB uid
     * @param tid     帖子ID
     * @param content 回复内容
     * @param toPid   引用的楼层ID（可选）
     * @return 创建结果
     */
    public JsonNode createReply(int uid, int tid, String content, Integer toPid) {
        String url = getBaseUrl() + "/api/v3/topics/" + tid;
        Map<String, Object> body = new HashMap<>();
        body.put("content", content);
        if (toPid != null) {
            body.put("toPid", toPid);
        }
        return doPost(url, body, uid);
    }

    // ==================== 互动相关 API ====================

    /**
     * 点赞/取消点赞帖子
     *
     * @param uid   操作用户的 NodeBB uid
     * @param pid   楼层ID
     * @param delta 1=点赞, -1=取消点赞, 0=取消投票
     * @return 操作结果
     */
    public JsonNode votePost(int uid, int pid, int delta) {
        String url = getBaseUrl() + "/api/v3/posts/" + pid + "/vote";
        Map<String, Object> body = new HashMap<>();
        body.put("delta", delta);
        return doPost(url, body, uid);
    }

    /**
     * 收藏帖子
     *
     * @param uid 操作用户的 NodeBB uid
     * @param pid 楼层ID
     * @return 操作结果
     */
    public JsonNode bookmarkPost(int uid, int pid) {
        String url = getBaseUrl() + "/api/v3/posts/" + pid + "/bookmark";
        return doPost(url, new HashMap<>(), uid);
    }

    /**
     * 取消收藏帖子
     *
     * @param uid 操作用户的 NodeBB uid
     * @param pid 楼层ID
     * @return 操作结果
     */
    public JsonNode unbookmarkPost(int uid, int pid) {
        String url = getBaseUrl() + "/api/v3/posts/" + pid + "/bookmark";
        return doDelete(url, uid);
    }

    /**
     * 关注帖子
     *
     * @param uid 操作用户的 NodeBB uid
     * @param tid 帖子ID
     * @return 操作结果
     */
    public JsonNode followTopic(int uid, int tid) {
        String url = getBaseUrl() + "/api/v3/topics/" + tid + "/follow";
        return doPost(url, new HashMap<>(), uid);
    }

    /**
     * 取消关注帖子
     *
     * @param uid 操作用户的 NodeBB uid
     * @param tid 帖子ID
     * @return 操作结果
     */
    public JsonNode unfollowTopic(int uid, int tid) {
        String url = getBaseUrl() + "/api/v3/topics/" + tid + "/follow";
        return doDelete(url, uid);
    }

    // ==================== 用户相关 API ====================

    /**
     * 获取用户发布的帖子列表
     *
     * @param userslug 用户 slug
     * @param page     页码
     * @return 帖子列表
     */
    public JsonNode getUserTopics(String userslug, int page) {
        String url = getBaseUrl() + "/api/user/" + userslug + "/topics";
        Map<String, Object> params = new HashMap<>();
        if (page > 1) {
            params.put("page", page);
        }
        return doGet(url, params);
    }

    /**
     * 获取用户的回帖列表
     *
     * @param userslug 用户 slug
     * @param page     页码
     * @return 回帖列表
     */
    public JsonNode getUserPosts(String userslug, int page) {
        String url = getBaseUrl() + "/api/user/" + userslug + "/posts";
        Map<String, Object> params = new HashMap<>();
        if (page > 1) {
            params.put("page", page);
        }
        return doGet(url, params);
    }

    /**
     * 获取用户收藏的帖子列表
     *
     * @param userslug 用户 slug
     * @param page     页码
     * @return 收藏列表
     */
    public JsonNode getUserBookmarks(String userslug, int page) {
        String url = getBaseUrl() + "/api/user/" + userslug + "/bookmarks";
        Map<String, Object> params = new HashMap<>();
        if (page > 1) {
            params.put("page", page);
        }
        return doGetWithAuth(url, params, nodeBBProperties.getAdminUid());
    }

    /**
     * 通过 uid 获取用户信息
     *
     * @param uid 用户 uid
     * @return 用户信息
     */
    public JsonNode getUserByUid(int uid) {
        String url = getBaseUrl() + "/api/user/uid/" + uid;
        return doGet(url, null);
    }

    // ==================== 通知相关 API ====================

    /**
     * 获取用户通知列表
     *
     * @param uid 用户的 NodeBB uid
     * @return 通知列表
     */
    public JsonNode getNotifications(int uid) {
        String url = getBaseUrl() + "/api/notifications";
        return doGetWithAuth(url, null, uid);
    }

    // ==================== 搜索相关 API ====================

    /**
     * 搜索帖子
     *
     * @param term 搜索词
     * @param page 页码
     * @return 搜索结果
     */
    public JsonNode search(String term, int page) {
        String url = getBaseUrl() + "/api/search";
        Map<String, Object> params = new HashMap<>();
        params.put("term", term);
        params.put("in", "titlesposts"); // 搜索标题和内容
        if (page > 1) {
            params.put("page", page);
        }
        return doGet(url, params);
    }

    // ==================== 内部方法 ====================

    private String getBaseUrl() {
        return StrUtil.removeSuffix(nodeBBProperties.getUrl(), "/");
    }

    private Map<String, String> getReadHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        return headers;
    }

    private Map<String, String> getWriteHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + nodeBBProperties.getMasterToken());
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        return headers;
    }

    /**
     * GET 请求（公开 API，无需认证）
     */
    private JsonNode doGet(String url, Map<String, Object> params) {
        try {
            String fullUrl = buildUrl(url, params);
            log.debug("[NodeBB API] GET: {}", fullUrl);
            String response = HttpUtils.get(fullUrl, getReadHeaders());
            log.debug("[NodeBB API] Response: {}", response);
            return JsonUtils.parseTree(response);
        } catch (Exception e) {
            log.error("[NodeBB API] GET 请求失败: url={}, error={}", url, e.getMessage(), e);
            return null;
        }
    }

    /**
     * GET 请求（需要认证）
     */
    private JsonNode doGetWithAuth(String url, Map<String, Object> params, int uid) {
        try {
            if (params == null) {
                params = new HashMap<>();
            }
            params.put("_uid", uid);
            String fullUrl = buildUrl(url, params);
            log.debug("[NodeBB API] GET (auth): {}", fullUrl);
            String response = HttpUtils.get(fullUrl, getWriteHeaders());
            log.debug("[NodeBB API] Response: {}", response);
            return JsonUtils.parseTree(response);
        } catch (Exception e) {
            log.error("[NodeBB API] GET (auth) 请求失败: url={}, error={}", url, e.getMessage(), e);
            return null;
        }
    }

    /**
     * POST 请求（需要认证）
     */
    private JsonNode doPost(String url, Map<String, Object> body, int uid) {
        try {
            body.put("_uid", uid);
            String fullUrl = url;
            log.debug("[NodeBB API] POST: {}, body: {}", fullUrl, JsonUtils.toJsonString(body));
            String response = HttpUtils.post(fullUrl, getWriteHeaders(), JsonUtils.toJsonString(body));
            log.debug("[NodeBB API] Response: {}", response);
            return JsonUtils.parseTree(response);
        } catch (Exception e) {
            log.error("[NodeBB API] POST 请求失败: url={}, error={}", url, e.getMessage(), e);
            return null;
        }
    }

    /**
     * DELETE 请求（需要认证）
     */
    private JsonNode doDelete(String url, int uid) {
        try {
            String fullUrl = url + "?_uid=" + uid;
            log.debug("[NodeBB API] DELETE: {}", fullUrl);
            Map<String, String> headers = getWriteHeaders();
            HttpRequest request = HttpRequest.delete(fullUrl);
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                request.header(entry.getKey(), entry.getValue());
            }
            String response = request.execute().body();
            log.debug("[NodeBB API] Response: {}", response);
            return JsonUtils.parseTree(response);
        } catch (Exception e) {
            log.error("[NodeBB API] DELETE 请求失败: url={}, error={}", url, e.getMessage(), e);
            return null;
        }
    }

    private String buildUrl(String baseUrl, Map<String, Object> params) {
        if (params == null || params.isEmpty()) {
            return baseUrl;
        }
        StringBuilder sb = new StringBuilder(baseUrl);
        sb.append("?");
        boolean first = true;
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (!first) {
                sb.append("&");
            }
            sb.append(entry.getKey()).append("=").append(entry.getValue());
            first = false;
        }
        return sb.toString();
    }
}


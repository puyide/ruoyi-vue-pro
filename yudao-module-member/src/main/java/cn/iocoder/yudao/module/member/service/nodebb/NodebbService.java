package cn.iocoder.yudao.module.member.service.nodebb;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.jwt.JWTUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.common.util.monitor.TracerUtils;
import cn.iocoder.yudao.framework.common.util.http.HttpUtils;
import cn.iocoder.yudao.module.member.config.NodeBBProperties;
import cn.iocoder.yudao.module.member.controller.app.auth.vo.AppAuthNodebbSsoRespVO;
import cn.iocoder.yudao.module.member.dal.dataobject.nodebb.MemberNodebbUserDO;
import cn.iocoder.yudao.module.member.dal.dataobject.user.MemberUserDO;
import cn.iocoder.yudao.module.member.dal.mysql.nodebb.MemberNodebbUserMapper;
import cn.iocoder.yudao.module.member.dal.mysql.user.MemberUserMapper;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DuplicateKeyException;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.member.enums.ErrorCodeConstants.*;

/**
 * NodeBB 集成服务（整合双方案 SSO）
 * 
 * 支持两种 SSO 方式：
 * 1. Session Sharing (JWT Cookie) - 用户从 ruoyi 跳转到 NodeBB
 * 2. OAuth2 Provider - 用户直接访问 NodeBB，由 NodeBB 发起授权
 *
 * @author 芋道源码
 */
@Service
@ConditionalOnProperty(prefix = "yudao.member.nodebb", name = "enabled", havingValue = "true", matchIfMissing = false)
@Slf4j
public class NodebbService {

    @Resource
    private NodeBBProperties nodeBBProperties;
    @Resource
    private MemberUserMapper memberUserMapper;
    @Resource
    private MemberNodebbUserMapper memberNodebbUserMapper;

    public boolean isCreateEnabled() {
        return StrUtil.isNotBlank(nodeBBProperties.getUrl())
                && StrUtil.isNotBlank(nodeBBProperties.getMasterToken());
    }

    public boolean isSsoEnabled() {
        return isCreateEnabled() && StrUtil.isNotBlank(nodeBBProperties.getSsoSecret());
    }

    /**
     * 注册完成后，同步创建 NodeBB 用户（带错误记录）
     * 
     * 此方法用于 MemberUserCreateMessage 消费者
     */
    @Transactional(rollbackFor = Exception.class)
    public void syncCreateNodeBBUser(Long userId) {
        if (!isCreateEnabled()) {
            log.info("[NodeBB 同步] 集成未启用，跳过同步：userId={}", userId);
            return;
        }

        // 1. 检查是否已同步
        MemberNodebbUserDO existMapping = memberNodebbUserMapper.selectByUserId(userId);
        if (existMapping != null && existMapping.getSyncStatus() != null && existMapping.getSyncStatus() == 1) {
            log.info("[NodeBB 同步] 用户已同步，跳过：userId={}, nodebbUid={}", userId, existMapping.getNodebbUid());
            return;
        }

        // 2. 获取会员用户信息
        MemberUserDO user = memberUserMapper.selectById(userId);
        if (user == null) {
            log.error("[NodeBB 同步] 会员用户不存在：userId={}", userId);
            return;
        }

        try {
            // 3. 生成 NodeBB 用户名
            String username = generateNodeBBUsername(user);
            String email = StrUtil.isNotBlank(user.getMobile()) 
                ? user.getMobile() + "@member.local" 
                : null;

            // 4. 调用 NodeBB API 创建用户
            Integer nodebbUid = remoteCreateUser(username, email);
            if (nodebbUid == null) {
                throw new RuntimeException("NodeBB API 返回 UID 为空");
            }

            log.info("[NodeBB 同步] 创建用户成功：userId={}, nodebbUid={}, username={}",
                    userId, nodebbUid, username);

            // 5. 保存或更新映射关系
            if (existMapping != null) {
                existMapping.setNodebbUid(nodebbUid);
                existMapping.setUsername(username);
                existMapping.setSyncStatus(1);
                existMapping.setSyncErrorMsg(null);
                memberNodebbUserMapper.updateById(existMapping);
            } else {
                MemberNodebbUserDO record = new MemberNodebbUserDO();
                record.setUserId(userId);
                record.setNodebbUid(nodebbUid);
                record.setUsername(username);
                record.setSyncStatus(1);
                try {
                    memberNodebbUserMapper.insert(record);
                } catch (DuplicateKeyException ignore) {
                    // 并发创建，忽略
                    log.info("[NodeBB 同步] 并发创建检测到重复，忽略：userId={}", userId);
                }
            }

        } catch (Exception e) {
            log.error("[NodeBB 同步] 创建用户失败：userId={}, error={}", userId, e.getMessage(), e);

            // 记录失败信息（不抛异常，避免影响用户注册）
            if (existMapping != null) {
                existMapping.setSyncStatus(0);
                existMapping.setSyncErrorMsg(StrUtil.maxLength(e.getMessage(), 500));
                memberNodebbUserMapper.updateById(existMapping);
            } else {
                MemberNodebbUserDO record = new MemberNodebbUserDO();
                record.setUserId(userId);
                record.setSyncStatus(0);
                record.setSyncErrorMsg(StrUtil.maxLength(e.getMessage(), 500));
                try {
                    memberNodebbUserMapper.insert(record);
                } catch (DuplicateKeyException ignore) {
                    // 并发插入失败记录，忽略
                }
            }

            log.warn("[NodeBB 同步] 已记录失败信息，不影响用户注册流程");
        }
    }

    /**
     * 注册完成后，同步创建 NodeBB 用户，若已存在则直接返回
     * 
     * 此方法用于快速获取或创建映射（无错误记录）
     */
    @Transactional
    public MemberNodebbUserDO createUserIfAbsent(Long userId) {
        if (!isCreateEnabled()) {
            return null;
        }
        MemberNodebbUserDO exist = memberNodebbUserMapper.selectByUserId(userId);
        if (exist != null && exist.getSyncStatus() != null && exist.getSyncStatus() == 1) {
            return exist;
        }
        
        MemberUserDO user = memberUserMapper.selectById(userId);
        if (user == null) {
            return null;
        }
        
        String username = generateNodeBBUsername(user);
        String email = StrUtil.isNotBlank(user.getMobile()) ? user.getMobile() + "@member.local" : null;
        Integer uid = remoteCreateUser(username, email);
        if (uid == null) {
            return null;
        }
        
        if (exist != null) {
            exist.setNodebbUid(uid);
            exist.setUsername(username);
            exist.setSyncStatus(1);
            exist.setSyncErrorMsg(null);
            memberNodebbUserMapper.updateById(exist);
            return exist;
        } else {
            MemberNodebbUserDO record = new MemberNodebbUserDO();
            record.setUserId(userId);
            record.setNodebbUid(uid);
            record.setUsername(username);
            record.setSyncStatus(1);
            try {
                memberNodebbUserMapper.insert(record);
            } catch (DuplicateKeyException ignore) {
                return memberNodebbUserMapper.selectByUserId(userId);
            }
            return record;
        }
    }

    /**
     * 为登录用户生成 NodeBB session-sharing token（方案A - Session Sharing）
     */
    public AppAuthNodebbSsoRespVO buildSso(Long userId) {
        if (!isSsoEnabled()) {
            log.warn("[NodeBB SSO] SSO 未启用：userId={}", userId);
            throw exception(NODEBB_NOT_ENABLED);
        }

        // 1. 检查用户是否已同步，如果没有则自动同步
        MemberNodebbUserDO mapping = memberNodebbUserMapper.selectByUserId(userId);
        if (mapping == null || mapping.getSyncStatus() == null || mapping.getSyncStatus() != 1) {
            log.info("[NodeBB SSO] 用户未同步，尝试自动同步：userId={}", userId);
            mapping = createUserIfAbsent(userId);
            if (mapping == null || mapping.getSyncStatus() != 1) {
                log.error("[NodeBB SSO] 用户同步失败或未完成：userId={}", userId);
                throw exception(NODEBB_USER_NOT_SYNCED);
            }
        }

        // 2. 获取用户信息
        MemberUserDO user = memberUserMapper.selectById(userId);
        String email = user != null && StrUtil.isNotBlank(user.getMobile())
                ? user.getMobile() + "@member.local" : null;

        // 3. 生成 JWT Payload（符合 nodebb-plugin-session-sharing 规范）
        long currentTimeSeconds = System.currentTimeMillis() / 1000;
        Map<String, Object> payload = new HashMap<>();
        payload.put("id", mapping.getNodebbUid());      // NodeBB UID（必需）
        payload.put("username", mapping.getUsername()); // 用户名（必需）
        
        if (StrUtil.isNotBlank(email)) {
            payload.put("email", email);
        }
        if (user != null && StrUtil.isNotBlank(user.getAvatar())) {
            payload.put("picture", user.getAvatar());
        }
        if (user != null && StrUtil.isNotBlank(user.getName())) {
            payload.put("firstName", user.getName());
        }
        
        // 添加标准 JWT 声明
        payload.put("iat", currentTimeSeconds);  // issued at
        payload.put("exp", currentTimeSeconds + nodeBBProperties.getSsoCookieMaxAgeSeconds()); // expiration

        // 4. 生成 JWT Token
        String token = JWTUtil.createToken(payload, nodeBBProperties.getSsoSecret().getBytes(StandardCharsets.UTF_8));

        // 5. 构造返回结果
        AppAuthNodebbSsoRespVO vo = new AppAuthNodebbSsoRespVO();
        vo.setToken(token);
        vo.setNodebbUrl(nodeBBProperties.getUrl());
        vo.setCookieName(nodeBBProperties.getSsoCookieName());
        vo.setCookieDomain(nodeBBProperties.getSsoCookieDomain());
        vo.setCookieMaxAgeSeconds(nodeBBProperties.getSsoCookieMaxAgeSeconds());

        log.info("[NodeBB SSO] 生成 SSO Token 成功：userId={}, nodebbUid={}", userId, mapping.getNodebbUid());
        return vo;
    }

    /**
     * 生成 NodeBB 用户名
     * 规则：优先昵称，其次手机号，最后随机
     */
    private String generateNodeBBUsername(MemberUserDO user) {
        if (StrUtil.isNotBlank(user.getNickname())) {
            // 移除特殊字符，NodeBB 要求：2-20 字符，字母数字下划线中划线中文
            String username = user.getNickname().replaceAll("[^a-zA-Z0-9_\\-\\u4e00-\\u9fa5]", "");
            if (username.length() >= 2) {
                return username;
            }
        }

        if (StrUtil.isNotBlank(user.getMobile())) {
            return "user_" + user.getMobile();
        }

        // 兜底：生成随机用户名
        return "user_" + IdUtil.fastSimpleUUID().substring(0, 8);
    }

    /**
     * 调用 NodeBB Write API 创建用户
     */
    private Integer remoteCreateUser(String username, String email) {
        String baseUrl = StrUtil.removeSuffix(nodeBBProperties.getUrl(), "/");
        String url = baseUrl + "/api/v3/users";
        
        Map<String, Object> body = new HashMap<>();
        body.put("username", username);
        body.put("password", StrUtil.blankToDefault(nodeBBProperties.getDefaultPassword(), RandomUtil.randomString(16)));
        body.put("_uid", nodeBBProperties.getAdminUid()); // Master Token 需要指定管理员 UID
        if (StrUtil.isNotBlank(email)) {
            body.put("email", email);
        }
        
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + nodeBBProperties.getMasterToken());
        headers.put("Content-Type", "application/json");
        
        String requestBody = JsonUtils.toJsonString(body);
        log.info("[NodeBB API] 创建用户请求：url={}, body={}", url, requestBody);
        
        try {
            String response = HttpUtils.post(url, headers, requestBody);
            log.info("[NodeBB API] 创建用户响应：{}", response);
            
            JsonNode root = JsonUtils.parseTree(response);
            if (root == null) {
                log.error("[NodeBB API] 响应解析失败：response={}", response);
                return null;
            }
            
            // 检查状态码
            JsonNode statusNode = root.path("status").path("code");
            if (!statusNode.isMissingNode() && !"ok".equals(statusNode.asText())) {
                String errorMsg = root.path("status").path("message").asText("Unknown error");
                log.error("[NodeBB API] 创建用户失败：{}", errorMsg);
                return null;
            }
            
            // 提取 UID
            JsonNode uidNode = root.path("response").path("uid");
            if (uidNode == null || !uidNode.isInt()) {
                log.warn("[NodeBB API] 创建用户失败，未返回 UID：traceId={}, resp={}", 
                        TracerUtils.getTraceId(), response);
                return null;
            }
            
            return uidNode.asInt();
        } catch (Exception e) {
            log.error("[NodeBB API] 调用失败：url={}, error={}", url, e.getMessage(), e);
            return null;
        }
    }
}


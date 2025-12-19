package cn.iocoder.yudao.module.member.framework.nodebb.core;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.util.http.HttpUtils;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.module.member.config.NodeBBProperties;
import cn.iocoder.yudao.module.member.framework.nodebb.core.dto.NodeBBCreateUserReqDTO;
import cn.iocoder.yudao.module.member.framework.nodebb.core.dto.NodeBBCreateUserRespDTO;
import cn.iocoder.yudao.module.member.framework.nodebb.core.dto.NodeBBLoginReqDTO;
import cn.iocoder.yudao.module.member.framework.nodebb.core.dto.NodeBBLoginRespDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * NodeBB API 客户端
 * 封装对 NodeBB Write API 的调用
 *
 * @author 芋道源码
 */
@Component
@ConditionalOnProperty(prefix = "yudao.member.nodebb", name = "enabled", havingValue = "true")
@RequiredArgsConstructor
@Slf4j
public class NodeBBApiClient {

    private final NodeBBProperties nodeBBProperties;

    /**
     * 创建 NodeBB 用户
     *
     * @param reqDTO 创建用户请求
     * @return 创建结果（包含 uid、username）
     */
    public NodeBBCreateUserRespDTO createUser(NodeBBCreateUserReqDTO reqDTO) {
        String url = nodeBBProperties.getUrl() + "/api/v3/users";
        
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + nodeBBProperties.getMasterToken());
        headers.put("Content-Type", "application/json");
        // Master token 需要通过 _uid 参数指定以哪个用户身份操作
        
        Map<String, Object> body = new HashMap<>();
        body.put("username", reqDTO.getUsername());
        body.put("password", reqDTO.getPassword());
        if (StrUtil.isNotBlank(reqDTO.getEmail())) {
            body.put("email", reqDTO.getEmail());
        }
        // 通过 _uid 指定管理员身份创建用户
        body.put("_uid", nodeBBProperties.getAdminUid());
        
        String requestBody = JsonUtils.toJsonString(body);
        log.info("[NodeBB API] 创建用户请求：url={}, body={}", url, requestBody);
        
        try {
            String responseBody = HttpUtils.post(url, headers, requestBody);
            log.info("[NodeBB API] 创建用户响应：{}", responseBody);
            
            // NodeBB API 返回格式：{"status": {"code": "ok", "message": "OK"}, "response": {...}}
            Map<String, Object> response = JsonUtils.parseObject(responseBody, Map.class);
            Map<String, Object> statusMap = (Map<String, Object>) response.get("status");
            
            if (statusMap == null || !"ok".equals(statusMap.get("code"))) {
                String errorMsg = statusMap != null ? (String) statusMap.get("message") : "Unknown error";
                throw new RuntimeException("NodeBB API 返回错误：" + errorMsg);
            }
            
            Map<String, Object> userMap = (Map<String, Object>) response.get("response");
            NodeBBCreateUserRespDTO respDTO = new NodeBBCreateUserRespDTO();
            respDTO.setUid((Integer) userMap.get("uid"));
            respDTO.setUsername((String) userMap.get("username"));
            respDTO.setEmail((String) userMap.get("email"));
            
            return respDTO;
        } catch (Exception e) {
            log.error("[NodeBB API] 创建用户失败：url={}, error={}", url, e.getMessage(), e);
            throw new RuntimeException("调用 NodeBB API 创建用户失败：" + e.getMessage(), e);
        }
    }

    /**
     * 验证用户登录（可用于测试连接）
     *
     * @param reqDTO 登录请求
     * @return 登录结果
     */
    public NodeBBLoginRespDTO login(NodeBBLoginReqDTO reqDTO) {
        String url = nodeBBProperties.getUrl() + "/api/v3/utilities/login";
        
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        
        Map<String, String> body = new HashMap<>();
        body.put("username", reqDTO.getUsername());
        body.put("password", reqDTO.getPassword());
        
        String requestBody = JsonUtils.toJsonString(body);
        log.info("[NodeBB API] 登录请求：url={}, username={}", url, reqDTO.getUsername());
        
        try {
            String responseBody = HttpUtils.post(url, headers, requestBody);
            log.info("[NodeBB API] 登录响应：{}", responseBody);
            
            Map<String, Object> response = JsonUtils.parseObject(responseBody, Map.class);
            Map<String, Object> statusMap = (Map<String, Object>) response.get("status");
            
            if (statusMap == null || !"ok".equals(statusMap.get("code"))) {
                String errorMsg = statusMap != null ? (String) statusMap.get("message") : "Login failed";
                throw new RuntimeException("NodeBB 登录失败：" + errorMsg);
            }
            
            Map<String, Object> userMap = (Map<String, Object>) response.get("response");
            NodeBBLoginRespDTO respDTO = new NodeBBLoginRespDTO();
            respDTO.setUid((Integer) userMap.get("uid"));
            respDTO.setUsername((String) userMap.get("username"));
            
            return respDTO;
        } catch (Exception e) {
            log.error("[NodeBB API] 登录失败：url={}, error={}", url, e.getMessage(), e);
            throw new RuntimeException("调用 NodeBB API 登录失败：" + e.getMessage(), e);
        }
    }

}


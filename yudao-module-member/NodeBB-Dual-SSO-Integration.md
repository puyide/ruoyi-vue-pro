# NodeBB 双方案 SSO 集成完整指南

## 🎯 集成方案概述

本系统实现了**两种 SSO 方案**，满足不同场景需求：

### 方案A：Session Sharing（JWT Cookie）✅
**适用场景**：用户从 ruoyi 应用进入论坛

```
用户在 ruoyi 登录 → 前端获取 JWT → 设置 Cookie → 跳转 NodeBB → 自动登录
```

### 方案B：OAuth2 Provider ✅  
**适用场景**：用户直接访问 NodeBB URL

```
访问 NodeBB → 未登录 → 跳转 ruoyi OAuth2 授权 → 回调 NodeBB → 自动登录
```

---

## 📦 已完成的工作

### ✅ Session Sharing（方案A）
- [x] `nodebb-plugin-session-sharing` 已安装并激活
- [x] JWT 生成接口：`GET /member/auth/nodebb-sso`
- [x] 用户自动同步到 NodeBB
- [x] 配置脚本：`update-jwt-secret.js`

### ✅ OAuth2 Provider（方案B）
- [x] `nodebb-plugin-sso-oauth` 已安装并激活
- [x] 用户信息接口：`GET /member/oauth2/user`
- [x] 复用 system 模块的 OAuth2 端点
- [x] OAuth2 授权码模式支持

### ✅ 通用基础
- [x] 用户同步服务（注册后自动创建 NodeBB 用户）
- [x] `member_nodebb_user` 映射表
- [x] SQL 建表语句（MySQL/PostgreSQL）

---

## 🚀 快速配置步骤

### 步骤 1：执行数据库建表（5分钟）

```bash
cd "/Users/admin/ma jia/ruoyi-vue-pro"

# PostgreSQL
psql -U postgres -d 你的数据库名 -f sql/postgresql/member_nodebb_user.sql

# MySQL
mysql -u root -p 你的数据库名 < sql/mysql/member_nodebb_user.sql
```

### 步骤 2：配置方案A - Session Sharing（5分钟）

#### 2.1 设置 JWT Secret

```bash
cd "/Users/admin/ma jia/NodeBB"
node update-jwt-secret.js
```

选择 **选项 2** 自动生成强密钥，**保存输出的密钥**！

#### 2.2 配置 yudao（application-local.yaml）

```yaml
yudao:
  member:
    nodebb:
      enabled: true
      url: http://localhost:4567
      master-token: 【见步骤3】
      admin-uid: 1
      sso-secret: 【步骤2.1生成的JWT_Secret】
      sso-cookie-name: token
      sso-cookie-domain: ""
      default-password: "DefaultPass123!"
```

### 步骤 3：配置方案B - OAuth2（10分钟）

#### 3.1 在 ruoyi 后台创建 OAuth2 Client

1. 登录 ruoyi 管理后台
2. 进入：**系统管理** → **OAuth2 管理** → **OAuth2 客户端**
3. 点击 **新增**，填写：

| 字段 | 值 | 说明 |
|------|-----|------|
| 客户端编号 | `nodebb` | Client ID |
| 客户端密钥 | `nodebb-secret-123` | Client Secret（自定义强密钥） |
| 授权类型 | `authorization_code,refresh_token` | 授权码模式 |
| 回调地址 | `http://localhost:4567/auth/nodebb/callback` | NodeBB 回调 URL |
| Scope | `user.read` | 权限范围 |
| 自动授权 | 勾选 | 跳过授权确认页面 |
| 状态 | 开启 | |

点击 **确定** 保存。

**如果后台没有 OAuth2 客户端管理页面**，执行以下 SQL：

```sql
-- MySQL
INSERT INTO system_oauth2_client (
  client_id, secret, name, logo, description,
  status, access_token_validity_seconds, refresh_token_validity_seconds,
  redirect_uris, authorized_grant_types, scopes, auto_approve_scopes,
  creator, create_time, updater, update_time, deleted, tenant_id
) VALUES (
  'nodebb', 'nodebb-secret-123', 'NodeBB Forum', '', 'NodeBB 论坛 SSO',
  0, 604800, 2592000,
  '["http://localhost:4567/auth/nodebb/callback"]',
  '["authorization_code","refresh_token"]',
  '["user.read"]', '["user.read"]',
  '1', NOW(), '1', NOW(), 0, 1
);

-- PostgreSQL  
INSERT INTO system_oauth2_client (
  client_id, secret, name, logo, description,
  status, access_token_validity_seconds, refresh_token_validity_seconds,
  redirect_uris, authorized_grant_types, scopes, auto_approve_scopes,
  creator, create_time, updater, update_time, deleted, tenant_id
) VALUES (
  'nodebb', 'nodebb-secret-123', 'NodeBB Forum', '', 'NodeBB 论坛 SSO',
  0, 604800, 2592000,
  '["http://localhost:4567/auth/nodebb/callback"]',
  '["authorization_code","refresh_token"]',
  '["user.read"]', '["user.read"]',
  '1', NOW(), '1', NOW(), 0, 1
);
```

#### 3.2 获取 Master Token（用于方案A）

1. 访问：http://localhost:4567/admin/settings/api
2. 点击 **Generate Token** → 选择 **Master Token**
3. 复制 Token，填入步骤 2.2 的 `master-token`

#### 3.3 配置 NodeBB OAuth2 插件

访问：http://localhost:4567/admin/plugins/sso-oauth

或者执行配置脚本（推荐）：

```bash
cd "/Users/admin/ma jia/NodeBB"
node << 'EOF'
const nconf = require('nconf');
const path = require('path');

nconf.argv().env({ separator: '__' });
nconf.file({ file: path.join(__dirname, 'config.json') });
nconf.defaults({ base_dir: __dirname });

const db = require('./src/database');

async function configureOAuth() {
  await db.init();
  
  const config = {
    id: 'nodebb',
    name: 'RuoYi Member',
    type: 'oauth2',
    oauth2: {
      authorizationURL: 'http://localhost:8080/admin-api/system/oauth2/authorize',
      tokenURL: 'http://localhost:8080/admin-api/system/oauth2/token',
      clientID: 'nodebb',
      clientSecret: 'nodebb-secret-123',
      scope: 'user.read',
      userRoute: 'http://localhost:8080/app-api/member/oauth2/user',
      emailDomain: 'member.local'
    },
    userRoute: 'http://localhost:8080/app-api/member/oauth2/user',
    emailClaim: 'email',
    usernameClaim: 'username'
  };
  
  await db.setObject('settings:sso-oauth', config);
  console.log('✅ OAuth2 配置已保存！');
  await db.close();
  process.exit(0);
}

configureOAuth().catch(err => {
  console.error('❌ 配置失败：', err);
  process.exit(1);
});
EOF
```

**手动配置** (如果脚本失败)：

在 NodeBB 后台填写：

| 配置项 | 值 |
|--------|-----|
| **OAuth2 ID** | `nodebb` |
| **OAuth2 Name** | `RuoYi Member` |
| **Authorization URL** | `http://localhost:8080/admin-api/system/oauth2/authorize` |
| **Token URL** | `http://localhost:8080/admin-api/system/oauth2/token` |
| **Client ID** | `nodebb` |
| **Client Secret** | `nodebb-secret-123` |
| **User Profile URL** | `http://localhost:8080/app-api/member/oauth2/user` |
| **Scope** | `user.read` |
| **Username Claim** | `username` |
| **Email Claim** | `email` |
| **Email Domain** | `member.local` |

### 步骤 4：重启服务（2分钟）

```bash
# 重启 NodeBB
cd "/Users/admin/ma jia/NodeBB"
./nodebb restart

# 重启 yudao（在新的终端）
cd "/Users/admin/ma jia/ruoyi-vue-pro/yudao-server"
mvn clean package -DskipTests
java -jar target/*.jar
```

---

## 🧪 测试验证

### 测试方案A：Session Sharing

#### 1. 注册会员并同步

```bash
# 注册新会员后，查看同步状态
# PostgreSQL
psql -U postgres -d your_db -c "SELECT * FROM member_nodebb_user ORDER BY create_time DESC LIMIT 1;"

# 应该看到 sync_status = 1
```

#### 2. 获取 SSO Token

```bash
# 1. 登录获取 access_token
curl -X POST http://localhost:8080/app-api/member/auth/login \
  -H "Content-Type: application/json" \
  -d '{"mobile":"13800138000","password":"123456"}'

# 2. 获取 SSO Token
curl http://localhost:8080/app-api/member/auth/nodebb-sso \
  -H "Authorization: Bearer <上一步的accessToken>"
```

#### 3. 前端测试

```html
<!DOCTYPE html>
<html>
<head><title>SSO Test</title></head>
<body>
  <button onclick="goToForum()">进入论坛 (Session Sharing)</button>
  
  <script>
  async function goToForum() {
    const token = 'YOUR_ACCESS_TOKEN_HERE';
    
    const res = await fetch('http://localhost:8080/app-api/member/auth/nodebb-sso', {
      headers: { 'Authorization': 'Bearer ' + token }
    });
    
    const result = await res.json();
    if (result.code === 0) {
      const sso = result.data;
      // 设置 Cookie
      document.cookie = `${sso.cookieName}=${sso.token}; path=/; max-age=${sso.maxAge}`;
      // 跳转
      window.location.href = sso.nodebbUrl;
    }
  }
  </script>
</body>
</html>
```

### 测试方案B：OAuth2

#### 1. 直接访问 NodeBB

```
http://localhost:4567
```

#### 2. 点击登录

你应该看到 **"使用 RuoYi Member 登录"** 按钮

#### 3. 点击后会跳转到 ruoyi

```
http://localhost:8080/admin-api/system/oauth2/authorize?
  response_type=code&
  client_id=nodebb&
  redirect_uri=http://localhost:4567/auth/nodebb/callback&
  scope=user.read
```

#### 4. 在 ruoyi 登录或授权

授权后会自动回调到 NodeBB 并登录

#### 5. 验证用户信息接口

```bash
# 使用 OAuth2 access_token 获取用户信息
curl http://localhost:8080/app-api/member/oauth2/user \
  -H "Authorization: Bearer <oauth2_access_token>"

# 应该返回：
{
  "code": 0,
  "data": {
    "id": 1,
    "username": "用户昵称",
    "nickname": "用户昵称",
    "email": "13800138000@member.local",
    "mobile": "13800138000",
    "avatar": "头像URL",
    "name": "真实姓名"
  }
}
```

---

## 💻 前端集成代码

### Vue 3 完整示例

```vue
<template>
  <div>
    <!-- 方案A：Session Sharing -->
    <button @click="goToForumSessionSharing">
      进入论坛 (从 ruoyi)
    </button>
    
    <!-- 方案B：OAuth2 -->
    <a :href="nodebbUrl" target="_blank">
      <button>进入论坛 (直接访问)</button>
    </a>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const nodebbUrl = 'http://localhost:4567';
const accessToken = ref(localStorage.getItem('accessToken'));

// 方案A：Session Sharing 跳转
async function goToForumSessionSharing() {
  try {
    const res = await fetch('/app-api/member/auth/nodebb-sso', {
      headers: {
        'Authorization': 'Bearer ' + accessToken.value
      }
    });
    
    const result = await res.json();
    if (result.code === 0) {
      const sso = result.data;
      
      // 设置 Cookie
      document.cookie = `${sso.cookieName}=${sso.token}; ` +
                        `path=/; ` +
                        `max-age=${sso.maxAge}; ` +
                        `SameSite=Lax`;
      
      // 跳转到 NodeBB
      window.location.href = sso.nodebbUrl;
    } else {
      alert('获取 SSO Token 失败：' + result.msg);
    }
  } catch (error) {
    console.error('SSO 错误:', error);
    alert('SSO 跳转失败');
  }
}
</script>
```

### React 示例

```jsx
import { useState } from 'react';

function ForumLinks() {
  const [accessToken] = useState(localStorage.getItem('accessToken'));
  const nodebbUrl = 'http://localhost:4567';
  
  // 方案A：Session Sharing
  const goToForumSessionSharing = async () => {
    try {
      const response = await fetch('/app-api/member/auth/nodebb-sso', {
        headers: {
          'Authorization': `Bearer ${accessToken}`
        }
      });
      
      const { code, data, msg } = await response.json();
      if (code === 0) {
        // 设置 Cookie
        document.cookie = `${data.cookieName}=${data.token}; path=/; max-age=${data.maxAge}`;
        // 跳转
        window.location.href = data.nodebbUrl;
      } else {
        alert(`SSO 失败：${msg}`);
      }
    } catch (error) {
      console.error('SSO error:', error);
      alert('SSO 跳转失败');
    }
  };
  
  return (
    <div>
      {/* 方案A */}
      <button onClick={goToForumSessionSharing}>
        进入论坛 (从 ruoyi)
      </button>
      
      {/* 方案B */}
      <a href={nodebbUrl} target="_blank" rel="noopener noreferrer">
        <button>进入论坛 (直接访问)</button>
      </a>
    </div>
  );
}

export default ForumLinks;
```

---

## 🔄 两种方案对比表

| 特性 | 方案A (Session Sharing) | 方案B (OAuth2) |
|------|------------------------|----------------|
| **登录入口** | 用户从 ruoyi 跳转 | 用户直接访问 NodeBB |
| **流程** | 前端设置 JWT Cookie | NodeBB 主动跳转授权 |
| **用户体验** | 一键跳转，无感知 | 标准 OAuth2 流程 |
| **前端参与** | 需要（设置 Cookie） | 不需要 |
| **标准化** | 自定义方案 | OAuth2 标准 |
| **配置复杂度** | 简单 | 中等 |
| **适用场景** | ruoyi 是主应用 | NodeBB 独立入口 |
| **安全性** | 高（JWT签名） | 高（OAuth2标准） |

---

## ⚙️ 生产环境配置

### HTTPS 配置（必须！）

```yaml
yudao:
  member:
    nodebb:
      url: https://forum.your-domain.com  # 使用 HTTPS
      sso-cookie-domain: .your-domain.com  # 设置主域名
```

### OAuth2 Client 生产配置

```sql
UPDATE system_oauth2_client SET
  secret = '生产环境强密钥',
  redirect_uris = '["https://forum.your-domain.com/auth/nodebb/callback"]',
  access_token_validity_seconds = 86400,  -- 1天
  refresh_token_validity_seconds = 2592000  -- 30天
WHERE client_id = 'nodebb';
```

### NodeBB OAuth2 配置更新

```javascript
oauth2: {
  authorizationURL: 'https://api.your-domain.com/admin-api/system/oauth2/authorize',
  tokenURL: 'https://api.your-domain.com/admin-api/system/oauth2/token',
  clientID: 'nodebb',
  clientSecret: '生产环境强密钥',
  userRoute: 'https://api.your-domain.com/app-api/member/oauth2/user',
}
```

---

## 🐛 故障排查

### 问题1：方案A - JWT Token 无效

**症状**：Cookie 已设置，但 NodeBB 未登录

**排查**：
1. 检查 JWT Secret 是否一致
2. 浏览器 F12 → Application → Cookies 查看 token 值
3. 使用 jwt.io 解码查看内容
4. 查看 NodeBB 日志：`tail -f logs/output.log | grep session-sharing`

### 问题2：方案B - OAuth2 授权失败

**症状**：跳转到 ruoyi 后报错或无法回调

**排查**：
1. 检查 OAuth2 Client 是否创建成功
2. 检查 redirect_uri 是否完全匹配
3. 查看 ruoyi 日志：搜索 `oauth2`
4. 测试授权接口：
   ```bash
   curl "http://localhost:8080/admin-api/system/oauth2/authorize?\
   response_type=code&\
   client_id=nodebb&\
   redirect_uri=http://localhost:4567/auth/nodebb/callback&\
   scope=user.read" \
   -H "Authorization: Bearer <your_admin_token>"
   ```

### 问题3：用户未同步到 NodeBB

**排查**：
```sql
-- 查看同步状态
SELECT * FROM member_nodebb_user WHERE sync_status = 0 ORDER BY create_time DESC;

-- 查看错误信息
SELECT user_id, sync_error_msg FROM member_nodebb_user WHERE sync_status = 0;
```

**手动重新同步**：
```bash
# 调用同步接口（需管理员权限）
curl -X POST http://localhost:8080/admin-api/member/nodebb/sync-user/【用户ID】 \
  -H "Authorization: Bearer <admin_token>"
```

---

## 📚 API 文档

### 方案A 接口

#### GET /member/auth/nodebb-sso
获取 Session Sharing SSO Token

**请求头：**
```
Authorization: Bearer {accessToken}
```

**响应：**
```json
{
  "code": 0,
  "data": {
    "token": "eyJhbGci...",
    "nodebbUrl": "http://localhost:4567",
    "cookieName": "token",
    "cookieDomain": "",
    "maxAge": 604800
  }
}
```

### 方案B 接口

#### GET /member/oauth2/user
获取 OAuth2 用户信息

**请求头：**
```
Authorization: Bearer {oauth2_access_token}
```

**响应：**
```json
{
  "code": 0,
  "data": {
    "id": 1,
    "username": "zhangsan",
    "nickname": "张三",
    "email": "13800138000@member.local",
    "mobile": "13800138000",
    "avatar": "http://example.com/avatar.jpg",
    "name": "张三"
  }
}
```

---

## 🎉 配置完成检查清单

### 方案A (Session Sharing)
- [ ] JWT Secret 已设置并与 yudao 一致
- [ ] Master Token 已配置
- [ ] nodebb 配置已保存
- [ ] 测试 SSO 跳转成功

### 方案B (OAuth2)
- [ ] OAuth2 Client 已创建
- [ ] NodeBB OAuth2 插件已配置
- [ ] 用户信息接口可访问
- [ ] 测试从 NodeBB 登录成功

### 通用
- [ ] SQL 建表已执行
- [ ] 用户注册后自动同步
- [ ] 两种方案都能正常登录

---

## 🆘 获取帮助

如遇问题，请提供：
1. 使用的方案（A/B）
2. 错误日志（yudao + NodeBB）
3. 配置截图
4. 浏览器控制台错误

祝配置顺利！🚀


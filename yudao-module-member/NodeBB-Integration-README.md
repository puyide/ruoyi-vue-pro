# NodeBB 集成说明

## 功能说明

本模块实现了会员系统与 NodeBB 论坛的集成，提供以下功能：

1. **用户自动同步**：会员注册后，自动在 NodeBB 中创建对应用户账号
2. **单点登录（SSO）**：会员登录后，可无感跳转到 NodeBB 论坛并自动登录

## 前置要求

### 1. NodeBB 安装与配置

确保 NodeBB 已正确安装并运行（版本 >= 4.x）。

### 2. 安装 nodebb-plugin-session-sharing 插件

该插件用于实现 JWT-based SSO。

```bash
cd /path/to/NodeBB
npm install nodebb-plugin-session-sharing
./nodebb build
./nodebb restart
```

### 3. 配置 nodebb-plugin-session-sharing

登录 NodeBB 后台 (Admin Panel)：

1. 进入 **Extend** > **Plugins** > **Session Sharing**
2. 配置以下参数：
   - **JWT Secret**: 设置一个强密钥（与下方 yudao 配置保持一致）
   - **Cookie Name**: 设置为 `token`（或自定义，需与 yudao 配置一致）
   - **Cookie Domain**: 设置为你的主域名（如 `.example.com`）
   - **Payload Parent Namespace**: 留空
   - **App ID**: 留空或设置为 `member`
3. **重要字段映射**：
   - `User ID` → `id`
   - `Username` → `username`
   - `Email` → `email`（可选）
   - `First Name` → `firstName`（可选）
   - `Last Name` → `lastName`（可选）
   - `Picture` → `picture`（可选）
4. 点击 **Save** 保存配置

### 4. 生成 NodeBB Master Token

1. 登录 NodeBB 后台
2. 进入 **Settings** > **API Access**
3. 点击 **Generate Token**
4. 选择 **Master Token**（不关联特定用户）
5. 复制生成的 Token

### 5. 获取管理员 UID

通常管理员 UID 为 `1`，可通过以下方式确认：

- 访问 `http://your-nodebb-url/api/user/username/admin`
- 或在 NodeBB 数据库中查询 `SELECT uid FROM "users" WHERE username='admin'`

## 配置步骤

### 1. 在 application-local.yaml 中添加配置

```yaml
yudao:
  member:
    nodebb:
      # 是否启用 NodeBB 集成（默认：false）
      enabled: true
      # NodeBB 服务地址
      url: http://localhost:4567
      # NodeBB Write API Master Token（在 NodeBB 后台生成）
      master-token: your-nodebb-master-token-here
      # NodeBB 管理员 UID（用于代表管理员创建用户，一般为 1）
      admin-uid: 1
      # SSO JWT 签名密钥（与 nodebb-plugin-session-sharing 配置的 secret 一致）
      sso-secret: your-jwt-secret-here
      # SSO Cookie 名称（与 nodebb-plugin-session-sharing 配置的 name 一致）
      sso-cookie-name: token
      # SSO Cookie Domain（例如：.example.com，留空则使用当前域）
      sso-cookie-domain: ""
      # SSO Cookie 有效期（秒），默认 7 天
      sso-cookie-max-age-seconds: 604800
      # 创建用户时的默认密码（NodeBB 要求至少 8 位）
      default-password: "YourDefaultPass123!"
```

### 2. 执行 SQL 建表语句

**MySQL:**
```bash
mysql -u root -p your_database < sql/mysql/member_nodebb_user.sql
```

**PostgreSQL:**
```bash
psql -U postgres -d your_database -f sql/postgresql/member_nodebb_user.sql
```

## 使用说明

### 1. 用户注册自动同步

会员用户注册成功后，系统会自动：
1. 在 NodeBB 中创建对应用户（用户名基于昵称或手机号）
2. 保存会员用户 ID 与 NodeBB UID 的映射关系
3. 若同步失败，不会影响用户注册流程，但会记录失败原因

### 2. 单点登录（前端集成）

会员登录后，前端需要：

1. **调用后端接口获取 SSO Token：**

```javascript
// GET /member/auth/nodebb-sso
const response = await fetch('/member/auth/nodebb-sso', {
  headers: {
    'Authorization': 'Bearer ' + userAccessToken
  }
});

const ssoData = await response.json();
// ssoData 包含: token, nodebbUrl, cookieName, cookieDomain, maxAge
```

2. **设置 Cookie 并跳转到 NodeBB：**

```javascript
// 设置 Cookie
document.cookie = `${ssoData.cookieName}=${ssoData.token}; ` +
                  `domain=${ssoData.cookieDomain || window.location.hostname}; ` +
                  `path=/; ` +
                  `max-age=${ssoData.maxAge}; ` +
                  `SameSite=Lax`;

// 跳转到 NodeBB（用户将自动登录）
window.location.href = ssoData.nodebbUrl;
```

3. **或者使用隐藏 iframe 实现无感登录：**

```javascript
// 创建隐藏 iframe 设置 Cookie
const iframe = document.createElement('iframe');
iframe.style.display = 'none';
iframe.src = `${ssoData.nodebbUrl}?token=${ssoData.token}`;
document.body.appendChild(iframe);

// 稍后移除 iframe
setTimeout(() => iframe.remove(), 3000);
```

## 故障排查

### 1. 用户未同步到 NodeBB

- 检查 `member_nodebb_user` 表中的 `sync_status` 和 `sync_error_msg` 字段
- 查看应用日志，搜索 `[NodeBB 同步]` 关键字
- 确认 NodeBB Master Token 和 Admin UID 配置正确
- 测试 NodeBB API 连通性：`curl -H "Authorization: Bearer YOUR_TOKEN" http://your-nodebb/api/v3/ping`

### 2. SSO 登录失败

- 确认 JWT Secret 与 nodebb-plugin-session-sharing 配置一致
- 检查 Cookie Domain 设置（必须是主域名或留空）
- 确认 Cookie Name 与插件配置一致
- 查看浏览器控制台和 NodeBB 日志

### 3. NodeBB API 返回 401/403

- 检查 Master Token 是否正确
- 确认请求头格式：`Authorization: Bearer YOUR_TOKEN`
- 确认请求体中包含 `_uid` 参数

## API 文档

### GET /member/auth/nodebb-sso

获取 NodeBB 单点登录信息（需登录）

**响应示例：**
```json
{
  "code": 0,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "nodebbUrl": "http://localhost:4567",
    "cookieName": "token",
    "cookieDomain": ".example.com",
    "maxAge": 604800
  }
}
```

## 安全建议

1. **JWT Secret**: 使用强随机字符串（至少 32 字符）
2. **Master Token**: 妥善保管，不要暴露在前端代码中
3. **HTTPS**: 生产环境必须启用 HTTPS
4. **Cookie Domain**: 仅在需要跨子域时设置，否则留空
5. **默认密码**: 设置复杂密码，用户无法通过此密码直接登录 NodeBB（因为使用 SSO）

## 开发调试

启用调试日志：

```yaml
logging:
  level:
    cn.iocoder.yudao.module.member.framework.nodebb: DEBUG
    cn.iocoder.yudao.module.member.service.nodebb: DEBUG
```

## 参考链接

- [NodeBB Write API Documentation](https://docs.nodebb.org/api/write/)
- [nodebb-plugin-session-sharing](https://github.com/NodeBB/nodebb-plugin-session-sharing)
- [JWT RFC 7519](https://datatracker.ietf.org/doc/html/rfc7519)


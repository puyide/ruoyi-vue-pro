# NodeBB Session Sharing 插件配置指南

## ✅ 插件安装状态

插件已成功安装并激活：`nodebb-plugin-session-sharing`

NodeBB 访问地址：http://localhost:4567

---

## 📝 配置步骤

### 1. 登录 NodeBB 后台

访问：http://localhost:4567/admin

### 2. 进入插件配置页面

导航路径：**Extend** → **Plugins** → 找到 **Session Sharing**，点击右侧的 **Settings** 按钮

或直接访问：http://localhost:4567/admin/plugins/session-sharing

### 3. 配置 Session Sharing 插件

请按照以下配置填写（**必须与 yudao 配置保持一致**）：

#### 基础配置

| 配置项 | 说明 | 推荐值 | 备注 |
|--------|------|--------|------|
| **JWT Secret** | JWT 签名密钥 | `your-strong-secret-key-at-least-32-chars` | ⚠️ 必须与 `yudao.member.nodebb.sso-secret` 完全一致 |
| **Cookie Name** | Cookie 名称 | `token` | ⚠️ 必须与 `yudao.member.nodebb.sso-cookie-name` 一致 |
| **Cookie Domain** | Cookie 域名 | 留空（或填 `.example.com`） | 单域留空，跨子域填主域名 |
| **Cookie Path** | Cookie 路径 | `/` | 默认即可 |
| **Payload Parent Namespace** | 载荷命名空间 | 留空 | 保持默认 |
| **App ID** | 应用标识 | `member` 或留空 | 可选 |

#### 用户字段映射（Payload Keys）

**重要**：将 JWT 中的字段映射到 NodeBB 用户字段

| NodeBB 字段 | JWT Payload 字段名 | 说明 |
|-------------|-------------------|------|
| **User ID** | `id` | ⚠️ 必填，对应 NodeBB UID |
| **Username** | `username` | ⚠️ 必填，用户名 |
| **Email** | `email` | 可选，邮箱 |
| **First Name** | `firstName` | 可选，名字 |
| **Last Name** | `lastName` | 可选，姓氏 |
| **Picture** | `picture` | 可选，头像 URL |

#### 安全选项

| 配置项 | 推荐值 | 说明 |
|--------|--------|------|
| **Guest Handling** | `Make them a guest` | 未登录用户处理方式 |
| **Behaviour when payload is invalid** | `Ignore the cookie` | JWT 无效时的行为 |
| **Revalidation Interval** | `3600` | 重新验证间隔（秒） |

### 4. 保存配置

点击页面底部的 **Save** 按钮。

---

## 🔧 配置示例（完整配置截图说明）

```yaml
# JWT Secret（最重要！）
JWT Secret: your-strong-secret-key-at-least-32-chars

# Cookie Settings
Cookie Name: token
Cookie Domain: （留空或 .example.com）
Cookie Path: /

# Payload Keys（字段映射）
User ID: id
Username: username
Email: email
First Name: firstName
Last Name: lastName
Picture: picture
```

---

## 🎯 验证配置

### 1. 检查配置是否生效

```bash
# 查看 NodeBB 数据库中的插件配置（PostgreSQL）
psql -U postgres -d nodebb -c "SELECT * FROM objects WHERE _key = 'settings:session-sharing';"
```

### 2. 测试 JWT Token

生成一个测试 Token（使用你的 JWT Secret）：

```javascript
// 在浏览器控制台或 Node.js 中执行
const payload = {
  id: 2,  // NodeBB UID
  username: "testuser",
  email: "test@example.com",
  iat: Math.floor(Date.now() / 1000),
  exp: Math.floor(Date.now() / 1000) + 604800
};

// 使用 jwt.io 网站生成，或使用以下命令：
// echo '{"alg":"HS256","typ":"JWT"}' | base64
```

访问：https://jwt.io/ 
- 粘贴你的 JWT Secret
- 生成测试 Token
- 在浏览器中设置 Cookie 测试

### 3. 手动测试 SSO

```bash
# 1. 在浏览器中访问 NodeBB
http://localhost:4567

# 2. 打开浏览器开发者工具 → Console，执行：
document.cookie = "token=你的JWT_Token; path=/; max-age=604800";

# 3. 刷新页面，如果配置正确，你应该已经登录
```

---

## ⚠️ 常见问题排查

### 问题 1: 找不到插件配置页面

**解决方案：**
- 确认插件已激活（`nodebb-plugin-session-sharing` 在活跃插件列表中）
- 尝试清除浏览器缓存并重新登录后台
- 重新构建 NodeBB：`cd /Users/admin/ma\ jia/NodeBB && ./nodebb build && ./nodebb restart`

### 问题 2: JWT Secret 输入后无法保存

**解决方案：**
- JWT Secret 必须至少 8 个字符
- 确保没有特殊字符导致问题
- 建议使用 32 位以上的随机字符串

### 问题 3: 设置 Cookie 后仍未登录

**可能原因：**
1. JWT Secret 不匹配
2. Cookie Domain 设置错误（跨域时需要设置主域名）
3. JWT Token 已过期
4. 用户字段映射不正确（id/username 必填）

**调试步骤：**
```bash
# 查看 NodeBB 日志
tail -f /Users/admin/ma\ jia/NodeBB/logs/output.log

# 查看浏览器 Console 是否有错误信息
# 查看 Network 面板，检查 Cookie 是否正确设置
```

### 问题 4: 用户 ID 不匹配

**确保：**
- JWT 中的 `id` 必须是 NodeBB 中已存在的用户 UID（整数）
- 用户必须先通过 yudao-module-member 注册并同步到 NodeBB
- 检查 `member_nodebb_user` 表中的 `nodebb_uid` 字段

---

## 🔐 安全建议

1. **JWT Secret 保密**
   - 使用强随机字符串（建议 64 字符）
   - 不要提交到代码仓库
   - 生产环境使用环境变量

2. **HTTPS 必须启用**
   - 生产环境必须使用 HTTPS
   - Cookie 设置 `Secure` 标志

3. **Token 过期时间**
   - 建议不超过 7 天（604800 秒）
   - 根据安全需求调整

4. **Cookie Domain 设置**
   - 单域：留空或不设置
   - 跨子域：设置为主域名（如 `.example.com`）
   - ⚠️ 不要设置错误的域名，否则 Cookie 无法读取

---

## 📚 参考资料

- [nodebb-plugin-session-sharing GitHub](https://github.com/NodeBB/nodebb-plugin-session-sharing)
- [NodeBB Plugin Development](https://docs.nodebb.org/development/plugins/)
- [JWT.io - JWT 调试工具](https://jwt.io/)

---

## ✅ 配置完成检查清单

- [ ] JWT Secret 已设置（与 yudao 配置一致）
- [ ] Cookie Name 设置为 `token`
- [ ] Cookie Domain 正确配置（单域留空）
- [ ] 用户字段映射已配置（id → id, username → username）
- [ ] 配置已保存
- [ ] NodeBB 已重启
- [ ] 测试 SSO 登录成功

---

## 🎉 下一步

配置完成后，请按照 `NodeBB-Integration-README.md` 中的说明：

1. 配置 `application-local.yaml` 中的 NodeBB 相关配置
2. 执行 SQL 建表语句
3. 重启 yudao 应用
4. 测试用户注册同步
5. 测试 SSO 单点登录

祝配置顺利！🚀


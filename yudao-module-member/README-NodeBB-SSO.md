# NodeBB SSO 集成 - 快速开始

## 🎯 方案选择

本系统提供两种 SSO 方案，**可以同时启用**：

| 方案 | 说明 | 使用场景 |
|------|------|---------|
| **方案A** | Session Sharing<br/>(JWT Cookie) | 用户从 ruoyi 主应用进入论坛 |
| **方案B** | OAuth2 Provider<br/>(标准授权码模式) | 用户直接访问 NodeBB URL |

---

## 🚀 10分钟快速配置

### 方式一：自动化脚本（推荐）⚡

```bash
cd "/Users/admin/ma jia/ruoyi-vue-pro/yudao-module-member"
./setup-nodebb-dual-sso.sh
```

脚本会自动完成：
- ✅ 数据库建表
- ✅ 生成 JWT Secret
- ✅ 配置两个 NodeBB 插件
- ✅ 生成 yudao 配置文件

### 方式二：手动配置

详见完整文档：[NodeBB-Dual-SSO-Integration.md](./NodeBB-Dual-SSO-Integration.md)

---

## 📦 已安装的组件

### NodeBB 插件
- ✅ `nodebb-plugin-session-sharing` - 方案A
- ✅ `nodebb-plugin-sso-oauth` - 方案B

### 后端接口
- ✅ `GET /member/auth/nodebb-sso` - 获取 Session Sharing Token
- ✅ `GET /member/oauth2/user` - OAuth2 用户信息
- ✅ 用户自动同步服务

### 数据库
- ✅ `member_nodebb_user` - 用户映射表

---

## 🔧 最小配置（application-local.yaml）

```yaml
yudao:
  member:
    nodebb:
      enabled: true
      url: http://localhost:4567
      master-token: 【你的Master_Token】
      admin-uid: 1
      sso-secret: 【你的JWT_Secret】
      sso-cookie-name: token
      default-password: "DefaultPass123!"
```

**获取配置值：**
1. **Master Token**: 访问 http://localhost:4567/admin/settings/api 生成
2. **JWT Secret**: 运行 `node update-jwt-secret.js` 生成

---

## 📝 配置步骤总览

1. **执行建表SQL** (1分钟)
   ```bash
   psql -U postgres -d your_db -f sql/postgresql/member_nodebb_user.sql
   ```

2. **配置 JWT Secret** (2分钟)
   ```bash
   cd "/Users/admin/ma jia/NodeBB"
   node update-jwt-secret.js
   ```

3. **创建 OAuth2 Client** (2分钟)
   - 在 ruoyi 后台或执行 SQL（见完整文档）

4. **配置 yudao** (3分钟)
   - 编辑 application-local.yaml 添加配置

5. **重启服务** (2分钟)
   ```bash
   # NodeBB
   cd "/Users/admin/ma jia/NodeBB" && ./nodebb restart
   
   # yudao
   cd yudao-server && mvn clean package -DskipTests && java -jar target/*.jar
   ```

---

## 🧪 测试验证

### 测试方案A：

```bash
# 1. 登录获取 token
curl -X POST http://localhost:8080/app-api/member/auth/login \
  -H "Content-Type: application/json" \
  -d '{"mobile":"13800138000","password":"123456"}'

# 2. 获取 SSO Token
curl http://localhost:8080/app-api/member/auth/nodebb-sso \
  -H "Authorization: Bearer <accessToken>"

# 3. 前端设置 Cookie 并跳转（见文档示例代码）
```

### 测试方案B：

1. 直接访问 http://localhost:4567
2. 点击 "使用 RuoYi Member 登录"
3. 在 ruoyi 授权后自动回到 NodeBB

---

## 📚 完整文档

- **[NodeBB-Dual-SSO-Integration.md](./NodeBB-Dual-SSO-Integration.md)** - 完整集成指南
  - 详细配置步骤
  - 前端集成代码（Vue/React）
  - API 文档
  - 故障排查

- **[NodeBB-Quick-Setup.md](./NodeBB-Quick-Setup.md)** - 快速配置向导
  - Session Sharing 配置
  - 常见问题

- **[NodeBB-Integration-README.md](./NodeBB-Integration-README.md)** - 原始设计文档
  - 架构说明
  - 安全建议

---

## 🔄 工作流程图

### 方案A：Session Sharing

```
用户登录 ruoyi
    ↓
前端: fetch('/member/auth/nodebb-sso')
    ↓
后端: 生成 JWT (包含 nodebbUid + username)
    ↓
前端: document.cookie = 'token=' + JWT
    ↓
前端: window.location.href = nodebbUrl
    ↓
NodeBB: 读取 Cookie JWT → 验证签名 → 自动登录
```

### 方案B：OAuth2

```
用户访问 http://localhost:4567 → 未登录
    ↓
NodeBB: 跳转 ruoyi OAuth2 授权页面
    ↓
用户在 ruoyi 登录/授权
    ↓
ruoyi: 回调 NodeBB (code)
    ↓
NodeBB: 用 code 换 access_token
    ↓
NodeBB: 用 token 调用 /member/oauth2/user 获取用户信息
    ↓
NodeBB: 创建/登录用户
```

---

## ⚠️ 常见问题

**Q: 两种方案可以同时启用吗？**  
A: 可以！推荐同时启用，满足不同入口需求。

**Q: 如果只想用一种方案怎么办？**  
A: 
- 只用方案A：配置 yudao 即可，忽略 OAuth2 部分
- 只用方案B：只配置 OAuth2 Client，不调用 `/member/auth/nodebb-sso`

**Q: 用户需要在 ruoyi 和 NodeBB 分别注册吗？**  
A: 不需要！用户在 ruoyi 注册后会自动同步到 NodeBB。

**Q: 忘记 JWT Secret 怎么办？**  
A: 重新运行 `node update-jwt-secret.js`，同时更新 yudao 配置。

**Q: OAuth2 授权失败？**  
A: 检查 OAuth2 Client 的 redirect_uri 是否完全匹配。

---

## 🆘 获取帮助

- 查看完整文档：[NodeBB-Dual-SSO-Integration.md](./NodeBB-Dual-SSO-Integration.md)
- 查看日志：
  - yudao: `tail -f yudao-server/logs/*.log | grep NodeBB`
  - NodeBB: `tail -f /Users/admin/ma\ jia/NodeBB/logs/output.log`

---

## 🎉 开始使用

```bash
# 运行自动化配置脚本
cd "/Users/admin/ma jia/ruoyi-vue-pro/yudao-module-member"
./setup-nodebb-dual-sso.sh

# 或者查看完整手动配置文档
cat NodeBB-Dual-SSO-Integration.md
```

祝配置顺利！🚀


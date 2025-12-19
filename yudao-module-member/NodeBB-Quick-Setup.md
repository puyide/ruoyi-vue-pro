# NodeBB 快速配置完成指南

## ✅ 已完成的工作

1. ✅ `nodebb-plugin-session-sharing` 插件已安装并激活
2. ✅ 插件基础配置已保存到数据库
3. ✅ NodeBB 已重启并加载插件

---

## 🚀 剩余步骤（5分钟完成）

### 步骤 1：设置 JWT Secret（2分钟）

在终端执行以下命令：

```bash
cd "/Users/admin/ma jia/NodeBB"
node update-jwt-secret.js
```

**选择选项 2（自动生成）** - 这是最安全的方式

脚本会：
- 生成一个 64 字符的强随机密钥
- 自动保存到 NodeBB 配置
- 显示完整的 yudao 配置示例

**⚠️ 重要：请复制并保存生成的 JWT Secret！**

---

### 步骤 2：获取 NodeBB Master Token（1分钟）

1. 访问：http://localhost:4567/admin/settings/api
2. 点击 **Generate Token** 按钮
3. 选择 **Master Token**（不关联特定用户）
4. 复制生成的 Token

示例 Token 格式：
```
abc123-def456-ghi789-jkl012
```

---

### 步骤 3：配置 yudao 应用（2分钟）

编辑文件：`yudao-server/src/main/resources/application-local.yaml`

添加以下配置（**替换标记的值**）：

```yaml
yudao:
  member:
    nodebb:
      enabled: true
      url: http://localhost:4567
      master-token: 【步骤2中复制的Token】
      admin-uid: 1
      sso-secret: 【步骤1中生成的JWT_Secret】
      sso-cookie-name: token
      sso-cookie-domain: ""
      default-password: "DefaultPass123!"
```

---

### 步骤 4：执行数据库建表

```bash
cd "/Users/admin/ma jia/ruoyi-vue-pro"

# PostgreSQL
psql -U postgres -d 你的数据库名 -f sql/postgresql/member_nodebb_user.sql
```

---

### 步骤 5：重启服务

```bash
# 重启 NodeBB（如果步骤1未自动重启）
cd "/Users/admin/ma jia/NodeBB"
./nodebb restart

# 重启 yudao 应用
cd "/Users/admin/ma jia/ruoyi-vue-pro/yudao-server"
mvn clean package -DskipTests
java -jar target/*.jar
```

---

## 🧪 测试验证

### 测试 1：用户注册同步

1. 注册一个新会员用户
2. 查看数据库：

```sql
SELECT * FROM member_nodebb_user ORDER BY create_time DESC LIMIT 1;
```

应该看到新创建的映射记录，`sync_status = 1` 表示成功。

### 测试 2：SSO 单点登录

```bash
# 1. 会员登录获取 access_token
curl -X POST http://localhost:8080/member/auth/login \
  -H "Content-Type: application/json" \
  -d '{"mobile":"13800138000","password":"123456"}'

# 2. 获取 SSO Token
curl http://localhost:8080/member/auth/nodebb-sso \
  -H "Authorization: Bearer <上一步的accessToken>"

# 3. 前端设置 Cookie 并跳转到 NodeBB
# 见下方前端代码示例
```

---

## 💻 前端集成示例

### Vue 3 示例

```javascript
// 获取 SSO Token 并跳转到论坛
async function goToForum() {
  try {
    const res = await fetch('/member/auth/nodebb-sso', {
      headers: {
        'Authorization': 'Bearer ' + localStorage.getItem('accessToken')
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
      
      // 跳转到 NodeBB（用户将自动登录）
      window.location.href = sso.nodebbUrl;
    }
  } catch (error) {
    console.error('SSO 跳转失败:', error);
  }
}
```

### React 示例

```jsx
const handleGoToForum = async () => {
  try {
    const response = await fetch('/member/auth/nodebb-sso', {
      headers: {
        'Authorization': `Bearer ${accessToken}`
      }
    });
    
    const { code, data } = await response.json();
    if (code === 0) {
      // 设置 Cookie
      document.cookie = `${data.cookieName}=${data.token}; path=/; max-age=${data.maxAge}`;
      // 跳转
      window.location.href = data.nodebbUrl;
    }
  } catch (error) {
    console.error('Forum SSO error:', error);
  }
};

// 在按钮中使用
<button onClick={handleGoToForum}>进入论坛</button>
```

---

## ❓ 常见问题

### Q1: JWT Secret 忘记了怎么办？

**答：** 重新运行配置脚本生成新的即可：

```bash
cd "/Users/admin/ma jia/NodeBB"
node update-jwt-secret.js
```

记得同步更新 yudao 配置中的 `sso-secret`。

### Q2: Master Token 在哪里获取？

**答：** 
1. 访问：http://localhost:4567/admin/settings/api
2. 如果之前生成过，可以在列表中看到
3. 如果找不到，点击 "Generate Token" 生成新的

### Q3: 用户同步失败怎么办？

**答：** 检查 `member_nodebb_user` 表中的 `sync_error_msg` 字段查看失败原因，常见问题：
- Master Token 无效
- NodeBB 服务未运行
- 网络连接问题

查看日志：
```bash
# yudao 日志
tail -f yudao-server/logs/*.log | grep NodeBB

# NodeBB 日志  
tail -f /Users/admin/ma\ jia/NodeBB/logs/output.log
```

### Q4: SSO 登录不成功？

**答：** 检查清单：
- [ ] JWT Secret 在 NodeBB 和 yudao 中是否完全一致
- [ ] Cookie Name 是否都是 `token`
- [ ] 用户是否已同步到 NodeBB（检查 member_nodebb_user 表）
- [ ] 浏览器 Cookie 是否正确设置（F12 → Application → Cookies）
- [ ] JWT Token 是否过期

---

## 📞 获取帮助

如遇问题，请提供：
1. yudao 应用日志（搜索 `[NodeBB`）
2. NodeBB 日志（`logs/output.log`）
3. 错误截图

---

## 🎉 配置完成

完成上述步骤后，你的 NodeBB 集成就配置完成了！

用户注册后会自动在 NodeBB 创建账号，登录后可以无感跳转到论坛并自动登录。

祝使用愉快！🚀


# OAuth2 Client 创建失败 - 快速修复

## ❌ 错误信息

```
ERROR: column "tenant_id" of relation "system_oauth2_client" does not exist
```

## 💡 原因

你的 `system_oauth2_client` 表没有 `tenant_id` 字段（非租户版本）。

---

## ✅ 解决方案：使用修正后的 SQL

### 方式 1：复制执行（最快）⚡

在你的数据库工具中执行以下 SQL：

```sql
-- PostgreSQL 版本（不含 tenant_id）
INSERT INTO system_oauth2_client (
    client_id, secret, name, logo, description,
    status, access_token_validity_seconds, refresh_token_validity_seconds,
    redirect_uris, authorized_grant_types, scopes, auto_approve_scopes,
    creator, create_time, updater, update_time, deleted
) VALUES (
    'nodebb',
    'nodebb-secret-change-me-in-production',
    'NodeBB Forum',
    '',
    'NodeBB 论坛 SSO 集成',
    0,
    604800,
    2592000,
    '["http://localhost:4567/auth/nodebb/callback"]',
    '["authorization_code","refresh_token"]',
    '["user.read"]',
    '["user.read"]',
    '1',
    NOW(),
    '1',
    NOW(),
    0
);
```

**验证：**
```sql
SELECT * FROM system_oauth2_client WHERE client_id = 'nodebb';
```

---

### 方式 2：使用智能 SQL（自动适配）✨

我已经为你准备了自动检测表结构的 SQL：

```bash
# 执行智能 SQL
cat /Users/admin/ma\ jia/ruoyi-vue-pro/sql/postgresql/create-oauth2-client-for-nodebb-fixed.sql
```

在你的数据库工具中打开并执行该文件，它会：
- ✅ 自动检测是否有 `tenant_id` 字段
- ✅ 自动选择正确的 SQL
- ✅ 如果记录已存在，则更新而不是报错

---

## 🎯 执行后的下一步

### 1. 验证 OAuth2 Client 创建成功

```sql
SELECT 
    client_id,
    name,
    redirect_uris,
    authorized_grant_types,
    scopes,
    auto_approve_scopes
FROM system_oauth2_client 
WHERE client_id = 'nodebb';
```

应该看到一条记录。

### 2. 获取 NodeBB Master Token（1分钟）

访问：http://localhost:4567/admin/settings/api

- 点击 **Generate Token**
- 选择 **Master Token**
- 复制 Token

### 3. 配置 yudao（1分钟）

编辑：`yudao-server/src/main/resources/application-nodebb-auto.yaml`

替换：
```yaml
master-token: "请替换为你的Master_Token"
```

改为：
```yaml
master-token: "【刚才复制的Token】"
```

然后在 `application-local.yaml` 中添加：
```yaml
spring:
  profiles:
    include: nodebb-auto
```

### 4. 重启 yudao（2分钟）

```bash
cd "/Users/admin/ma jia/ruoyi-vue-pro/yudao-server"
mvn clean package -DskipTests
java -jar target/*.jar
```

---

## 🎉 配置完成后测试

### 测试方案A：Session Sharing

```bash
# 1. 登录
curl -X POST http://localhost:8080/app-api/member/auth/login \
  -H "Content-Type: application/json" \
  -d '{"mobile":"13800138000","password":"123456"}'

# 2. 获取 SSO Token
curl http://localhost:8080/app-api/member/auth/nodebb-sso \
  -H "Authorization: Bearer <accessToken>"
```

### 测试方案B：OAuth2

1. 访问：http://localhost:4567
2. 点击登录
3. 选择 "使用 RuoYi Member System 登录"
4. 授权后自动回到 NodeBB

---

## 📝 完整配置清单

- [x] ✅ member_nodebb_user 表已创建
- [x] ✅ JWT Secret 已生成：`T6oRQn1lIBnUo1ZhlvT11JQf8DVUnGUdHxNHCX2xPVMowDpU/CXKupAm0cqfWAGx`
- [x] ✅ NodeBB OAuth2 插件已配置
- [x] ✅ NodeBB 已重启
- [ ] ⏳ 创建 OAuth2 Client（执行上方 SQL）
- [ ] ⏳ 获取 Master Token
- [ ] ⏳ 配置 yudao master-token
- [ ] ⏳ 重启 yudao

---

**请执行上方的修正 SQL，然后继续步骤 2-4** 🚀


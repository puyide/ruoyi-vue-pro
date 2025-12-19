-- ✅ 修正版：为 NodeBB 创建 OAuth2 Client
-- 在你的数据库工具中复制执行以下 SQL
-- 会自动检查并处理已存在的情况

-- 方案：先删除可能存在的 nodebb client，然后重新插入
-- 这样可以避免主键冲突

-- 1. 删除已存在的 nodebb client（如果有）
DELETE FROM system_oauth2_client WHERE client_id = 'nodebb';

-- 2. 插入新的 nodebb client（不指定 id，让数据库自动生成）
INSERT INTO system_oauth2_client (
    client_id, 
    secret, 
    name, 
    logo, 
    description,
    status, 
    access_token_validity_seconds, 
    refresh_token_validity_seconds,
    redirect_uris, 
    authorized_grant_types, 
    scopes, 
    auto_approve_scopes,
    creator, 
    create_time, 
    updater, 
    update_time, 
    deleted
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

-- 3. 验证创建结果
SELECT 
    id,
    client_id,
    name,
    status,
    redirect_uris,
    authorized_grant_types,
    scopes,
    auto_approve_scopes
FROM system_oauth2_client 
WHERE client_id = 'nodebb';

-- 应该看到一条记录，显示 ✓


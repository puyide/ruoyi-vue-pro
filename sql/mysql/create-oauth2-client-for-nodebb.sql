-- 为 NodeBB 创建 OAuth2 Client（MySQL版本）
-- 在 ruoyi 数据库中执行

-- 检查是否已存在
SET @exists = (SELECT COUNT(*) FROM system_oauth2_client WHERE client_id = 'nodebb');

-- 如果不存在则插入，存在则更新
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
    604800,    -- 7天
    2592000,   -- 30天
    '["http://localhost:4567/auth/nodebb/callback"]',
    '["authorization_code","refresh_token"]',
    '["user.read"]',
    '["user.read"]',
    '1',
    NOW(),
    '1',
    NOW(),
    0
) ON DUPLICATE KEY UPDATE
    secret = VALUES(secret),
    name = VALUES(name),
    description = VALUES(description),
    status = VALUES(status),
    access_token_validity_seconds = VALUES(access_token_validity_seconds),
    refresh_token_validity_seconds = VALUES(refresh_token_validity_seconds),
    redirect_uris = VALUES(redirect_uris),
    authorized_grant_types = VALUES(authorized_grant_types),
    scopes = VALUES(scopes),
    auto_approve_scopes = VALUES(auto_approve_scopes),
    updater = '1',
    update_time = NOW();

-- 如果你的表有 tenant_id 字段，使用以下版本：
/*
INSERT INTO system_oauth2_client (
    client_id, secret, name, logo, description,
    status, access_token_validity_seconds, refresh_token_validity_seconds,
    redirect_uris, authorized_grant_types, scopes, auto_approve_scopes,
    creator, create_time, updater, update_time, deleted, tenant_id
) VALUES (
    'nodebb',
    'nodebb-secret-change-me-in-production',
    'NodeBB Forum',
    '',
    'NodeBB 论坛 SSO 集成',
    0, 604800, 2592000,
    '["http://localhost:4567/auth/nodebb/callback"]',
    '["authorization_code","refresh_token"]',
    '["user.read"]', '["user.read"]',
    '1', NOW(), '1', NOW(), 0, 1
) ON DUPLICATE KEY UPDATE
    secret = VALUES(secret),
    name = VALUES(name),
    description = VALUES(description),
    updater = '1',
    update_time = NOW();
*/

-- 验证创建结果
SELECT 
    client_id,
    name,
    status,
    redirect_uris,
    authorized_grant_types,
    scopes
FROM system_oauth2_client 
WHERE client_id = 'nodebb';


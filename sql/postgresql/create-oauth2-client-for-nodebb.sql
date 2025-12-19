-- 为 NodeBB 创建 OAuth2 Client
-- 执行前请确认 system_oauth2_client 表已存在

-- 检查是否已存在
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM system_oauth2_client WHERE client_id = 'nodebb') THEN
        RAISE NOTICE '⚠️  OAuth2 Client 已存在，将更新配置';
        
        UPDATE system_oauth2_client SET
            secret = 'nodebb-secret-change-me-in-production',
            name = 'NodeBB Forum',
            description = 'NodeBB 论坛 SSO 集成',
            status = 0,
            access_token_validity_seconds = 604800,
            refresh_token_validity_seconds = 2592000,
            redirect_uris = '["http://localhost:4567/auth/nodebb/callback"]',
            authorized_grant_types = '["authorization_code","refresh_token"]',
            scopes = '["user.read"]',
            auto_approve_scopes = '["user.read"]',
            updater = '1',
            update_time = NOW()
        WHERE client_id = 'nodebb';
        
        RAISE NOTICE '✅ OAuth2 Client 已更新';
    ELSE
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
            0,
            1
        );
        
        RAISE NOTICE '✅ OAuth2 Client 已创建';
    END IF;
END $$;

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


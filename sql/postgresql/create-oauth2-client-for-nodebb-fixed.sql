-- 为 NodeBB 创建 OAuth2 Client（自动适配 tenant_id）
-- 在 ruoyi 数据库中执行

-- 方案1：如果表有 tenant_id 字段（带租户系统）
-- 如果执行报错"column tenant_id does not exist"，请使用下方的方案2

-- 先检查记录是否存在
DO $$
DECLARE
    has_tenant_id BOOLEAN;
    record_exists BOOLEAN;
BEGIN
    -- 检查表是否有 tenant_id 列
    SELECT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'system_oauth2_client' 
        AND column_name = 'tenant_id'
    ) INTO has_tenant_id;
    
    -- 检查记录是否已存在
    SELECT EXISTS (
        SELECT 1 FROM system_oauth2_client WHERE client_id = 'nodebb'
    ) INTO record_exists;
    
    IF record_exists THEN
        RAISE NOTICE '⚠️  OAuth2 Client nodebb 已存在，正在更新...';
        
        -- 更新现有记录
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
        RAISE NOTICE '创建新的 OAuth2 Client...';
        
        -- 根据是否有 tenant_id 字段执行不同的插入
        IF has_tenant_id THEN
            -- 有 tenant_id 字段
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
                '["user.read"]',
                '["user.read"]',
                '1', NOW(), '1', NOW(), 0, 1
            );
            RAISE NOTICE '✅ OAuth2 Client 已创建（带 tenant_id）';
        ELSE
            -- 无 tenant_id 字段
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
                0, 604800, 2592000,
                '["http://localhost:4567/auth/nodebb/callback"]',
                '["authorization_code","refresh_token"]',
                '["user.read"]',
                '["user.read"]',
                '1', NOW(), '1', NOW(), 0
            );
            RAISE NOTICE '✅ OAuth2 Client 已创建（无 tenant_id）';
        END IF;
    END IF;
END $$;

-- 验证创建结果
SELECT 
    client_id,
    name,
    status,
    redirect_uris,
    authorized_grant_types,
    scopes,
    auto_approve_scopes
FROM system_oauth2_client 
WHERE client_id = 'nodebb';


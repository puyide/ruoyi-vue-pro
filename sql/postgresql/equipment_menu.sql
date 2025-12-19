-- =============================================
-- 康复训练器材共享平台 - 管理后台菜单配置
-- 数据库：PostgreSQL
-- =============================================

-- =============================================
-- 步骤1：修复序列值（重要！）
-- =============================================
-- 将序列设置为当前最大ID+1
SELECT setval('system_menu_seq', (SELECT COALESCE(MAX(id), 0) + 1 FROM system_menu), false);

-- =============================================
-- 步骤2：查询现有菜单结构，获取父级ID
-- =============================================
SELECT id, name, parent_id FROM system_menu 
WHERE name IN ('商城系统', '交易中心', '会员中心', '商品中心', '订单管理') 
AND deleted = 0
ORDER BY id;

-- =============================================
-- 步骤3：根据查询结果修改下面的 parent_id
-- =============================================
-- 假设查询结果：
-- 交易中心 ID = 2015 （请根据实际查询结果修改）
-- 会员中心 ID = 2010 （请根据实际查询结果修改）

-- =============================================
-- 步骤4：执行以下INSERT语句
-- =============================================

-- 1. 器材预约菜单（在交易中心下）
INSERT INTO system_menu (
    name, permission, type, sort, parent_id, path, icon, 
    component, component_name, status, visible, keep_alive, 
    always_show, creator, create_time, updater, update_time, deleted
) VALUES (
    '器材预约', 
    'product:reservation:query', 
    2,    -- type: 2=菜单
    20,   -- sort: 排序
    2015, -- ⚠️ parent_id: 改为实际的交易中心菜单ID
    'reservation', 
    'ep:calendar', 
    'mall/trade/reservation/index', 
    'EquipmentReservation', 
    0,    -- status: 0=正常
    '1',  -- visible
    '1',  -- keep_alive
    '1',  -- always_show
    'admin', NOW(), 'admin', NOW(), 0
);

-- 2. 交接会话菜单
INSERT INTO system_menu (
    name, permission, type, sort, parent_id, path, icon, 
    component, component_name, status, visible, keep_alive, 
    always_show, creator, create_time, updater, update_time, deleted
) VALUES (
    '交接会话', 
    'trade:handover:query', 
    2, 
    21, 
    2015, -- ⚠️ parent_id: 改为实际的交易中心菜单ID
    'handover', 
    'ep:chat-line-round', 
    'mall/trade/handover/index', 
    'EquipmentHandover', 
    0, '1', '1', '1', 
    'admin', NOW(), 'admin', NOW(), 0
);

-- 3. 交接详情菜单（隐藏）
INSERT INTO system_menu (
    name, permission, type, sort, parent_id, path, icon, 
    component, component_name, status, visible, keep_alive, 
    always_show, creator, create_time, updater, update_time, deleted
) VALUES (
    '交接详情', 
    'trade:handover:query', 
    2, 
    22, 
    2015, -- ⚠️ parent_id: 改为实际的交易中心菜单ID
    'handover/detail/:id', 
    '', 
    'mall/trade/handover/detail/index', 
    'EquipmentHandoverDetail', 
    0, 
    '0',  -- visible: '0'=隐藏
    '1', '1', 
    'admin', NOW(), 'admin', NOW(), 0
);

-- 4. 信用分管理菜单（在会员中心下）
INSERT INTO system_menu (
    name, permission, type, sort, parent_id, path, icon, 
    component, component_name, status, visible, keep_alive, 
    always_show, creator, create_time, updater, update_time, deleted
) VALUES (
    '信用分管理', 
    'member:credit:query', 
    2, 
    10, 
    2010, -- ⚠️ parent_id: 改为实际的会员中心菜单ID
    'credit', 
    'ep:medal', 
    'mall/trade/credit/index', 
    'MemberCredit', 
    0, '1', '1', '1', 
    'admin', NOW(), 'admin', NOW(), 0
);

-- =============================================
-- 步骤5：验证新增的菜单
-- =============================================
SELECT id, name, path, component, parent_id FROM system_menu 
WHERE name IN ('器材预约', '交接会话', '交接详情', '信用分管理')
AND deleted = 0;

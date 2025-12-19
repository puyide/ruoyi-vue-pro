-- 社区内容审核菜单 SQL
-- 执行前请确保菜单父ID正确

-- 获取会员管理菜单ID（假设为 2000，请根据实际情况调整）
-- SELECT id FROM system_menu WHERE name = '会员中心';

-- 先删除已存在的社区菜单（如果有）
DELETE FROM system_menu WHERE id IN (2300, 2301, 2302, 2303, 2304, 2305, 2306, 2307, 2308, 2309);

-- 社区管理目录
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES (2300, '社区管理', '', 1, 50, 2000, 'community', 'ep:chat-round', '', '', 0, '1', '1', '1', '1', NOW(), '1', NOW(), '0');

-- 帖子管理
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES (2301, '帖子管理', 'member:community:query', 2, 1, 2300, 'topic', 'ep:document', 'member/community/topic/index', 'CommunityTopic', 0, '1', '1', '1', '1', NOW(), '1', NOW(), '0');

-- 回帖管理
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES (2302, '回帖管理', 'member:community:query', 2, 2, 2300, 'post', 'ep:comment', 'member/community/post/index', 'CommunityPost', 0, '1', '1', '1', '1', NOW(), '1', NOW(), '0');

-- 操作权限按钮
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES (2303, '帖子查看', 'member:community:query', 3, 1, 2301, '', '', '', '', 0, '1', '1', '1', '1', NOW(), '1', NOW(), '0');

INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES (2304, '帖子审核', 'member:community:audit', 3, 2, 2301, '', '', '', '', 0, '1', '1', '1', '1', NOW(), '1', NOW(), '0');

INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES (2305, '帖子更新', 'member:community:update', 3, 3, 2301, '', '', '', '', 0, '1', '1', '1', '1', NOW(), '1', NOW(), '0');

INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES (2306, '帖子删除', 'member:community:delete', 3, 4, 2301, '', '', '', '', 0, '1', '1', '1', '1', NOW(), '1', NOW(), '0');

INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES (2307, '回帖查看', 'member:community:query', 3, 1, 2302, '', '', '', '', 0, '1', '1', '1', '1', NOW(), '1', NOW(), '0');

INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES (2308, '回帖审核', 'member:community:audit', 3, 2, 2302, '', '', '', '', 0, '1', '1', '1', '1', NOW(), '1', NOW(), '0');

INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES (2309, '回帖删除', 'member:community:delete', 3, 3, 2302, '', '', '', '', 0, '1', '1', '1', '1', NOW(), '1', NOW(), '0');

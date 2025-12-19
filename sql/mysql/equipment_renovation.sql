-- ----------------------------
-- 康复训练器材共享平台 - 数据库改造脚本 (MySQL)
-- 改造说明: 将商城模块改造为自闭症康复训练器材的赠送/借用平台
-- 创建日期: 2025-12-15
-- ----------------------------

-- ========================================
-- 1. 修改商品表(product_spu) - 改造为器材表
-- ========================================

-- 新增器材特有字段
ALTER TABLE product_spu ADD COLUMN equipment_status TINYINT DEFAULT 1 
    COMMENT '器材状态: 1-全新, 2-九成新, 3-八成新, 4-七成新以下';

ALTER TABLE product_spu ADD COLUMN suitable_age_range VARCHAR(50) DEFAULT NULL 
    COMMENT '适用年龄段: 例如 0-3岁, 3-6岁, 6-12岁';

ALTER TABLE product_spu ADD COLUMN training_types VARCHAR(200) DEFAULT NULL 
    COMMENT '适用训练类型(多选,逗号分隔): 感统,语言,认知,社交,精细动作';

ALTER TABLE product_spu ADD COLUMN usage_count INT DEFAULT 0 
    COMMENT '使用次数统计';

ALTER TABLE product_spu ADD COLUMN last_disinfection_date DATETIME DEFAULT NULL 
    COMMENT '最近消毒时间';

ALTER TABLE product_spu ADD COLUMN owner_user_id BIGINT DEFAULT NULL 
    COMMENT '器材拥有者ID(会员ID)';

ALTER TABLE product_spu ADD COLUMN share_type TINYINT DEFAULT 1 
    COMMENT '共享类型: 1-仅赠送, 2-仅借用, 3-赠送或借用';

ALTER TABLE product_spu ADD COLUMN deposit_amount INT DEFAULT 0 
    COMMENT '押金金额(单位:分,仅借用时需要)';

ALTER TABLE product_spu ADD COLUMN borrow_duration INT DEFAULT 30 
    COMMENT '建议借用时长(单位:天)';

ALTER TABLE product_spu ADD COLUMN equipment_location VARCHAR(200) DEFAULT NULL 
    COMMENT '器材所在位置(省市区)';

-- 为新字段添加索引
ALTER TABLE product_spu ADD INDEX idx_owner_user_id(owner_user_id);
ALTER TABLE product_spu ADD INDEX idx_share_type(share_type);
ALTER TABLE product_spu ADD INDEX idx_equipment_status(equipment_status);

-- ========================================
-- 2. 修改交易订单表(trade_order) - 改造为赠送/借用订单表
-- ========================================

-- 新增赠送/借用相关字段
ALTER TABLE trade_order ADD COLUMN share_type TINYINT DEFAULT 1 
    COMMENT '订单类型: 1-赠送, 2-借用';

ALTER TABLE trade_order ADD COLUMN borrow_start_date DATETIME DEFAULT NULL 
    COMMENT '借用开始时间';

ALTER TABLE trade_order ADD COLUMN borrow_end_date DATETIME DEFAULT NULL 
    COMMENT '计划归还时间';

ALTER TABLE trade_order ADD COLUMN actual_return_date DATETIME DEFAULT NULL 
    COMMENT '实际归还时间';

ALTER TABLE trade_order ADD COLUMN return_status TINYINT DEFAULT 0 
    COMMENT '归还状态: 0-未归还, 1-已归还, 2-逾期未还, 3-已损坏';

ALTER TABLE trade_order ADD COLUMN deposit_refund_status TINYINT DEFAULT 0 
    COMMENT '押金退还状态: 0-未退还, 1-已退还, 2-部分退还(扣除赔偿)';

ALTER TABLE trade_order ADD COLUMN equipment_condition TEXT DEFAULT NULL 
    COMMENT '器材状态说明(借用归还时填写)';

ALTER TABLE trade_order ADD COLUMN equipment_photos VARCHAR(1000) DEFAULT NULL 
    COMMENT '器材照片(JSON数组,借用/归还时拍照)';

ALTER TABLE trade_order ADD COLUMN overdue_days INT DEFAULT 0 
    COMMENT '逾期天数';

ALTER TABLE trade_order ADD COLUMN damage_compensation INT DEFAULT 0 
    COMMENT '损坏赔偿金额(单位:分)';

-- 为新字段添加索引
ALTER TABLE trade_order ADD INDEX idx_share_type(share_type);
ALTER TABLE trade_order ADD INDEX idx_return_status(return_status);
ALTER TABLE trade_order ADD INDEX idx_borrow_end_date(borrow_end_date);

-- ========================================
-- 3. 创建会员信用分表
-- ========================================

DROP TABLE IF EXISTS member_credit_score;
CREATE TABLE member_credit_score (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    total_score INT DEFAULT 100 NOT NULL COMMENT '总信用分(默认100分)',
    donate_count INT DEFAULT 0 COMMENT '捐赠次数',
    borrow_count INT DEFAULT 0 COMMENT '借用次数',
    return_on_time_count INT DEFAULT 0 COMMENT '按时归还次数',
    overdue_count INT DEFAULT 0 COMMENT '逾期次数',
    damage_count INT DEFAULT 0 COMMENT '损坏次数',
    violation_count INT DEFAULT 0 COMMENT '违规次数',
    score_change_reason VARCHAR(500) DEFAULT NULL COMMENT '最近一次分数变动原因',
    last_score_change_date DATETIME DEFAULT NULL COMMENT '最近一次分数变动时间',
    
    -- 基础字段
    creator VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updater VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted BIT DEFAULT 0 NOT NULL COMMENT '是否删除',
    tenant_id BIGINT DEFAULT 0 NOT NULL COMMENT '租户编号',
    
    UNIQUE KEY idx_user_id(user_id, deleted) COMMENT '用户ID唯一索引',
    KEY idx_total_score(total_score) COMMENT '信用分索引',
    KEY idx_tenant_id(tenant_id) COMMENT '租户索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会员信用分表';

-- ========================================
-- 4. 创建信用分变动记录表
-- ========================================

DROP TABLE IF EXISTS member_credit_log;
CREATE TABLE member_credit_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    change_type TINYINT NOT NULL COMMENT '变动类型: 1-捐赠(+10), 2-按时归还(+5), 3-逾期(-10), 4-损坏(-20), 5-违规(-30)',
    change_score INT NOT NULL COMMENT '变动分数(正数为增加,负数为减少)',
    before_score INT NOT NULL COMMENT '变动前分数',
    after_score INT NOT NULL COMMENT '变动后分数',
    reason VARCHAR(500) DEFAULT NULL COMMENT '变动原因描述',
    related_order_id BIGINT DEFAULT NULL COMMENT '关联订单ID',
    
    -- 基础字段
    creator VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updater VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted BIT DEFAULT 0 NOT NULL COMMENT '是否删除',
    tenant_id BIGINT DEFAULT 0 NOT NULL COMMENT '租户编号',
    
    KEY idx_user_id(user_id) COMMENT '用户ID索引',
    KEY idx_change_type(change_type) COMMENT '变动类型索引',
    KEY idx_create_time(create_time) COMMENT '创建时间索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会员信用分变动记录表';

-- ========================================
-- 5. 创建器材预约表(借用预约)
-- ========================================

DROP TABLE IF EXISTS equipment_reservation;
CREATE TABLE equipment_reservation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    equipment_id BIGINT NOT NULL COMMENT '器材ID(product_spu表ID)',
    user_id BIGINT NOT NULL COMMENT '预约用户ID',
    reservation_status TINYINT DEFAULT 0 COMMENT '预约状态: 0-待审核, 1-已同意, 2-已拒绝, 3-已取消, 4-已完成',
    reservation_date DATETIME NOT NULL COMMENT '预约时间',
    plan_borrow_date DATETIME NOT NULL COMMENT '计划借用日期',
    plan_return_date DATETIME NOT NULL COMMENT '计划归还日期',
    cancel_reason VARCHAR(500) DEFAULT NULL COMMENT '取消/拒绝原因',
    owner_user_id BIGINT NOT NULL COMMENT '器材拥有者ID',
    
    -- 基础字段
    creator VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updater VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted BIT DEFAULT 0 NOT NULL COMMENT '是否删除',
    tenant_id BIGINT DEFAULT 0 NOT NULL COMMENT '租户编号',
    
    KEY idx_equipment_id(equipment_id) COMMENT '器材ID索引',
    KEY idx_user_id(user_id) COMMENT '用户ID索引',
    KEY idx_reservation_status(reservation_status) COMMENT '预约状态索引',
    KEY idx_owner_user_id(owner_user_id) COMMENT '拥有者ID索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='器材预约表';

-- ========================================
-- 6. 插入初始数据 - 数据字典配置
-- ========================================

-- 器材状态字典
INSERT INTO system_dict_type (name, type, status, remark, creator, create_time, updater, update_time, deleted) 
VALUES ('器材状态', 'equipment_status', 0, '康复训练器材的新旧状态', 'admin', NOW(), '', NOW(), 0);

SET @dict_type_id = LAST_INSERT_ID();

INSERT INTO system_dict_data (dict_type, value, label, sort, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted) VALUES
('equipment_status', '1', '全新', 1, 0, 'success', '', '未使用过的全新器材', 'admin', NOW(), '', NOW(), 0),
('equipment_status', '2', '九成新', 2, 0, 'primary', '', '轻微使用痕迹', 'admin', NOW(), '', NOW(), 0),
('equipment_status', '3', '八成新', 3, 0, 'warning', '', '有明显使用痕迹但功能完好', 'admin', NOW(), '', NOW(), 0),
('equipment_status', '4', '七成新以下', 4, 0, 'danger', '', '使用痕迹较多', 'admin', NOW(), '', NOW(), 0);

-- 共享类型字典
INSERT INTO system_dict_type (name, type, status, remark, creator, create_time, updater, update_time, deleted) 
VALUES ('共享类型', 'share_type', 0, '器材的共享方式', 'admin', NOW(), '', NOW(), 0);

INSERT INTO system_dict_data (dict_type, value, label, sort, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted) VALUES
('share_type', '1', '仅赠送', 1, 0, 'success', '', '器材赠送给需要的家庭', 'admin', NOW(), '', NOW(), 0),
('share_type', '2', '仅借用', 2, 0, 'primary', '', '器材可借用但需归还', 'admin', NOW(), '', NOW(), 0),
('share_type', '3', '赠送或借用', 3, 0, 'info', '', '可以选择赠送或借用', 'admin', NOW(), '', NOW(), 0);

-- 训练类型字典
INSERT INTO system_dict_type (name, type, status, remark, creator, create_time, updater, update_time, deleted) 
VALUES ('训练类型', 'training_type', 0, '康复训练的类型分类', 'admin', NOW(), '', NOW(), 0);

INSERT INTO system_dict_data (dict_type, value, label, sort, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted) VALUES
('training_type', 'sensory', '感统训练', 1, 0, 'primary', '', '感觉统合训练器材', 'admin', NOW(), '', NOW(), 0),
('training_type', 'language', '语言训练', 2, 0, 'success', '', '语言能力训练器材', 'admin', NOW(), '', NOW(), 0),
('training_type', 'cognitive', '认知训练', 3, 0, 'warning', '', '认知能力训练器材', 'admin', NOW(), '', NOW(), 0),
('training_type', 'social', '社交训练', 4, 0, 'info', '', '社交能力训练器材', 'admin', NOW(), '', NOW(), 0),
('training_type', 'fine_motor', '精细动作', 5, 0, 'danger', '', '手部精细动作训练器材', 'admin', NOW(), '', NOW(), 0),
('training_type', 'gross_motor', '大运动', 6, 0, 'default', '', '大肌肉运动训练器材', 'admin', NOW(), '', NOW(), 0);

-- 归还状态字典
INSERT INTO system_dict_type (name, type, status, remark, creator, create_time, updater, update_time, deleted) 
VALUES ('归还状态', 'return_status', 0, '借用器材的归还状态', 'admin', NOW(), '', NOW(), 0);

INSERT INTO system_dict_data (dict_type, value, label, sort, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted) VALUES
('return_status', '0', '未归还', 1, 0, 'warning', '', '器材尚未归还', 'admin', NOW(), '', NOW(), 0),
('return_status', '1', '已归还', 2, 0, 'success', '', '器材已归还', 'admin', NOW(), '', NOW(), 0),
('return_status', '2', '逾期未还', 3, 0, 'danger', '', '超过约定时间未归还', 'admin', NOW(), '', NOW(), 0),
('return_status', '3', '已损坏', 4, 0, 'danger', '', '归还时发现器材损坏', 'admin', NOW(), '', NOW(), 0);

-- 信用分变动类型字典
INSERT INTO system_dict_type (name, type, status, remark, creator, create_time, updater, update_time, deleted) 
VALUES ('信用分变动类型', 'credit_change_type', 0, '会员信用分变动的类型', 'admin', NOW(), '', NOW(), 0);

INSERT INTO system_dict_data (dict_type, value, label, sort, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted) VALUES
('credit_change_type', '1', '捐赠器材', 1, 0, 'success', '', '+10分', 'admin', NOW(), '', NOW(), 0),
('credit_change_type', '2', '按时归还', 2, 0, 'success', '', '+5分', 'admin', NOW(), '', NOW(), 0),
('credit_change_type', '3', '逾期归还', 3, 0, 'danger', '', '-10分', 'admin', NOW(), '', NOW(), 0),
('credit_change_type', '4', '器材损坏', 4, 0, 'danger', '', '-20分', 'admin', NOW(), '', NOW(), 0),
('credit_change_type', '5', '违规行为', 5, 0, 'danger', '', '-30分', 'admin', NOW(), '', NOW(), 0),
('credit_change_type', '6', '优质评价', 6, 0, 'success', '', '+3分', 'admin', NOW(), '', NOW(), 0);

-- ========================================
-- 7. 创建菜单配置
-- ========================================

-- 注意: 实际部署时需要根据具体情况调整 parent_id 和 菜单ID

-- 一级菜单: 器材共享 (假设 ID 从 3000 开始)
INSERT INTO system_menu (name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted) 
VALUES ('器材共享', '', 1, 90, 0, '/equipment', 'ep:box', NULL, NULL, 0, 1, 1, 1, 'admin', NOW(), '', NOW(), 0);

SET @menu_id = LAST_INSERT_ID();

-- 二级菜单: 器材管理
INSERT INTO system_menu (name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted) 
VALUES ('器材管理', 'equipment:item:query', 2, 1, @menu_id, 'item', 'ep:goods', 'mall/product/spu/index', 'EquipmentItem', 0, 1, 1, 1, 'admin', NOW(), '', NOW(), 0);

-- 二级菜单: 赠送管理
INSERT INTO system_menu (name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted) 
VALUES ('赠送管理', 'equipment:donate:query', 2, 2, @menu_id, 'donate', 'ep:present', 'mall/trade/order/index', 'EquipmentDonate', 0, 1, 1, 1, 'admin', NOW(), '', NOW(), 0);

-- 二级菜单: 借用管理
INSERT INTO system_menu (name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted) 
VALUES ('借用管理', 'equipment:borrow:query', 2, 3, @menu_id, 'borrow', 'ep:refresh-left', 'mall/trade/order/index', 'EquipmentBorrow', 0, 1, 1, 1, 'admin', NOW(), '', NOW(), 0);

-- 二级菜单: 信用管理
INSERT INTO system_menu (name, permission, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted) 
VALUES ('信用管理', 'member:credit:query', 2, 4, @menu_id, 'credit', 'ep:medal', NULL, 'MemberCredit', 0, 1, 1, 1, 'admin', NOW(), '', NOW(), 0);

-- ========================================
-- 改造完成提示
-- ========================================

SELECT '=======================================' AS '';
SELECT '康复训练器材共享平台改造完成！' AS '';
SELECT '=======================================' AS '';
SELECT '已完成以下改造:' AS '';
SELECT '1. product_spu 表新增器材相关字段' AS '';
SELECT '2. trade_order 表新增借用相关字段' AS '';
SELECT '3. 创建 member_credit_score 信用分表' AS '';
SELECT '4. 创建 member_credit_log 信用分记录表' AS '';
SELECT '5. 创建 equipment_reservation 预约表' AS '';
SELECT '6. 插入相关数据字典配置' AS '';
SELECT '7. 创建菜单配置(需根据实际情况调整)' AS '';
SELECT '=======================================' AS '';
SELECT '注意事项:' AS '';
SELECT '1. 请检查菜单的 parent_id 是否正确' AS '';
SELECT '2. 建议先在测试环境执行此脚本' AS '';
SELECT '3. 后续需要配合后端代码和前端页面改造' AS '';
SELECT '=======================================' AS '';


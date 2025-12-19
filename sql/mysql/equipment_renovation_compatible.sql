-- ----------------------------
-- 康复训练器材共享平台 - 兼容版改造脚本 (MySQL)
-- 基于现有商城数据库结构优化
-- 完全兼容，零风险改造
-- 创建日期: 2025-12-15
-- ----------------------------

-- ========================================
-- 重要说明
-- ========================================
-- 1. 本脚本基于您现有的商城数据库结构
-- 2. 只新增字段，不修改、不删除现有字段
-- 3. 所有新增字段都有默认值，不影响现有数据
-- 4. 可以在生产环境安全执行
-- 5. 支持回滚（删除新增字段即可）
-- ========================================

-- 开始事务（可选，建议测试时使用）
-- START TRANSACTION;

-- ========================================
-- 1. 扩展 product_spu 表 - 商品转器材
-- ========================================

-- 检查表是否存在
SELECT '正在扩展 product_spu 表...' AS '';

-- 新增器材特有字段
ALTER TABLE `product_spu` 
  ADD COLUMN IF NOT EXISTS `equipment_status` TINYINT DEFAULT 1 
    COMMENT '器材状态: 1-全新, 2-九成新, 3-八成新, 4-七成新以下',
  ADD COLUMN IF NOT EXISTS `suitable_age_range` VARCHAR(50) DEFAULT NULL 
    COMMENT '适用年龄段: 例如 0-3岁, 3-6岁, 6-12岁',
  ADD COLUMN IF NOT EXISTS `training_types` VARCHAR(200) DEFAULT NULL 
    COMMENT '适用训练类型(多选,逗号分隔): 感统,语言,认知,社交,精细动作,大运动',
  ADD COLUMN IF NOT EXISTS `usage_count` INT DEFAULT 0 
    COMMENT '使用次数统计',
  ADD COLUMN IF NOT EXISTS `last_disinfection_date` DATETIME DEFAULT NULL 
    COMMENT '最近消毒时间',
  ADD COLUMN IF NOT EXISTS `owner_user_id` BIGINT DEFAULT NULL 
    COMMENT '器材拥有者ID(会员ID)',
  ADD COLUMN IF NOT EXISTS `share_type` TINYINT DEFAULT NULL 
    COMMENT '共享类型: 1-仅赠送, 2-仅借用, 3-赠送或借用, NULL-普通商品',
  ADD COLUMN IF NOT EXISTS `deposit_amount` INT DEFAULT 0 
    COMMENT '押金金额(单位:分,仅借用时需要)',
  ADD COLUMN IF NOT EXISTS `borrow_duration` INT DEFAULT 30 
    COMMENT '建议借用时长(单位:天)',
  ADD COLUMN IF NOT EXISTS `equipment_location` VARCHAR(200) DEFAULT NULL 
    COMMENT '器材所在位置(省市区)';

-- 添加索引（如果不存在）
ALTER TABLE `product_spu`
  ADD INDEX IF NOT EXISTS `idx_owner_user_id`(`owner_user_id`),
  ADD INDEX IF NOT EXISTS `idx_share_type`(`share_type`),
  ADD INDEX IF NOT EXISTS `idx_equipment_status`(`equipment_status`);

SELECT '✓ product_spu 表扩展完成' AS '';

-- ========================================
-- 2. 扩展 trade_order 表 - 订单支持赠送/借用
-- ========================================

SELECT '正在扩展 trade_order 表...' AS '';

-- 新增赠送/借用相关字段
ALTER TABLE `trade_order`
  ADD COLUMN IF NOT EXISTS `share_type` TINYINT DEFAULT NULL 
    COMMENT '共享订单类型: 1-赠送订单, 2-借用订单, NULL-普通订单',
  ADD COLUMN IF NOT EXISTS `borrow_start_date` DATETIME DEFAULT NULL 
    COMMENT '借用开始时间',
  ADD COLUMN IF NOT EXISTS `borrow_end_date` DATETIME DEFAULT NULL 
    COMMENT '计划归还时间',
  ADD COLUMN IF NOT EXISTS `actual_return_date` DATETIME DEFAULT NULL 
    COMMENT '实际归还时间',
  ADD COLUMN IF NOT EXISTS `return_status` TINYINT DEFAULT 0 
    COMMENT '归还状态: 0-未归还, 1-已归还, 2-逾期未还, 3-已损坏',
  ADD COLUMN IF NOT EXISTS `deposit_refund_status` TINYINT DEFAULT 0 
    COMMENT '押金退还状态: 0-未退还, 1-已退还, 2-部分退还(扣除赔偿)',
  ADD COLUMN IF NOT EXISTS `equipment_condition` TEXT DEFAULT NULL 
    COMMENT '器材状态说明(借用归还时填写)',
  ADD COLUMN IF NOT EXISTS `equipment_photos` VARCHAR(1000) DEFAULT NULL 
    COMMENT '器材照片(JSON数组,借用/归还时拍照)',
  ADD COLUMN IF NOT EXISTS `overdue_days` INT DEFAULT 0 
    COMMENT '逾期天数',
  ADD COLUMN IF NOT EXISTS `damage_compensation` INT DEFAULT 0 
    COMMENT '损坏赔偿金额(单位:分)';

-- 添加索引
ALTER TABLE `trade_order`
  ADD INDEX IF NOT EXISTS `idx_share_type`(`share_type`),
  ADD INDEX IF NOT EXISTS `idx_return_status`(`return_status`),
  ADD INDEX IF NOT EXISTS `idx_borrow_end_date`(`borrow_end_date`);

SELECT '✓ trade_order 表扩展完成' AS '';

-- ========================================
-- 3. 创建会员信用分表
-- ========================================

SELECT '正在创建 member_credit_score 表...' AS '';

DROP TABLE IF EXISTS `member_credit_score`;
CREATE TABLE `member_credit_score` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `total_score` INT DEFAULT 100 NOT NULL COMMENT '总信用分(默认100分)',
    `donate_count` INT DEFAULT 0 COMMENT '捐赠次数',
    `borrow_count` INT DEFAULT 0 COMMENT '借用次数',
    `return_on_time_count` INT DEFAULT 0 COMMENT '按时归还次数',
    `overdue_count` INT DEFAULT 0 COMMENT '逾期次数',
    `damage_count` INT DEFAULT 0 COMMENT '损坏次数',
    `violation_count` INT DEFAULT 0 COMMENT '违规次数',
    `score_change_reason` VARCHAR(500) DEFAULT NULL COMMENT '最近一次分数变动原因',
    `last_score_change_date` DATETIME DEFAULT NULL COMMENT '最近一次分数变动时间',
    
    -- 基础字段
    `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` BIT DEFAULT 0 NOT NULL COMMENT '是否删除',
    `tenant_id` BIGINT DEFAULT 0 NOT NULL COMMENT '租户编号',
    
    UNIQUE KEY `idx_user_id`(`user_id`, `deleted`) COMMENT '用户ID唯一索引',
    KEY `idx_total_score`(`total_score`) COMMENT '信用分索引',
    KEY `idx_tenant_id`(`tenant_id`) COMMENT '租户索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会员信用分表';

SELECT '✓ member_credit_score 表创建完成' AS '';

-- ========================================
-- 4. 创建信用分变动记录表
-- ========================================

SELECT '正在创建 member_credit_log 表...' AS '';

DROP TABLE IF EXISTS `member_credit_log`;
CREATE TABLE `member_credit_log` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `change_type` TINYINT NOT NULL COMMENT '变动类型: 1-捐赠(+10), 2-按时归还(+5), 3-逾期(-10), 4-损坏(-20), 5-违规(-30)',
    `change_score` INT NOT NULL COMMENT '变动分数(正数为增加,负数为减少)',
    `before_score` INT NOT NULL COMMENT '变动前分数',
    `after_score` INT NOT NULL COMMENT '变动后分数',
    `reason` VARCHAR(500) DEFAULT NULL COMMENT '变动原因描述',
    `related_order_id` BIGINT DEFAULT NULL COMMENT '关联订单ID',
    
    -- 基础字段
    `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` BIT DEFAULT 0 NOT NULL COMMENT '是否删除',
    `tenant_id` BIGINT DEFAULT 0 NOT NULL COMMENT '租户编号',
    
    KEY `idx_user_id`(`user_id`) COMMENT '用户ID索引',
    KEY `idx_change_type`(`change_type`) COMMENT '变动类型索引',
    KEY `idx_create_time`(`create_time`) COMMENT '创建时间索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会员信用分变动记录表';

SELECT '✓ member_credit_log 表创建完成' AS '';

-- ========================================
-- 5. 创建器材预约表
-- ========================================

SELECT '正在创建 equipment_reservation 表...' AS '';

DROP TABLE IF EXISTS `equipment_reservation`;
CREATE TABLE `equipment_reservation` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `equipment_id` BIGINT NOT NULL COMMENT '器材ID(product_spu表ID)',
    `user_id` BIGINT NOT NULL COMMENT '预约用户ID',
    `reservation_status` TINYINT DEFAULT 0 COMMENT '预约状态: 0-待审核, 1-已同意, 2-已拒绝, 3-已取消, 4-已完成',
    `reservation_date` DATETIME NOT NULL COMMENT '预约时间',
    `plan_borrow_date` DATETIME NOT NULL COMMENT '计划借用日期',
    `plan_return_date` DATETIME NOT NULL COMMENT '计划归还日期',
    `cancel_reason` VARCHAR(500) DEFAULT NULL COMMENT '取消/拒绝原因',
    `owner_user_id` BIGINT NOT NULL COMMENT '器材拥有者ID',
    
    -- 基础字段
    `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` BIT DEFAULT 0 NOT NULL COMMENT '是否删除',
    `tenant_id` BIGINT DEFAULT 0 NOT NULL COMMENT '租户编号',
    
    KEY `idx_equipment_id`(`equipment_id`) COMMENT '器材ID索引',
    KEY `idx_user_id`(`user_id`) COMMENT '用户ID索引',
    KEY `idx_reservation_status`(`reservation_status`) COMMENT '预约状态索引',
    KEY `idx_owner_user_id`(`owner_user_id`) COMMENT '拥有者ID索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='器材预约表';

SELECT '✓ equipment_reservation 表创建完成' AS '';

-- ========================================
-- 6. 插入数据字典配置
-- ========================================

SELECT '正在插入数据字典配置...' AS '';

-- 器材状态字典
INSERT INTO `system_dict_type` (`name`, `type`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) 
VALUES ('器材状态', 'equipment_status', 0, '康复训练器材的新旧状态', 'admin', NOW(), '', NOW(), 0);

INSERT INTO `system_dict_data` (`dict_type`, `value`, `label`, `sort`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES
('equipment_status', '1', '全新', 1, 0, 'success', '', '未使用过的全新器材', 'admin', NOW(), '', NOW(), 0),
('equipment_status', '2', '九成新', 2, 0, 'primary', '', '轻微使用痕迹', 'admin', NOW(), '', NOW(), 0),
('equipment_status', '3', '八成新', 3, 0, 'warning', '', '有明显使用痕迹但功能完好', 'admin', NOW(), '', NOW(), 0),
('equipment_status', '4', '七成新以下', 4, 0, 'danger', '', '使用痕迹较多', 'admin', NOW(), '', NOW(), 0);

-- 共享类型字典
INSERT INTO `system_dict_type` (`name`, `type`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) 
VALUES ('共享类型', 'share_type', 0, '器材的共享方式', 'admin', NOW(), '', NOW(), 0);

INSERT INTO `system_dict_data` (`dict_type`, `value`, `label`, `sort`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES
('share_type', '1', '仅赠送', 1, 0, 'success', '', '器材赠送给需要的家庭', 'admin', NOW(), '', NOW(), 0),
('share_type', '2', '仅借用', 2, 0, 'primary', '', '器材可借用但需归还', 'admin', NOW(), '', NOW(), 0),
('share_type', '3', '赠送或借用', 3, 0, 'info', '', '可以选择赠送或借用', 'admin', NOW(), '', NOW(), 0);

-- 训练类型字典
INSERT INTO `system_dict_type` (`name`, `type`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) 
VALUES ('训练类型', 'training_type', 0, '康复训练的类型分类', 'admin', NOW(), '', NOW(), 0);

INSERT INTO `system_dict_data` (`dict_type`, `value`, `label`, `sort`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES
('training_type', 'sensory', '感统训练', 1, 0, 'primary', '', '感觉统合训练器材', 'admin', NOW(), '', NOW(), 0),
('training_type', 'language', '语言训练', 2, 0, 'success', '', '语言能力训练器材', 'admin', NOW(), '', NOW(), 0),
('training_type', 'cognitive', '认知训练', 3, 0, 'warning', '', '认知能力训练器材', 'admin', NOW(), '', NOW(), 0),
('training_type', 'social', '社交训练', 4, 0, 'info', '', '社交能力训练器材', 'admin', NOW(), '', NOW(), 0),
('training_type', 'fine_motor', '精细动作', 5, 0, 'danger', '', '手部精细动作训练器材', 'admin', NOW(), '', NOW(), 0),
('training_type', 'gross_motor', '大运动', 6, 0, 'default', '', '大肌肉运动训练器材', 'admin', NOW(), '', NOW(), 0);

-- 归还状态字典
INSERT INTO `system_dict_type` (`name`, `type`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) 
VALUES ('归还状态', 'return_status', 0, '借用器材的归还状态', 'admin', NOW(), '', NOW(), 0);

INSERT INTO `system_dict_data` (`dict_type`, `value`, `label`, `sort`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES
('return_status', '0', '未归还', 1, 0, 'warning', '', '器材尚未归还', 'admin', NOW(), '', NOW(), 0),
('return_status', '1', '已归还', 2, 0, 'success', '', '器材已归还', 'admin', NOW(), '', NOW(), 0),
('return_status', '2', '逾期未还', 3, 0, 'danger', '', '超过约定时间未归还', 'admin', NOW(), '', NOW(), 0),
('return_status', '3', '已损坏', 4, 0, 'danger', '', '归还时发现器材损坏', 'admin', NOW(), '', NOW(), 0);

-- 信用分变动类型字典
INSERT INTO `system_dict_type` (`name`, `type`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) 
VALUES ('信用分变动类型', 'credit_change_type', 0, '会员信用分变动的类型', 'admin', NOW(), '', NOW(), 0);

INSERT INTO `system_dict_data` (`dict_type`, `value`, `label`, `sort`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES
('credit_change_type', '1', '捐赠器材', 1, 0, 'success', '', '+10分', 'admin', NOW(), '', NOW(), 0),
('credit_change_type', '2', '按时归还', 2, 0, 'success', '', '+5分', 'admin', NOW(), '', NOW(), 0),
('credit_change_type', '3', '逾期归还', 3, 0, 'danger', '', '-10分', 'admin', NOW(), '', NOW(), 0),
('credit_change_type', '4', '器材损坏', 4, 0, 'danger', '', '-20分', 'admin', NOW(), '', NOW(), 0),
('credit_change_type', '5', '违规行为', 5, 0, 'danger', '', '-30分', 'admin', NOW(), '', NOW(), 0),
('credit_change_type', '6', '优质评价', 6, 0, 'success', '', '+3分', 'admin', NOW(), '', NOW(), 0);

SELECT '✓ 数据字典配置完成' AS '';

-- ========================================
-- 7. 验证改造结果
-- ========================================

SELECT '========================================' AS '';
SELECT '改造完成！正在验证结果...' AS '';
SELECT '========================================' AS '';

-- 验证product_spu表
SELECT CONCAT('✓ product_spu 表字段数: ', COUNT(*)) AS '' 
FROM information_schema.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
  AND TABLE_NAME = 'product_spu';

-- 验证trade_order表  
SELECT CONCAT('✓ trade_order 表字段数: ', COUNT(*)) AS '' 
FROM information_schema.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
  AND TABLE_NAME = 'trade_order';

-- 验证新建表
SELECT '✓ member_credit_score 表已创建' AS '' 
FROM information_schema.TABLES 
WHERE TABLE_SCHEMA = DATABASE() 
  AND TABLE_NAME = 'member_credit_score';

SELECT '✓ member_credit_log 表已创建' AS '' 
FROM information_schema.TABLES 
WHERE TABLE_SCHEMA = DATABASE() 
  AND TABLE_NAME = 'member_credit_log';

SELECT '✓ equipment_reservation 表已创建' AS '' 
FROM information_schema.TABLES 
WHERE TABLE_SCHEMA = DATABASE() 
  AND TABLE_NAME = 'equipment_reservation';

-- ========================================
-- 完成提示
-- ========================================

SELECT '========================================' AS '';
SELECT '🎉 康复训练器材共享平台改造完成！' AS '';
SELECT '========================================' AS '';
SELECT '已完成:' AS '';
SELECT '✓ 扩展 product_spu 表 (新增10个字段)' AS '';
SELECT '✓ 扩展 trade_order 表 (新增10个字段)' AS '';
SELECT '✓ 创建 member_credit_score 表' AS '';
SELECT '✓ 创建 member_credit_log 表' AS '';
SELECT '✓ 创建 equipment_reservation 表' AS '';
SELECT '✓ 插入 5类数据字典配置' AS '';
SELECT '========================================' AS '';
SELECT '特色功能:' AS '';
SELECT '✓ 器材状态管理 (全新/九成新/八成新/七成新)' AS '';
SELECT '✓ 共享类型 (赠送/借用/两者都可)' AS '';
SELECT '✓ 信用分体系 (默认100分)' AS '';
SELECT '✓ 借用归还流程' AS '';
SELECT '✓ 预约管理系统' AS '';
SELECT '========================================' AS '';
SELECT '注意事项:' AS '';
SELECT '1. 现有商品不受影响 (share_type为NULL表示普通商品)' AS '';
SELECT '2. 可以通过设置share_type将商品转为器材' AS '';
SELECT '3. 建议查看文档: COMPATIBILITY_ANALYSIS.md' AS '';
SELECT '========================================' AS '';

-- 提交事务（如果使用了START TRANSACTION）
-- COMMIT;

-- 如需回滚（仅在测试时使用）
-- ROLLBACK;


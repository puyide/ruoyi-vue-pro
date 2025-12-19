-- ----------------------------
-- 康复训练器材共享平台 - 交接会话模块 (MySQL)
-- 有温度的设计：隐私保护 + 流畅沟通 + 社区互动
-- 创建日期: 2025-12-15
-- ----------------------------

-- ========================================
-- 1. 交接会话表 (核心表)
-- ========================================

DROP TABLE IF EXISTS equipment_handover;
CREATE TABLE equipment_handover (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    order_id BIGINT NOT NULL COMMENT '关联订单ID',
    equipment_id BIGINT NOT NULL COMMENT '器材ID',
    donor_user_id BIGINT NOT NULL COMMENT '赠送/出借人ID',
    receiver_user_id BIGINT NOT NULL COMMENT '接收人ID',
    
    -- 会话状态
    handover_status TINYINT DEFAULT 0 NOT NULL COMMENT '交接状态: 0-沟通中, 1-约见/寄出, 2-完成',
    
    -- 交接模式
    handover_mode TINYINT DEFAULT NULL COMMENT '交接模式: 1-快递模式, 2-当面模式',
    
    -- 快递模式相关
    express_company VARCHAR(50) DEFAULT NULL COMMENT '快递公司',
    express_no VARCHAR(100) DEFAULT NULL COMMENT '快递单号',
    express_status TINYINT DEFAULT NULL COMMENT '物流状态: 0-待发货, 1-已发货, 2-运输中, 3-派送中, 4-已签收',
    
    -- 当面模式相关
    meet_date DATETIME DEFAULT NULL COMMENT '约定见面时间',
    meet_location VARCHAR(200) DEFAULT NULL COMMENT '见面地点',
    meet_location_type VARCHAR(50) DEFAULT NULL COMMENT '地点类型: metro-地铁站, mall-商场, park-公园',
    
    -- 联系方式交换
    contact_exchange_status TINYINT DEFAULT 0 COMMENT '联系方式交换状态: 0-未交换, 1-待对方同意, 2-已交换',
    donor_agreed_contact BIT DEFAULT 0 COMMENT '赠送人是否同意交换联系方式',
    receiver_agreed_contact BIT DEFAULT 0 COMMENT '接收人是否同意交换联系方式',
    contact_exchange_time DATETIME DEFAULT NULL COMMENT '联系方式交换时间',
    
    -- 一次性中转号（隐私保护）
    temp_phone_donor VARCHAR(20) DEFAULT NULL COMMENT '赠送人临时中转号',
    temp_phone_receiver VARCHAR(20) DEFAULT NULL COMMENT '接收人临时中转号',
    temp_phone_expire_time DATETIME DEFAULT NULL COMMENT '中转号过期时间',
    
    -- 完成确认
    completed_time DATETIME DEFAULT NULL COMMENT '完成时间',
    donor_confirmed BIT DEFAULT 0 COMMENT '赠送人确认完成',
    receiver_confirmed BIT DEFAULT 0 COMMENT '接收人确认完成',
    
    -- 基础字段
    creator VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updater VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted BIT DEFAULT 0 NOT NULL COMMENT '是否删除',
    tenant_id BIGINT DEFAULT 0 NOT NULL COMMENT '租户编号',
    
    KEY idx_order_id(order_id) COMMENT '订单ID索引',
    KEY idx_equipment_id(equipment_id) COMMENT '器材ID索引',
    KEY idx_donor_user_id(donor_user_id) COMMENT '赠送人ID索引',
    KEY idx_receiver_user_id(receiver_user_id) COMMENT '接收人ID索引',
    KEY idx_handover_status(handover_status) COMMENT '交接状态索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='器材交接会话表';

-- ========================================
-- 2. 交接消息表（简化留言）
-- ========================================

DROP TABLE IF EXISTS equipment_handover_message;
CREATE TABLE equipment_handover_message (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    handover_id BIGINT NOT NULL COMMENT '交接会话ID',
    sender_user_id BIGINT NOT NULL COMMENT '发送人ID',
    receiver_user_id BIGINT NOT NULL COMMENT '接收人ID',
    
    -- 消息内容
    message_content TEXT NOT NULL COMMENT '消息内容',
    message_type TINYINT DEFAULT 1 COMMENT '消息类型: 1-文本, 2-系统提示',
    
    -- 敏感信息拦截
    has_sensitive_info BIT DEFAULT 0 COMMENT '是否包含敏感信息',
    sensitive_keywords VARCHAR(500) DEFAULT NULL COMMENT '命中的敏感关键词',
    
    -- 阅读状态
    is_read BIT DEFAULT 0 COMMENT '是否已读',
    read_time DATETIME DEFAULT NULL COMMENT '阅读时间',
    
    -- 基础字段
    creator VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updater VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted BIT DEFAULT 0 NOT NULL COMMENT '是否删除',
    tenant_id BIGINT DEFAULT 0 NOT NULL COMMENT '租户编号',
    
    KEY idx_handover_id(handover_id) COMMENT '交接会话ID索引',
    KEY idx_sender_user_id(sender_user_id) COMMENT '发送人ID索引',
    KEY idx_create_time(create_time) COMMENT '创建时间索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='交接消息表';

-- ========================================
-- 3. 感谢评价表
-- ========================================

DROP TABLE IF EXISTS equipment_feedback;
CREATE TABLE equipment_feedback (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    handover_id BIGINT NOT NULL COMMENT '交接会话ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    equipment_id BIGINT NOT NULL COMMENT '器材ID',
    
    -- 评价人和被评价人
    evaluator_user_id BIGINT NOT NULL COMMENT '评价人ID',
    evaluated_user_id BIGINT NOT NULL COMMENT '被评价人ID',
    
    -- 感谢内容
    feedback_content TEXT DEFAULT NULL COMMENT '感谢内容(<=500字)',
    feedback_rating TINYINT DEFAULT NULL COMMENT '评价星级(1-5)',
    
    -- 隐私设置
    is_public BIT DEFAULT 1 COMMENT '是否公开',
    
    -- NodeBB 同步
    sync_to_nodebb BIT DEFAULT 0 COMMENT '是否同步到NodeBB论坛',
    nodebb_topic_id BIGINT DEFAULT NULL COMMENT 'NodeBB主题ID',
    nodebb_post_id BIGINT DEFAULT NULL COMMENT 'NodeBB帖子ID',
    nodebb_sync_time DATETIME DEFAULT NULL COMMENT 'NodeBB同步时间',
    nodebb_sync_status TINYINT DEFAULT NULL COMMENT '同步状态: 0-未同步, 1-同步中, 2-已同步, 3-同步失败',
    
    -- 器材卡片回链
    equipment_card_link VARCHAR(500) DEFAULT NULL COMMENT '器材卡片链接',
    
    -- 基础字段
    creator VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updater VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted BIT DEFAULT 0 NOT NULL COMMENT '是否删除',
    tenant_id BIGINT DEFAULT 0 NOT NULL COMMENT '租户编号',
    
    KEY idx_handover_id_fb(handover_id) COMMENT '交接会话ID索引',
    KEY idx_equipment_id_fb(equipment_id) COMMENT '器材ID索引',
    KEY idx_evaluator_user_id(evaluator_user_id) COMMENT '评价人ID索引',
    KEY idx_is_public(is_public) COMMENT '是否公开索引',
    KEY idx_nodebb_topic_id(nodebb_topic_id) COMMENT 'NodeBB主题ID索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='感谢评价表';

-- ========================================
-- 4. 敏感词库表
-- ========================================

DROP TABLE IF EXISTS sensitive_keyword;
CREATE TABLE sensitive_keyword (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    keyword VARCHAR(100) NOT NULL COMMENT '敏感词',
    keyword_type TINYINT NOT NULL COMMENT '类型: 1-联系方式, 2-地址信息, 3-违禁词',
    severity_level TINYINT DEFAULT 1 COMMENT '严重等级: 1-提示, 2-警告, 3-拦截',
    action_type TINYINT DEFAULT 1 COMMENT '处理方式: 1-高亮提示, 2-替换为***, 3-禁止发送',
    
    -- 基础字段
    creator VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updater VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted BIT DEFAULT 0 NOT NULL COMMENT '是否删除',
    tenant_id BIGINT DEFAULT 0 NOT NULL COMMENT '租户编号',
    
    UNIQUE KEY idx_keyword(keyword, deleted) COMMENT '关键词唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='敏感词库表';

-- 插入默认敏感词
INSERT INTO sensitive_keyword (keyword, keyword_type, severity_level, action_type, creator) VALUES
-- 联系方式类
('电话', 1, 1, 1, 'system'),
('手机', 1, 1, 1, 'system'),
('微信', 1, 1, 1, 'system'),
('QQ', 1, 1, 1, 'system'),
('邮箱', 1, 1, 1, 'system'),
-- 地址信息类
('家庭地址', 2, 2, 1, 'system'),
('住址', 2, 2, 1, 'system'),
('小区', 2, 2, 1, 'system'),
('门牌号', 2, 2, 1, 'system');

-- ========================================
-- 5. 公共地点库表（当面交接地点）
-- ========================================

DROP TABLE IF EXISTS public_location;
CREATE TABLE public_location (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    location_name VARCHAR(200) NOT NULL COMMENT '地点名称',
    location_type VARCHAR(50) NOT NULL COMMENT '地点类型: metro-地铁站, mall-商场, park-公园, library-图书馆',
    location_address VARCHAR(500) DEFAULT NULL COMMENT '详细地址',
    
    -- 地理位置
    province VARCHAR(50) DEFAULT NULL COMMENT '省份',
    city VARCHAR(50) DEFAULT NULL COMMENT '城市',
    district VARCHAR(50) DEFAULT NULL COMMENT '区县',
    latitude DECIMAL(10, 6) DEFAULT NULL COMMENT '纬度',
    longitude DECIMAL(10, 6) DEFAULT NULL COMMENT '经度',
    
    -- 状态
    is_active BIT DEFAULT 1 COMMENT '是否启用',
    usage_count INT DEFAULT 0 COMMENT '使用次数',
    
    -- 基础字段
    creator VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updater VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted BIT DEFAULT 0 NOT NULL COMMENT '是否删除',
    tenant_id BIGINT DEFAULT 0 NOT NULL COMMENT '租户编号',
    
    KEY idx_location_type(location_type) COMMENT '地点类型索引',
    KEY idx_city(city) COMMENT '城市索引',
    KEY idx_is_active(is_active) COMMENT '是否启用索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='公共地点库表';

-- 插入示例公共地点
INSERT INTO public_location (location_name, location_type, location_address, city, district, creator) VALUES
('人民广场地铁站', 'metro', '上海市黄浦区人民大道', '上海市', '黄浦区', 'system'),
('徐家汇商圈', 'mall', '上海市徐汇区徐家汇', '上海市', '徐汇区', 'system'),
('世纪公园', 'park', '上海市浦东新区锦绣路', '上海市', '浦东新区', 'system'),
('上海图书馆', 'library', '上海市徐汇区淮海中路', '上海市', '徐汇区', 'system');

-- ========================================
-- 6. 插入数据字典配置
-- ========================================

-- 交接状态字典
INSERT INTO system_dict_type (name, type, status, remark, creator, create_time, updater, update_time, deleted) 
VALUES ('交接状态', 'handover_status', 0, '器材交接的状态', 'admin', NOW(), '', NOW(), 0);

INSERT INTO system_dict_data (dict_type, value, label, sort, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted) VALUES
('handover_status', '0', '沟通中', 1, 0, 'info', '', '双方正在沟通中', 'admin', NOW(), '', NOW(), 0),
('handover_status', '1', '约见/寄出', 2, 0, 'warning', '', '已约定见面或已寄出', 'admin', NOW(), '', NOW(), 0),
('handover_status', '2', '完成', 3, 0, 'success', '', '交接已完成', 'admin', NOW(), '', NOW(), 0);

-- 交接模式字典
INSERT INTO system_dict_type (name, type, status, remark, creator, create_time, updater, update_time, deleted) 
VALUES ('交接模式', 'handover_mode', 0, '器材交接方式', 'admin', NOW(), '', NOW(), 0);

INSERT INTO system_dict_data (dict_type, value, label, sort, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted) VALUES
('handover_mode', '1', '快递模式', 1, 0, 'primary', '', '通过快递寄送', 'admin', NOW(), '', NOW(), 0),
('handover_mode', '2', '当面模式', 2, 0, 'success', '', '面对面交接', 'admin', NOW(), '', NOW(), 0);

-- 物流状态字典
INSERT INTO system_dict_type (name, type, status, remark, creator, create_time, updater, update_time, deleted) 
VALUES ('物流状态', 'express_status', 0, '快递物流状态', 'admin', NOW(), '', NOW(), 0);

INSERT INTO system_dict_data (dict_type, value, label, sort, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted) VALUES
('express_status', '0', '待发货', 1, 0, 'info', '', '等待发货', 'admin', NOW(), '', NOW(), 0),
('express_status', '1', '已发货', 2, 0, 'primary', '', '已发货', 'admin', NOW(), '', NOW(), 0),
('express_status', '2', '运输中', 3, 0, 'warning', '', '运输中', 'admin', NOW(), '', NOW(), 0),
('express_status', '3', '派送中', 4, 0, 'warning', '', '正在派送', 'admin', NOW(), '', NOW(), 0),
('express_status', '4', '已签收', 5, 0, 'success', '', '已签收', 'admin', NOW(), '', NOW(), 0);

-- 地点类型字典
INSERT INTO system_dict_type (name, type, status, remark, creator, create_time, updater, update_time, deleted) 
VALUES ('地点类型', 'location_type', 0, '公共地点类型', 'admin', NOW(), '', NOW(), 0);

INSERT INTO system_dict_data (dict_type, value, label, sort, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted) VALUES
('location_type', 'metro', '地铁站', 1, 0, 'primary', '', '地铁站', 'admin', NOW(), '', NOW(), 0),
('location_type', 'mall', '商场', 2, 0, 'success', '', '商场', 'admin', NOW(), '', NOW(), 0),
('location_type', 'park', '公园', 3, 0, 'info', '', '公园', 'admin', NOW(), '', NOW(), 0),
('location_type', 'library', '图书馆', 4, 0, 'warning', '', '图书馆', 'admin', NOW(), '', NOW(), 0);

-- ========================================
-- 完成提示
-- ========================================

SELECT '========================================' AS '';
SELECT '有温度的交接会话模块创建完成！' AS '';
SELECT '========================================' AS '';
SELECT '已完成:' AS '';
SELECT '1. equipment_handover 交接会话表' AS '';
SELECT '2. equipment_handover_message 消息表' AS '';
SELECT '3. equipment_feedback 感谢评价表(支持NodeBB同步)' AS '';
SELECT '4. sensitive_keyword 敏感词库表' AS '';
SELECT '5. public_location 公共地点库表' AS '';
SELECT '6. 相关数据字典配置' AS '';
SELECT '========================================' AS '';
SELECT '特色功能:' AS '';
SELECT '✓ 受控的联系方式交换' AS '';
SELECT '✓ 一次性中转号保护隐私' AS '';
SELECT '✓ 敏感信息自动拦截' AS '';
SELECT '✓ 快递自动追踪' AS '';
SELECT '✓ 公共地点安全交接' AS '';
SELECT '✓ 感谢贴自动同步NodeBB' AS '';
SELECT '========================================' AS '';


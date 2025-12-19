-- ----------------------------
-- 康复训练器材共享平台 - 交接会话模块（独立版）
-- 不依赖system_dict_type表，可独立执行
-- 有温度的设计：隐私保护 + 流畅沟通 + 社区互动
-- 创建日期: 2025-12-15
-- ----------------------------

SET client_encoding = 'UTF8';

-- ========================================
-- 1. 交接会话表 (核心表)
-- ========================================

DO $$
BEGIN
    RAISE NOTICE '正在创建 equipment_handover 表...';
END $$;

DROP TABLE IF EXISTS equipment_handover;
CREATE TABLE equipment_handover (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    equipment_id BIGINT NOT NULL,
    donor_user_id BIGINT NOT NULL,
    receiver_user_id BIGINT NOT NULL,
    
    -- 会话状态
    handover_status INT2 DEFAULT 0 NOT NULL,
    
    -- 交接模式
    handover_mode INT2,
    
    -- 快递模式相关
    express_company VARCHAR(50),
    express_no VARCHAR(100),
    express_status INT2,
    
    -- 当面模式相关
    meet_date TIMESTAMP,
    meet_location VARCHAR(200),
    meet_location_type VARCHAR(50),
    
    -- 联系方式交换
    contact_exchange_status INT2 DEFAULT 0,
    donor_agreed_contact BOOLEAN DEFAULT FALSE,
    receiver_agreed_contact BOOLEAN DEFAULT FALSE,
    contact_exchange_time TIMESTAMP,
    
    -- 一次性中转号（隐私保护）
    temp_phone_donor VARCHAR(20),
    temp_phone_receiver VARCHAR(20),
    temp_phone_expire_time TIMESTAMP,
    
    -- 完成确认
    completed_time TIMESTAMP,
    donor_confirmed BOOLEAN DEFAULT FALSE,
    receiver_confirmed BOOLEAN DEFAULT FALSE,
    
    -- 基础字段
    creator VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT DEFAULT 0 NOT NULL,
    tenant_id BIGINT DEFAULT 0 NOT NULL
);

COMMENT ON TABLE equipment_handover IS '器材交接会话表';
COMMENT ON COLUMN equipment_handover.handover_status IS '交接状态: 0-沟通中, 1-约见/寄出, 2-完成';
COMMENT ON COLUMN equipment_handover.handover_mode IS '交接模式: 1-快递模式, 2-当面模式';

-- 创建索引
CREATE INDEX idx_handover_order_id ON equipment_handover(order_id);
CREATE INDEX idx_handover_equipment_id ON equipment_handover(equipment_id);
CREATE INDEX idx_handover_donor_user_id ON equipment_handover(donor_user_id);
CREATE INDEX idx_handover_receiver_user_id ON equipment_handover(receiver_user_id);
CREATE INDEX idx_handover_status ON equipment_handover(handover_status);

DO $$
BEGIN
    RAISE NOTICE '✓ equipment_handover 表创建完成';
END $$;

-- ========================================
-- 2. 交接消息表（简化留言）
-- ========================================

DO $$
BEGIN
    RAISE NOTICE '正在创建 equipment_handover_message 表...';
END $$;

DROP TABLE IF EXISTS equipment_handover_message;
CREATE TABLE equipment_handover_message (
    id BIGSERIAL PRIMARY KEY,
    handover_id BIGINT NOT NULL,
    sender_user_id BIGINT NOT NULL,
    receiver_user_id BIGINT NOT NULL,
    
    -- 消息内容
    message_content TEXT NOT NULL,
    message_type INT2 DEFAULT 1,
    
    -- 敏感信息拦截
    has_sensitive_info BOOLEAN DEFAULT FALSE,
    sensitive_keywords VARCHAR(500),
    
    -- 阅读状态
    is_read BOOLEAN DEFAULT FALSE,
    read_time TIMESTAMP,
    
    -- 基础字段
    creator VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT DEFAULT 0 NOT NULL,
    tenant_id BIGINT DEFAULT 0 NOT NULL
);

COMMENT ON TABLE equipment_handover_message IS '交接消息表';

CREATE INDEX idx_message_handover_id ON equipment_handover_message(handover_id);
CREATE INDEX idx_message_sender_user_id ON equipment_handover_message(sender_user_id);
CREATE INDEX idx_message_create_time ON equipment_handover_message(create_time);

DO $$
BEGIN
    RAISE NOTICE '✓ equipment_handover_message 表创建完成';
END $$;

-- ========================================
-- 3. 感谢评价表
-- ========================================

DO $$
BEGIN
    RAISE NOTICE '正在创建 equipment_feedback 表...';
END $$;

DROP TABLE IF EXISTS equipment_feedback;
CREATE TABLE equipment_feedback (
    id BIGSERIAL PRIMARY KEY,
    handover_id BIGINT NOT NULL,
    order_id BIGINT NOT NULL,
    equipment_id BIGINT NOT NULL,
    
    -- 评价人和被评价人
    evaluator_user_id BIGINT NOT NULL,
    evaluated_user_id BIGINT NOT NULL,
    
    -- 感谢内容
    feedback_content TEXT,
    feedback_rating INT2,
    
    -- 隐私设置
    is_public BOOLEAN DEFAULT TRUE,
    
    -- NodeBB 同步
    sync_to_nodebb BOOLEAN DEFAULT FALSE,
    nodebb_topic_id INT8,
    nodebb_post_id INT8,
    nodebb_sync_time TIMESTAMP,
    nodebb_sync_status INT2,
    
    -- 器材卡片回链
    equipment_card_link VARCHAR(500),
    
    -- 基础字段
    creator VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT DEFAULT 0 NOT NULL,
    tenant_id BIGINT DEFAULT 0 NOT NULL
);

COMMENT ON TABLE equipment_feedback IS '感谢评价表';

CREATE INDEX idx_feedback_handover_id ON equipment_feedback(handover_id);
CREATE INDEX idx_feedback_equipment_id ON equipment_feedback(equipment_id);
CREATE INDEX idx_feedback_evaluator_user_id ON equipment_feedback(evaluator_user_id);
CREATE INDEX idx_feedback_is_public ON equipment_feedback(is_public);
CREATE INDEX idx_feedback_nodebb_topic_id ON equipment_feedback(nodebb_topic_id);

DO $$
BEGIN
    RAISE NOTICE '✓ equipment_feedback 表创建完成';
END $$;

-- ========================================
-- 4. 敏感词库表
-- ========================================

DO $$
BEGIN
    RAISE NOTICE '正在创建 sensitive_keyword 表...';
END $$;

DROP TABLE IF EXISTS sensitive_keyword;
CREATE TABLE sensitive_keyword (
    id BIGSERIAL PRIMARY KEY,
    keyword VARCHAR(100) NOT NULL,
    keyword_type INT2 NOT NULL,
    severity_level INT2 DEFAULT 1,
    action_type INT2 DEFAULT 1,
    
    -- 基础字段
    creator VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT DEFAULT 0 NOT NULL,
    tenant_id BIGINT DEFAULT 0 NOT NULL
);

COMMENT ON TABLE sensitive_keyword IS '敏感词库表';
COMMENT ON COLUMN sensitive_keyword.keyword_type IS '类型: 1-联系方式, 2-地址信息, 3-违禁词';
COMMENT ON COLUMN sensitive_keyword.severity_level IS '严重等级: 1-提示, 2-警告, 3-拦截';

-- 创建唯一索引
CREATE UNIQUE INDEX idx_sensitive_keyword ON sensitive_keyword(keyword, deleted);

-- 插入默认敏感词
INSERT INTO sensitive_keyword (keyword, keyword_type, severity_level, action_type, creator) VALUES
('电话', 1, 1, 1, 'system'),
('手机', 1, 1, 1, 'system'),
('微信', 1, 1, 1, 'system'),
('QQ', 1, 1, 1, 'system'),
('邮箱', 1, 1, 1, 'system'),
('家庭地址', 2, 2, 1, 'system'),
('住址', 2, 2, 1, 'system'),
('小区', 2, 2, 1, 'system'),
('门牌号', 2, 2, 1, 'system');

DO $$
BEGIN
    RAISE NOTICE '✓ sensitive_keyword 表创建完成，插入9个默认敏感词';
END $$;

-- ========================================
-- 5. 公共地点库表（当面交接地点）
-- ========================================

DO $$
BEGIN
    RAISE NOTICE '正在创建 public_location 表...';
END $$;

DROP TABLE IF EXISTS public_location;
CREATE TABLE public_location (
    id BIGSERIAL PRIMARY KEY,
    location_name VARCHAR(200) NOT NULL,
    location_type VARCHAR(50) NOT NULL,
    location_address VARCHAR(500),
    
    -- 地理位置
    province VARCHAR(50),
    city VARCHAR(50),
    district VARCHAR(50),
    latitude DECIMAL(10, 6),
    longitude DECIMAL(10, 6),
    
    -- 状态
    is_active BOOLEAN DEFAULT TRUE,
    usage_count INT4 DEFAULT 0,
    
    -- 基础字段
    creator VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT DEFAULT 0 NOT NULL,
    tenant_id BIGINT DEFAULT 0 NOT NULL
);

COMMENT ON TABLE public_location IS '公共地点库表';
COMMENT ON COLUMN public_location.location_type IS '地点类型: metro-地铁站, mall-商场, park-公园, library-图书馆';

-- 创建索引
CREATE INDEX idx_location_type ON public_location(location_type);
CREATE INDEX idx_location_city ON public_location(city);
CREATE INDEX idx_location_is_active ON public_location(is_active);

-- 插入示例公共地点
INSERT INTO public_location (location_name, location_type, location_address, city, district, creator) VALUES
('人民广场地铁站', 'metro', '上海市黄浦区人民大道', '上海市', '黄浦区', 'system'),
('徐家汇商圈', 'mall', '上海市徐汇区徐家汇', '上海市', '徐汇区', 'system'),
('世纪公园', 'park', '上海市浦东新区锦绣路', '上海市', '浦东新区', 'system'),
('上海图书馆', 'library', '上海市徐汇区淮海中路', '上海市', '徐汇区', 'system');

DO $$
BEGIN
    RAISE NOTICE '✓ public_location 表创建完成，插入4个示例地点';
END $$;

-- ========================================
-- 完成提示
-- ========================================

DO $$
BEGIN
    RAISE NOTICE '========================================';
    RAISE NOTICE '有温度的交接会话模块创建完成！';
    RAISE NOTICE '========================================';
    RAISE NOTICE '已完成:';
    RAISE NOTICE '1. equipment_handover 交接会话表';
    RAISE NOTICE '2. equipment_handover_message 消息表';
    RAISE NOTICE '3. equipment_feedback 感谢评价表(支持NodeBB同步)';
    RAISE NOTICE '4. sensitive_keyword 敏感词库表(含9个默认词)';
    RAISE NOTICE '5. public_location 公共地点库表(含4个示例地点)';
    RAISE NOTICE '========================================';
    RAISE NOTICE '特色功能:';
    RAISE NOTICE '✓ 受控的联系方式交换';
    RAISE NOTICE '✓ 一次性中转号保护隐私';
    RAISE NOTICE '✓ 敏感信息自动拦截';
    RAISE NOTICE '✓ 快递自动追踪';
    RAISE NOTICE '✓ 公共地点安全交接';
    RAISE NOTICE '✓ 感谢贴自动同步NodeBB';
    RAISE NOTICE '========================================';
    RAISE NOTICE '注意：数据字典需要在管理后台手动配置';
    RAISE NOTICE '或者先执行 ruoyi-vue-pro.sql 后';
    RAISE NOTICE '再执行 equipment_handover.sql (完整版)';
    RAISE NOTICE '========================================';
END $$;


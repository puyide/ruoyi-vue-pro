-- ----------------------------
-- 康复训练器材共享平台 - 改造脚本（独立版）
-- 不依赖system_dict_type表，可独立执行
-- 创建日期: 2025-12-15
-- ----------------------------

-- ========================================
-- 说明：
-- 1. 此版本不插入数据字典，仅创建表结构和扩展字段
-- 2. 数据字典可以后续在管理后台手动配置
-- 3. 如果您已经有system_dict_type表，请使用 equipment_renovation.sql
-- ========================================

SET client_encoding = 'UTF8';

-- ========================================
-- 1. 扩展 product_spu 表 - 商品转器材
-- ========================================

DO $$
BEGIN
    RAISE NOTICE '正在扩展 product_spu 表...';
END $$;

-- 新增器材特有字段
ALTER TABLE product_spu 
  ADD COLUMN IF NOT EXISTS equipment_status INT2 DEFAULT 1,
  ADD COLUMN IF NOT EXISTS suitable_age_range VARCHAR(50) DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS training_types VARCHAR(200) DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS usage_count INT4 DEFAULT 0,
  ADD COLUMN IF NOT EXISTS last_disinfection_date TIMESTAMP DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS owner_user_id INT8 DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS share_type INT2 DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS deposit_amount INT4 DEFAULT 0,
  ADD COLUMN IF NOT EXISTS borrow_duration INT4 DEFAULT 30,
  ADD COLUMN IF NOT EXISTS equipment_location VARCHAR(200) DEFAULT NULL;

COMMENT ON COLUMN product_spu.equipment_status IS '器材状态: 1-全新, 2-九成新, 3-八成新, 4-七成新以下';
COMMENT ON COLUMN product_spu.suitable_age_range IS '适用年龄段';
COMMENT ON COLUMN product_spu.training_types IS '训练类型(多选,逗号分隔)';
COMMENT ON COLUMN product_spu.usage_count IS '使用次数';
COMMENT ON COLUMN product_spu.last_disinfection_date IS '最近消毒时间';
COMMENT ON COLUMN product_spu.owner_user_id IS '器材拥有者ID';
COMMENT ON COLUMN product_spu.share_type IS '共享类型: 1-仅赠送, 2-仅借用, 3-赠送或借用';
COMMENT ON COLUMN product_spu.deposit_amount IS '押金金额(分)';
COMMENT ON COLUMN product_spu.borrow_duration IS '建议借用天数';
COMMENT ON COLUMN product_spu.equipment_location IS '器材所在位置';

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_owner_user_id ON product_spu(owner_user_id);
CREATE INDEX IF NOT EXISTS idx_share_type ON product_spu(share_type);
CREATE INDEX IF NOT EXISTS idx_equipment_status ON product_spu(equipment_status);

DO $$
BEGIN
    RAISE NOTICE '✓ product_spu 表扩展完成';
END $$;

-- ========================================
-- 2. 扩展 trade_order 表
-- ========================================

DO $$
BEGIN
    RAISE NOTICE '正在扩展 trade_order 表...';
END $$;

ALTER TABLE trade_order
  ADD COLUMN IF NOT EXISTS share_type INT2 DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS borrow_start_date TIMESTAMP DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS borrow_end_date TIMESTAMP DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS actual_return_date TIMESTAMP DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS return_status INT2 DEFAULT 0,
  ADD COLUMN IF NOT EXISTS deposit_refund_status INT2 DEFAULT 0,
  ADD COLUMN IF NOT EXISTS equipment_condition TEXT DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS equipment_photos VARCHAR(1000) DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS overdue_days INT4 DEFAULT 0,
  ADD COLUMN IF NOT EXISTS damage_compensation INT4 DEFAULT 0;

COMMENT ON COLUMN trade_order.share_type IS '订单类型: 1-赠送, 2-借用';
COMMENT ON COLUMN trade_order.borrow_start_date IS '借用开始时间';
COMMENT ON COLUMN trade_order.borrow_end_date IS '计划归还时间';
COMMENT ON COLUMN trade_order.actual_return_date IS '实际归还时间';
COMMENT ON COLUMN trade_order.return_status IS '归还状态: 0-未归还, 1-已归还, 2-逾期, 3-已损坏';
COMMENT ON COLUMN trade_order.deposit_refund_status IS '押金退还状态';
COMMENT ON COLUMN trade_order.equipment_condition IS '器材状态说明';
COMMENT ON COLUMN trade_order.equipment_photos IS '器材照片';
COMMENT ON COLUMN trade_order.overdue_days IS '逾期天数';
COMMENT ON COLUMN trade_order.damage_compensation IS '损坏赔偿(分)';

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_trade_share_type ON trade_order(share_type);
CREATE INDEX IF NOT EXISTS idx_trade_return_status ON trade_order(return_status);
CREATE INDEX IF NOT EXISTS idx_trade_borrow_end_date ON trade_order(borrow_end_date);

DO $$
BEGIN
    RAISE NOTICE '✓ trade_order 表扩展完成';
END $$;

-- ========================================
-- 3. 创建会员信用分表
-- ========================================

DO $$
BEGIN
    RAISE NOTICE '正在创建 member_credit_score 表...';
END $$;

DROP TABLE IF EXISTS member_credit_score;
CREATE TABLE member_credit_score (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    total_score INT4 DEFAULT 100 NOT NULL,
    donate_count INT4 DEFAULT 0,
    borrow_count INT4 DEFAULT 0,
    return_on_time_count INT4 DEFAULT 0,
    overdue_count INT4 DEFAULT 0,
    damage_count INT4 DEFAULT 0,
    violation_count INT4 DEFAULT 0,
    score_change_reason VARCHAR(500),
    last_score_change_date TIMESTAMP,
    
    creator VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT DEFAULT 0 NOT NULL,
    tenant_id BIGINT DEFAULT 0 NOT NULL
);

COMMENT ON TABLE member_credit_score IS '会员信用分表';

CREATE UNIQUE INDEX idx_credit_score_user_id ON member_credit_score(user_id) WHERE deleted = 0;
CREATE INDEX idx_credit_score_total_score ON member_credit_score(total_score);
CREATE INDEX idx_credit_score_tenant_id ON member_credit_score(tenant_id);

DO $$
BEGIN
    RAISE NOTICE '✓ member_credit_score 表创建完成';
END $$;

-- ========================================
-- 4. 创建信用分变动记录表
-- ========================================

DO $$
BEGIN
    RAISE NOTICE '正在创建 member_credit_log 表...';
END $$;

DROP TABLE IF EXISTS member_credit_log;
CREATE TABLE member_credit_log (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    change_type INT2 NOT NULL,
    change_score INT4 NOT NULL,
    before_score INT4 NOT NULL,
    after_score INT4 NOT NULL,
    reason VARCHAR(500),
    related_order_id BIGINT,
    
    creator VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT DEFAULT 0 NOT NULL,
    tenant_id BIGINT DEFAULT 0 NOT NULL
);

COMMENT ON TABLE member_credit_log IS '会员信用分变动记录表';

CREATE INDEX idx_credit_log_user_id ON member_credit_log(user_id);
CREATE INDEX idx_credit_log_change_type ON member_credit_log(change_type);
CREATE INDEX idx_credit_log_create_time ON member_credit_log(create_time);

DO $$
BEGIN
    RAISE NOTICE '✓ member_credit_log 表创建完成';
END $$;

-- ========================================
-- 5. 创建器材预约表
-- ========================================

DO $$
BEGIN
    RAISE NOTICE '正在创建 equipment_reservation 表...';
END $$;

DROP TABLE IF EXISTS equipment_reservation;
CREATE TABLE equipment_reservation (
    id BIGSERIAL PRIMARY KEY,
    equipment_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    reservation_status INT2 DEFAULT 0,
    reservation_date TIMESTAMP NOT NULL,
    plan_borrow_date TIMESTAMP NOT NULL,
    plan_return_date TIMESTAMP NOT NULL,
    cancel_reason VARCHAR(500),
    owner_user_id BIGINT NOT NULL,
    
    creator VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT DEFAULT 0 NOT NULL,
    tenant_id BIGINT DEFAULT 0 NOT NULL
);

COMMENT ON TABLE equipment_reservation IS '器材预约表';

CREATE INDEX idx_reservation_equipment_id ON equipment_reservation(equipment_id);
CREATE INDEX idx_reservation_user_id ON equipment_reservation(user_id);
CREATE INDEX idx_reservation_status ON equipment_reservation(reservation_status);
CREATE INDEX idx_reservation_owner_user_id ON equipment_reservation(owner_user_id);

DO $$
BEGIN
    RAISE NOTICE '✓ equipment_reservation 表创建完成';
END $$;

-- ========================================
-- 完成提示
-- ========================================

DO $$
BEGIN
    RAISE NOTICE '========================================';
    RAISE NOTICE '康复训练器材共享平台改造完成！';
    RAISE NOTICE '========================================';
    RAISE NOTICE '已完成以下改造:';
    RAISE NOTICE '1. product_spu 表新增10个器材字段';
    RAISE NOTICE '2. trade_order 表新增10个借用字段';
    RAISE NOTICE '3. 创建 member_credit_score 信用分表';
    RAISE NOTICE '4. 创建 member_credit_log 信用分记录表';
    RAISE NOTICE '5. 创建 equipment_reservation 预约表';
    RAISE NOTICE '========================================';
    RAISE NOTICE '注意事项:';
    RAISE NOTICE '1. 数据字典需要在管理后台手动配置';
    RAISE NOTICE '2. 或者在执行 ruoyi-vue-pro.sql 后';
    RAISE NOTICE '   再执行 equipment_renovation.sql';
    RAISE NOTICE '========================================';
END $$;


-- ============================================
-- 确认型荣誉系统 - 数据库脚本
-- 设计理念：只确认"发生过"，不评价"好不好"
-- ============================================

-- 1. 勋章定义表（叙事型勋章）
DROP TABLE IF EXISTS member_badge_definition CASCADE;
CREATE TABLE member_badge_definition (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,           -- 勋章编码
    name VARCHAR(100) NOT NULL,                 -- 勋章名称（叙事型）
    description VARCHAR(500),                   -- 触发时的温暖文案
    icon VARCHAR(255),                          -- 图标（emoji或图片URL）
    category VARCHAR(50) NOT NULL,              -- 类别: growth_memory / connection / presence
    trigger_event VARCHAR(100) NOT NULL,        -- 触发事件类型
    sort INT DEFAULT 0,                         -- 排序
    status SMALLINT DEFAULT 0,                  -- 状态 0-启用 1-禁用
    creator VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP DEFAULT NOW(),
    updater VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP DEFAULT NOW(),
    deleted SMALLINT DEFAULT 0
);

COMMENT ON TABLE member_badge_definition IS '勋章定义表';
COMMENT ON COLUMN member_badge_definition.code IS '勋章编码';
COMMENT ON COLUMN member_badge_definition.name IS '勋章名称';
COMMENT ON COLUMN member_badge_definition.description IS '触发时的温暖文案';
COMMENT ON COLUMN member_badge_definition.icon IS '图标';
COMMENT ON COLUMN member_badge_definition.category IS '类别: growth_memory-成长记忆 / connection-连接确认 / presence-存在确认';
COMMENT ON COLUMN member_badge_definition.trigger_event IS '触发事件类型';

-- 2. 用户勋章记录表（只记录"发生过"）
DROP TABLE IF EXISTS member_user_badge CASCADE;
CREATE TABLE member_user_badge (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,                    -- 用户ID
    badge_code VARCHAR(50) NOT NULL,            -- 勋章编码
    first_unlock_time TIMESTAMP DEFAULT NOW(),  -- 第一次触发时间
    is_displayed BOOLEAN DEFAULT FALSE,         -- 是否在主页展示
    creator VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP DEFAULT NOW(),
    updater VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP DEFAULT NOW(),
    deleted SMALLINT DEFAULT 0,
    UNIQUE(user_id, badge_code)
);

CREATE INDEX idx_user_badge_user_id ON member_user_badge(user_id);

COMMENT ON TABLE member_user_badge IS '用户勋章记录表';
COMMENT ON COLUMN member_user_badge.user_id IS '用户ID';
COMMENT ON COLUMN member_user_badge.badge_code IS '勋章编码';
COMMENT ON COLUMN member_user_badge.first_unlock_time IS '第一次触发时间';
COMMENT ON COLUMN member_user_badge.is_displayed IS '是否在主页展示';

-- 3. 行为事件表（事件驱动，与勋章解耦）
DROP TABLE IF EXISTS member_behavior_event CASCADE;
CREATE TABLE member_behavior_event (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,                    -- 用户ID
    event_type VARCHAR(50) NOT NULL,            -- 事件类型
    event_data JSONB,                           -- 事件详情
    ref_id BIGINT,                              -- 关联业务ID
    ref_type VARCHAR(50),                       -- 关联业务类型
    creator VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP DEFAULT NOW(),
    updater VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP DEFAULT NOW(),
    deleted SMALLINT DEFAULT 0
);

CREATE INDEX idx_behavior_event_user_id ON member_behavior_event(user_id);
CREATE INDEX idx_behavior_event_type ON member_behavior_event(event_type);
CREATE INDEX idx_behavior_event_create_time ON member_behavior_event(create_time);

COMMENT ON TABLE member_behavior_event IS '行为事件表';
COMMENT ON COLUMN member_behavior_event.event_type IS '事件类型: training_complete/post_publish/relay_give等';
COMMENT ON COLUMN member_behavior_event.event_data IS '事件详情JSON';

-- 4. 后台统计表（运营用，用户不可见）
DROP TABLE IF EXISTS member_stats_internal CASCADE;
CREATE TABLE member_stats_internal (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,             -- 用户ID
    training_days INT DEFAULT 0,                -- 训练天数
    post_count INT DEFAULT 0,                   -- 帖子数
    comment_count INT DEFAULT 0,                -- 评论数
    relay_give_count INT DEFAULT 0,             -- 赠送物品数
    relay_receive_count INT DEFAULT 0,          -- 接收物品数
    days_with_us INT DEFAULT 0,                 -- 加入天数
    last_active_time TIMESTAMP,                 -- 最后活跃时间
    creator VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP DEFAULT NOW(),
    updater VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP DEFAULT NOW(),
    deleted SMALLINT DEFAULT 0
);

COMMENT ON TABLE member_stats_internal IS '后台统计表（仅运营可见）';

-- 5. 接力记忆表（不统计"帮助了多少人"）
DROP TABLE IF EXISTS relay_memory CASCADE;
CREATE TABLE relay_memory (
    id BIGSERIAL PRIMARY KEY,
    item_id BIGINT NOT NULL,                    -- 物品ID
    original_item_id BIGINT,                    -- 原始物品ID（追溯第一位捐赠者）
    from_user_id BIGINT NOT NULL,               -- 传递者
    to_user_id BIGINT NOT NULL,                 -- 接收者
    sequence_num INT DEFAULT 1,                 -- 接力序号（第几棒）
    moment_message TEXT,                        -- "那一刻的心情"
    creator VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP DEFAULT NOW(),
    updater VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP DEFAULT NOW(),
    deleted SMALLINT DEFAULT 0
);

CREATE INDEX idx_relay_memory_item_id ON relay_memory(item_id);
CREATE INDEX idx_relay_memory_from_user ON relay_memory(from_user_id);
CREATE INDEX idx_relay_memory_to_user ON relay_memory(to_user_id);

COMMENT ON TABLE relay_memory IS '接力记忆表';
COMMENT ON COLUMN relay_memory.moment_message IS '那一刻的心情';
COMMENT ON COLUMN relay_memory.sequence_num IS '接力序号（第几棒）';

-- 6. 感谢私信表（不公开，不精选）
DROP TABLE IF EXISTS relay_thank_message CASCADE;
CREATE TABLE relay_thank_message (
    id BIGSERIAL PRIMARY KEY,
    from_user_id BIGINT NOT NULL,               -- 发送者
    to_user_id BIGINT NOT NULL,                 -- 接收者
    relay_memory_id BIGINT NOT NULL,            -- 关联接力记忆
    item_id BIGINT NOT NULL,                    -- 物品ID
    message TEXT NOT NULL,                      -- 感谢内容
    is_read BOOLEAN DEFAULT FALSE,              -- 是否已读
    creator VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP DEFAULT NOW(),
    updater VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP DEFAULT NOW(),
    deleted SMALLINT DEFAULT 0
);

CREATE INDEX idx_thank_message_to_user ON relay_thank_message(to_user_id);
CREATE INDEX idx_thank_message_is_read ON relay_thank_message(to_user_id, is_read);

COMMENT ON TABLE relay_thank_message IS '感谢私信表';

-- ============================================
-- 初始化勋章数据（叙事型勋章）
-- ============================================

INSERT INTO member_badge_definition (code, name, description, icon, category, trigger_event, sort) VALUES
-- 成长记忆类
('first_step', '第一步', '迈出第一步，就已经很了不起', '👣', 'growth_memory', 'training_first_complete', 1),
('slow_is_ok', '慢慢走也没关系', '中断过也没关系，你回来了', '🐢', 'growth_memory', 'training_resume_after_break', 2),
('today_with_you', '今天也陪着', '今天，你陪在孩子身边', '🤝', 'growth_memory', 'training_complete', 3),
('week_together', '一周的陪伴', '这一周，你一直都在', '🌈', 'growth_memory', 'training_week_complete', 4),

-- 连接确认类
('someone_caught_it', '有人接住了', '你传递的东西，有人接住了', '🎁', 'connection', 'relay_item_matched', 10),
('warmth_flows', '温暖没有停', '温暖在流动，没有停在你这里', '🔄', 'connection', 'relay_chain_extended', 11),
('been_thanked', '被感谢过', '有人想对你说谢谢', '💌', 'connection', 'thank_received', 12),
('not_alone', '同行者', '你不是一个人在走这条路', '👥', 'connection', 'group_first_join', 13),
('someone_sees_you', '有人懂你', '有人认真看过你写的', '👀', 'connection', 'post_first_comment_received', 14),

-- 存在确认类
('left_a_mark', '留下痕迹', '你的故事，被记录下来了', '✍️', 'presence', 'post_first_publish', 20),
('someone_kept_it', '有人收藏', '有人把你的话放进了口袋', '💝', 'presence', 'post_first_collected', 21),
('quiet_companion', '安静陪伴', '你一直都在', '🌙', 'presence', 'app_visit_7_days', 22),
('first_share', '第一次分享', '谢谢你愿意分享', '📤', 'presence', 'first_share', 23);

-- ============================================
-- 验证创建结果
-- ============================================
SELECT 'member_badge_definition' as table_name, COUNT(*) as count FROM member_badge_definition
UNION ALL
SELECT 'member_user_badge', COUNT(*) FROM member_user_badge
UNION ALL
SELECT 'member_behavior_event', COUNT(*) FROM member_behavior_event
UNION ALL
SELECT 'member_stats_internal', COUNT(*) FROM member_stats_internal
UNION ALL
SELECT 'relay_memory', COUNT(*) FROM relay_memory
UNION ALL
SELECT 'relay_thank_message', COUNT(*) FROM relay_thank_message;

-- 查看勋章定义
SELECT code, name, description, category FROM member_badge_definition ORDER BY sort;


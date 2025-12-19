-- =============================================
-- 同行小组模块 - 数据库表结构
-- 数据库类型: PostgreSQL
-- 说明: 业务库是唯一真相源，NodeBB 只是同步的"投影"
-- =============================================

-- ---------------------------------------------
-- 1. peer_group 同行小组表
-- ---------------------------------------------
CREATE TABLE IF NOT EXISTS peer_group (
    id              BIGSERIAL       PRIMARY KEY,
    name            VARCHAR(100)    NOT NULL,
    icon            VARCHAR(50)     DEFAULT NULL,
    theme           VARCHAR(20)     DEFAULT 'blue',
    cover_url       VARCHAR(512)    DEFAULT NULL,
    description     VARCHAR(500)    DEFAULT NULL,
    category        VARCHAR(50)     DEFAULT NULL,
    tags            VARCHAR(500)    DEFAULT NULL,
    age_group       VARCHAR(20)     DEFAULT NULL,
    max_members     INT             DEFAULT 200,
    member_count    INT             DEFAULT 0,
    
    -- 微信群相关
    wechat_qr_url   VARCHAR(512)    DEFAULT NULL,
    wechat_qr_expire TIMESTAMP      DEFAULT NULL,
    wecom_url       VARCHAR(512)    DEFAULT NULL,
    
    -- NodeBB 关联
    nodebb_group_id BIGINT          DEFAULT NULL,
    nodebb_category_id BIGINT       DEFAULT NULL,
    nodebb_sync_status SMALLINT     DEFAULT 0,
    nodebb_synced_at TIMESTAMP      DEFAULT NULL,
    
    -- 状态与审核
    status          SMALLINT        DEFAULT 0,
    join_mode       SMALLINT        DEFAULT 0,
    visibility      SMALLINT        DEFAULT 0,
    
    -- 创建者
    creator_id      BIGINT          DEFAULT NULL,
    
    -- 通用字段
    creator         VARCHAR(64)     DEFAULT '',
    create_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater         VARCHAR(64)     DEFAULT '',
    update_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted         SMALLINT        DEFAULT 0 NOT NULL,
    tenant_id       BIGINT          DEFAULT 0 NOT NULL
);

-- 添加注释
COMMENT ON TABLE peer_group IS '同行小组表';
COMMENT ON COLUMN peer_group.id IS '小组编号';
COMMENT ON COLUMN peer_group.name IS '小组名称';
COMMENT ON COLUMN peer_group.icon IS '小组图标（emoji或icon code）';
COMMENT ON COLUMN peer_group.theme IS '主题色：blue/orange/purple/green/yellow';
COMMENT ON COLUMN peer_group.cover_url IS '小组封面图URL';
COMMENT ON COLUMN peer_group.description IS '小组简介';
COMMENT ON COLUMN peer_group.category IS '分类：language/emotion/sensory/social/kindergarten';
COMMENT ON COLUMN peer_group.tags IS '标签，JSON数组';
COMMENT ON COLUMN peer_group.age_group IS '适用年龄组：0-3/3-6/6-12';
COMMENT ON COLUMN peer_group.max_members IS '最大成员数';
COMMENT ON COLUMN peer_group.member_count IS '当前成员数（冗余计数）';
COMMENT ON COLUMN peer_group.wechat_qr_url IS '微信群二维码图片URL';
COMMENT ON COLUMN peer_group.wechat_qr_expire IS '二维码过期时间';
COMMENT ON COLUMN peer_group.wecom_url IS '企业微信客户群链接（可选）';
COMMENT ON COLUMN peer_group.nodebb_group_id IS 'NodeBB Group ID（同步后回填）';
COMMENT ON COLUMN peer_group.nodebb_category_id IS 'NodeBB 私密分类 ID';
COMMENT ON COLUMN peer_group.nodebb_sync_status IS '同步状态：0-待同步 1-已同步 2-同步失败';
COMMENT ON COLUMN peer_group.nodebb_synced_at IS '最后同步时间';
COMMENT ON COLUMN peer_group.status IS '状态：0-开启 1-关闭';
COMMENT ON COLUMN peer_group.join_mode IS '加入模式：0-自由加入 1-需要审核 2-仅邀请';
COMMENT ON COLUMN peer_group.visibility IS '可见性：0-公开 1-仅成员可见';
COMMENT ON COLUMN peer_group.creator_id IS '创建人用户ID';

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_peer_group_category ON peer_group(category);
CREATE INDEX IF NOT EXISTS idx_peer_group_age_group ON peer_group(age_group);
CREATE INDEX IF NOT EXISTS idx_peer_group_status ON peer_group(status);
CREATE INDEX IF NOT EXISTS idx_peer_group_nodebb_group_id ON peer_group(nodebb_group_id);

-- ---------------------------------------------
-- 2. peer_group_member 小组成员表
-- ---------------------------------------------
CREATE TABLE IF NOT EXISTS peer_group_member (
    id              BIGSERIAL       PRIMARY KEY,
    group_id        BIGINT          NOT NULL,
    user_id         BIGINT          NOT NULL,
    
    -- 角色与权限
    role            VARCHAR(20)     DEFAULT 'member',
    
    -- 状态
    status          SMALLINT        DEFAULT 0,
    join_source     VARCHAR(50)     DEFAULT 'self',
    invited_by      BIGINT          DEFAULT NULL,
    
    -- 时间戳
    joined_at       TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    left_at         TIMESTAMP       DEFAULT NULL,
    
    -- NodeBB 同步
    nodebb_synced   SMALLINT        DEFAULT 0,
    nodebb_synced_at TIMESTAMP      DEFAULT NULL,
    
    -- 用户在小组内的设置
    notify_enabled  SMALLINT        DEFAULT 1,
    last_read_at    TIMESTAMP       DEFAULT NULL,
    
    -- 通用字段
    creator         VARCHAR(64)     DEFAULT '',
    create_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater         VARCHAR(64)     DEFAULT '',
    update_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted         SMALLINT        DEFAULT 0 NOT NULL,
    tenant_id       BIGINT          DEFAULT 0 NOT NULL
);

-- 添加注释
COMMENT ON TABLE peer_group_member IS '同行小组成员表';
COMMENT ON COLUMN peer_group_member.id IS '成员记录编号';
COMMENT ON COLUMN peer_group_member.group_id IS '小组ID';
COMMENT ON COLUMN peer_group_member.user_id IS '会员用户ID';
COMMENT ON COLUMN peer_group_member.role IS '角色：owner/admin/member';
COMMENT ON COLUMN peer_group_member.status IS '状态：0-正常 1-静音 2-已退出 3-被移除 4-待审核';
COMMENT ON COLUMN peer_group_member.join_source IS '加入来源：self/invite/admin/auto_match';
COMMENT ON COLUMN peer_group_member.invited_by IS '邀请人用户ID';
COMMENT ON COLUMN peer_group_member.joined_at IS '加入时间';
COMMENT ON COLUMN peer_group_member.left_at IS '退出时间';
COMMENT ON COLUMN peer_group_member.nodebb_synced IS 'NodeBB成员同步状态';
COMMENT ON COLUMN peer_group_member.nodebb_synced_at IS '最后同步时间';
COMMENT ON COLUMN peer_group_member.notify_enabled IS '是否接收小组通知';
COMMENT ON COLUMN peer_group_member.last_read_at IS '最后阅读时间';

-- 创建索引
CREATE UNIQUE INDEX IF NOT EXISTS uk_peer_group_member_group_user ON peer_group_member(group_id, user_id);
CREATE INDEX IF NOT EXISTS idx_peer_group_member_user_id ON peer_group_member(user_id);
CREATE INDEX IF NOT EXISTS idx_peer_group_member_status ON peer_group_member(status);
CREATE INDEX IF NOT EXISTS idx_peer_group_member_role ON peer_group_member(role);

-- ---------------------------------------------
-- 3. nodebb_sync_outbox 同步发件箱表
-- 实现 Outbox 模式，避免直接同步导致的请求阻塞
-- ---------------------------------------------
CREATE TABLE IF NOT EXISTS nodebb_sync_outbox (
    id              BIGSERIAL       PRIMARY KEY,
    
    -- 同步类型与操作
    sync_type       VARCHAR(50)     NOT NULL,
    operation       VARCHAR(20)     NOT NULL,
    
    -- 关联业务
    biz_type        VARCHAR(50)     NOT NULL,
    biz_id          BIGINT          NOT NULL,
    
    -- 同步数据（JSON）
    payload         TEXT            NOT NULL,
    
    -- 状态与重试
    status          SMALLINT        DEFAULT 0,
    retry_count     INT             DEFAULT 0,
    max_retries     INT             DEFAULT 5,
    next_retry_at   TIMESTAMP       DEFAULT NULL,
    
    -- 结果
    result          TEXT            DEFAULT NULL,
    processed_at    TIMESTAMP       DEFAULT NULL,
    
    -- 通用字段
    creator         VARCHAR(64)     DEFAULT '',
    create_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater         VARCHAR(64)     DEFAULT '',
    update_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted         SMALLINT        DEFAULT 0 NOT NULL,
    tenant_id       BIGINT          DEFAULT 0 NOT NULL
);

-- 添加注释
COMMENT ON TABLE nodebb_sync_outbox IS 'NodeBB同步发件箱表（Outbox模式）';
COMMENT ON COLUMN nodebb_sync_outbox.id IS '消息编号';
COMMENT ON COLUMN nodebb_sync_outbox.sync_type IS '同步类型：GROUP_CREATE/GROUP_UPDATE/MEMBER_JOIN/MEMBER_LEAVE/CATEGORY_CREATE';
COMMENT ON COLUMN nodebb_sync_outbox.operation IS '操作：CREATE/UPDATE/DELETE';
COMMENT ON COLUMN nodebb_sync_outbox.biz_type IS '业务类型：PEER_GROUP/PEER_GROUP_MEMBER';
COMMENT ON COLUMN nodebb_sync_outbox.biz_id IS '业务ID';
COMMENT ON COLUMN nodebb_sync_outbox.payload IS '同步数据JSON';
COMMENT ON COLUMN nodebb_sync_outbox.status IS '状态：0-待处理 1-处理中 2-成功 3-失败';
COMMENT ON COLUMN nodebb_sync_outbox.retry_count IS '重试次数';
COMMENT ON COLUMN nodebb_sync_outbox.max_retries IS '最大重试次数';
COMMENT ON COLUMN nodebb_sync_outbox.next_retry_at IS '下次重试时间';
COMMENT ON COLUMN nodebb_sync_outbox.result IS '处理结果/错误信息';
COMMENT ON COLUMN nodebb_sync_outbox.processed_at IS '处理完成时间';

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_nodebb_sync_outbox_status ON nodebb_sync_outbox(status);
CREATE INDEX IF NOT EXISTS idx_nodebb_sync_outbox_sync_type ON nodebb_sync_outbox(sync_type);
CREATE INDEX IF NOT EXISTS idx_nodebb_sync_outbox_biz ON nodebb_sync_outbox(biz_type, biz_id);
CREATE INDEX IF NOT EXISTS idx_nodebb_sync_outbox_next_retry ON nodebb_sync_outbox(next_retry_at);

-- ---------------------------------------------
-- 4. peer_group_join_request 加入申请表（用于审核模式）
-- ---------------------------------------------
CREATE TABLE IF NOT EXISTS peer_group_join_request (
    id              BIGSERIAL       PRIMARY KEY,
    group_id        BIGINT          NOT NULL,
    user_id         BIGINT          NOT NULL,
    
    -- 申请信息
    reason          VARCHAR(500)    DEFAULT NULL,
    
    -- 状态
    status          SMALLINT        DEFAULT 0,
    reviewed_by     BIGINT          DEFAULT NULL,
    reviewed_at     TIMESTAMP       DEFAULT NULL,
    reject_reason   VARCHAR(200)    DEFAULT NULL,
    
    -- 通用字段
    creator         VARCHAR(64)     DEFAULT '',
    create_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater         VARCHAR(64)     DEFAULT '',
    update_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted         SMALLINT        DEFAULT 0 NOT NULL,
    tenant_id       BIGINT          DEFAULT 0 NOT NULL
);

-- 添加注释
COMMENT ON TABLE peer_group_join_request IS '同行小组加入申请表';
COMMENT ON COLUMN peer_group_join_request.id IS '申请编号';
COMMENT ON COLUMN peer_group_join_request.group_id IS '小组ID';
COMMENT ON COLUMN peer_group_join_request.user_id IS '申请人用户ID';
COMMENT ON COLUMN peer_group_join_request.reason IS '申请理由';
COMMENT ON COLUMN peer_group_join_request.status IS '状态：0-待审核 1-已通过 2-已拒绝 3-已过期';
COMMENT ON COLUMN peer_group_join_request.reviewed_by IS '审核人用户ID';
COMMENT ON COLUMN peer_group_join_request.reviewed_at IS '审核时间';
COMMENT ON COLUMN peer_group_join_request.reject_reason IS '拒绝原因';

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_peer_group_join_request_group_user ON peer_group_join_request(group_id, user_id);
CREATE INDEX IF NOT EXISTS idx_peer_group_join_request_status ON peer_group_join_request(status);

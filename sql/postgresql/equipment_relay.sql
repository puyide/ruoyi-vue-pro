-- =============================================
-- 康复训练器材共享平台 - 成长接力（器材交易）模块
-- 数据库：PostgreSQL
-- 
-- 支持三种交易模式：
-- 1. 赠送 (give) - 免费赠送
-- 2. 借用 (borrow) - 借用后归还
-- 3. 低价转让 (sell) - 低价出售
-- =============================================

-- 修复序列值（如果需要）
-- SELECT setval('relay_item_seq', (SELECT COALESCE(MAX(id), 0) + 1 FROM relay_item), false);

-- =============================================
-- 1. 接力好物表 (relay_item)
-- =============================================

CREATE SEQUENCE IF NOT EXISTS relay_item_seq START 1;

CREATE TABLE IF NOT EXISTS relay_item (
    id              BIGINT          NOT NULL DEFAULT nextval('relay_item_seq'),
    user_id         BIGINT          NOT NULL,
    title           VARCHAR(100)    NOT NULL,
    description     TEXT            NULL,
    photos          TEXT            NULL,
    condition       VARCHAR(20)     NOT NULL,
    type            VARCHAR(50)     NOT NULL,
    age_group       VARCHAR(20)     NOT NULL,
    relay_method    SMALLINT        NOT NULL DEFAULT 1,
    delivery_method SMALLINT        NOT NULL DEFAULT 1,
    price           INT             NULL DEFAULT 0,
    status          SMALLINT        NOT NULL DEFAULT 0,
    view_count      INT             NOT NULL DEFAULT 0,
    request_count   INT             NOT NULL DEFAULT 0,
    matched_user_id BIGINT          NULL,
    matched_time    TIMESTAMP       NULL,
    completed_time  TIMESTAMP       NULL,
    reject_reason   VARCHAR(255)    NULL,
    creator         VARCHAR(64)     NULL DEFAULT '',
    create_time     TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater         VARCHAR(64)     NULL DEFAULT '',
    update_time     TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted         SMALLINT        NOT NULL DEFAULT 0,
    tenant_id       BIGINT          NOT NULL DEFAULT 0,
    CONSTRAINT pk_relay_item PRIMARY KEY (id)
);

CREATE INDEX idx_relay_item_user_id ON relay_item(user_id);
CREATE INDEX idx_relay_item_status ON relay_item(status);
CREATE INDEX idx_relay_item_relay_method ON relay_item(relay_method);
CREATE INDEX idx_relay_item_type ON relay_item(type);

COMMENT ON TABLE relay_item IS '接力好物表';
COMMENT ON COLUMN relay_item.id IS '好物ID';
COMMENT ON COLUMN relay_item.user_id IS '发布者用户ID';
COMMENT ON COLUMN relay_item.title IS '好物名称';
COMMENT ON COLUMN relay_item.description IS '物品说明';
COMMENT ON COLUMN relay_item.photos IS '照片列表(JSON)';
COMMENT ON COLUMN relay_item.condition IS '新旧程度: brand_new/95_new/9_new/8_new/7_new';
COMMENT ON COLUMN relay_item.type IS '好物类型: language/sensory/attention/social/emotion/other';
COMMENT ON COLUMN relay_item.age_group IS '适用年龄: 0-3/3-6/6-12/all';
COMMENT ON COLUMN relay_item.relay_method IS '交易方式: 1-赠送 2-借用 3-低价转让';
COMMENT ON COLUMN relay_item.delivery_method IS '交付方式: 1-当面交接 2-快递寄送';
COMMENT ON COLUMN relay_item.price IS '价格(分)，仅低价转让时有效';
COMMENT ON COLUMN relay_item.status IS '状态: 0-待审核 1-接力中 2-已匹配 3-交接中 4-已完成 5-审核拒绝 6-已下架';
COMMENT ON COLUMN relay_item.view_count IS '浏览次数';
COMMENT ON COLUMN relay_item.request_count IS '申请次数';
COMMENT ON COLUMN relay_item.matched_user_id IS '匹配的用户ID';
COMMENT ON COLUMN relay_item.matched_time IS '匹配时间';
COMMENT ON COLUMN relay_item.completed_time IS '完成时间';
COMMENT ON COLUMN relay_item.reject_reason IS '审核拒绝原因';


-- =============================================
-- 2. 接力申请表 (relay_request)
-- =============================================

CREATE SEQUENCE IF NOT EXISTS relay_request_seq START 1;

CREATE TABLE IF NOT EXISTS relay_request (
    id                  BIGINT          NOT NULL DEFAULT nextval('relay_request_seq'),
    item_id             BIGINT          NOT NULL,
    requester_id        BIGINT          NOT NULL,
    giver_id            BIGINT          NOT NULL,
    message             VARCHAR(500)    NULL,
    status              SMALLINT        NOT NULL DEFAULT 0,
    requester_wechat    VARCHAR(50)     NULL,
    giver_wechat        VARCHAR(50)     NULL,
    contact_exchanged   BOOLEAN         NOT NULL DEFAULT FALSE,
    accept_time         TIMESTAMP       NULL,
    reject_reason       VARCHAR(255)    NULL,
    creator             VARCHAR(64)     NULL DEFAULT '',
    create_time         TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater             VARCHAR(64)     NULL DEFAULT '',
    update_time         TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted             SMALLINT        NOT NULL DEFAULT 0,
    tenant_id           BIGINT          NOT NULL DEFAULT 0,
    CONSTRAINT pk_relay_request PRIMARY KEY (id)
);

CREATE INDEX idx_relay_request_item_id ON relay_request(item_id);
CREATE INDEX idx_relay_request_requester_id ON relay_request(requester_id);
CREATE INDEX idx_relay_request_giver_id ON relay_request(giver_id);
CREATE INDEX idx_relay_request_status ON relay_request(status);

COMMENT ON TABLE relay_request IS '接力申请表';
COMMENT ON COLUMN relay_request.id IS '申请ID';
COMMENT ON COLUMN relay_request.item_id IS '好物ID';
COMMENT ON COLUMN relay_request.requester_id IS '申请者用户ID';
COMMENT ON COLUMN relay_request.giver_id IS '发布者用户ID';
COMMENT ON COLUMN relay_request.message IS '申请留言';
COMMENT ON COLUMN relay_request.status IS '状态: 0-待处理 1-已同意 2-已拒绝 3-已取消 4-已完成';
COMMENT ON COLUMN relay_request.requester_wechat IS '申请者微信号';
COMMENT ON COLUMN relay_request.giver_wechat IS '发布者微信号';
COMMENT ON COLUMN relay_request.contact_exchanged IS '是否已交换联系方式';
COMMENT ON COLUMN relay_request.accept_time IS '同意时间';
COMMENT ON COLUMN relay_request.reject_reason IS '拒绝原因';


-- =============================================
-- 3. 接力交接记录表 (relay_transfer)
-- =============================================

CREATE SEQUENCE IF NOT EXISTS relay_transfer_seq START 1;

CREATE TABLE IF NOT EXISTS relay_transfer (
    id                  BIGINT          NOT NULL DEFAULT nextval('relay_transfer_seq'),
    item_id             BIGINT          NOT NULL,
    request_id          BIGINT          NOT NULL,
    giver_id            BIGINT          NOT NULL,
    receiver_id         BIGINT          NOT NULL,
    transfer_method     SMALLINT        NOT NULL DEFAULT 1,
    express_company     VARCHAR(50)     NULL,
    express_no          VARCHAR(50)     NULL,
    transfer_time       TIMESTAMP       NULL,
    giver_confirm       BOOLEAN         NOT NULL DEFAULT FALSE,
    receiver_confirm    BOOLEAN         NOT NULL DEFAULT FALSE,
    status              SMALLINT        NOT NULL DEFAULT 0,
    return_required     BOOLEAN         NOT NULL DEFAULT FALSE,
    return_date         DATE            NULL,
    return_status       SMALLINT        NULL DEFAULT 0,
    -- 低价转让-支付凭证相关
    transfer_price      INT             NULL DEFAULT 0,
    payment_proof_photos TEXT           NULL,
    payment_proof_time  TIMESTAMP       NULL,
    payment_confirmed   BOOLEAN         NOT NULL DEFAULT FALSE,
    payment_confirm_time TIMESTAMP      NULL,
    payment_remark      VARCHAR(255)    NULL,
    payment_status      SMALLINT        NULL DEFAULT 0,
    creator             VARCHAR(64)     NULL DEFAULT '',
    create_time         TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater             VARCHAR(64)     NULL DEFAULT '',
    update_time         TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted             SMALLINT        NOT NULL DEFAULT 0,
    tenant_id           BIGINT          NOT NULL DEFAULT 0,
    CONSTRAINT pk_relay_transfer PRIMARY KEY (id)
);

CREATE INDEX idx_relay_transfer_item_id ON relay_transfer(item_id);
CREATE INDEX idx_relay_transfer_giver_id ON relay_transfer(giver_id);
CREATE INDEX idx_relay_transfer_receiver_id ON relay_transfer(receiver_id);

COMMENT ON TABLE relay_transfer IS '接力交接记录表';
COMMENT ON COLUMN relay_transfer.id IS '交接ID';
COMMENT ON COLUMN relay_transfer.item_id IS '好物ID';
COMMENT ON COLUMN relay_transfer.request_id IS '关联申请ID';
COMMENT ON COLUMN relay_transfer.giver_id IS '出借/赠送方用户ID';
COMMENT ON COLUMN relay_transfer.receiver_id IS '接收方用户ID';
COMMENT ON COLUMN relay_transfer.transfer_method IS '交接方式: 1-当面交接 2-快递';
COMMENT ON COLUMN relay_transfer.express_company IS '快递公司';
COMMENT ON COLUMN relay_transfer.express_no IS '快递单号';
COMMENT ON COLUMN relay_transfer.transfer_time IS '交接时间';
COMMENT ON COLUMN relay_transfer.giver_confirm IS '出借方是否确认';
COMMENT ON COLUMN relay_transfer.receiver_confirm IS '接收方是否确认';
COMMENT ON COLUMN relay_transfer.status IS '状态: 0-待交接 1-交接中 2-已完成';
COMMENT ON COLUMN relay_transfer.return_required IS '是否需要归还(借用模式)';
COMMENT ON COLUMN relay_transfer.return_date IS '预计归还日期';
COMMENT ON COLUMN relay_transfer.return_status IS '归还状态: 0-未归还 1-已归还 2-逾期';
COMMENT ON COLUMN relay_transfer.transfer_price IS '转让价格(分)';
COMMENT ON COLUMN relay_transfer.payment_proof_photos IS '支付凭证图片(JSON数组)';
COMMENT ON COLUMN relay_transfer.payment_proof_time IS '支付凭证上传时间';
COMMENT ON COLUMN relay_transfer.payment_confirmed IS '卖家是否确认收款';
COMMENT ON COLUMN relay_transfer.payment_confirm_time IS '确认收款时间';
COMMENT ON COLUMN relay_transfer.payment_remark IS '支付备注';
COMMENT ON COLUMN relay_transfer.payment_status IS '支付状态: 0-待支付 1-已上传凭证 2-卖家已确认 3-有争议';


-- =============================================
-- 4. 感谢/评价表 (relay_thank)
-- =============================================

CREATE SEQUENCE IF NOT EXISTS relay_thank_seq START 1;

CREATE TABLE IF NOT EXISTS relay_thank (
    id              BIGINT          NOT NULL DEFAULT nextval('relay_thank_seq'),
    item_id         BIGINT          NOT NULL,
    transfer_id     BIGINT          NOT NULL,
    from_user_id    BIGINT          NOT NULL,
    to_user_id      BIGINT          NOT NULL,
    content         VARCHAR(500)    NULL,
    is_public       BOOLEAN         NOT NULL DEFAULT TRUE,
    creator         VARCHAR(64)     NULL DEFAULT '',
    create_time     TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater         VARCHAR(64)     NULL DEFAULT '',
    update_time     TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted         SMALLINT        NOT NULL DEFAULT 0,
    tenant_id       BIGINT          NOT NULL DEFAULT 0,
    CONSTRAINT pk_relay_thank PRIMARY KEY (id)
);

CREATE INDEX idx_relay_thank_item_id ON relay_thank(item_id);
CREATE INDEX idx_relay_thank_from_user_id ON relay_thank(from_user_id);
CREATE INDEX idx_relay_thank_to_user_id ON relay_thank(to_user_id);

COMMENT ON TABLE relay_thank IS '接力感谢/评价表';
COMMENT ON COLUMN relay_thank.id IS '感谢ID';
COMMENT ON COLUMN relay_thank.item_id IS '好物ID';
COMMENT ON COLUMN relay_thank.transfer_id IS '交接记录ID';
COMMENT ON COLUMN relay_thank.from_user_id IS '发送者用户ID';
COMMENT ON COLUMN relay_thank.to_user_id IS '接收者用户ID';
COMMENT ON COLUMN relay_thank.content IS '感谢内容';
COMMENT ON COLUMN relay_thank.is_public IS '是否公开显示';


-- =============================================
-- 数据字典配置（可选，在管理后台配置）
-- =============================================

-- 接力交易方式
-- INSERT INTO system_dict_type (name, type, status, remark, creator, create_time, updater, update_time, deleted) 
-- VALUES ('接力交易方式', 'relay_method', 0, '成长接力交易方式', 'admin', NOW(), 'admin', NOW(), 0);
-- 
-- INSERT INTO system_dict_data (sort, label, value, dict_type, status, creator, create_time, updater, update_time, deleted)
-- VALUES (1, '赠送', '1', 'relay_method', 0, 'admin', NOW(), 'admin', NOW(), 0);
-- INSERT INTO system_dict_data (sort, label, value, dict_type, status, creator, create_time, updater, update_time, deleted)
-- VALUES (2, '借用', '2', 'relay_method', 0, 'admin', NOW(), 'admin', NOW(), 0);
-- INSERT INTO system_dict_data (sort, label, value, dict_type, status, creator, create_time, updater, update_time, deleted)
-- VALUES (3, '低价转让', '3', 'relay_method', 0, 'admin', NOW(), 'admin', NOW(), 0);


-- =============================================
-- 验证
-- =============================================
SELECT table_name FROM information_schema.tables 
WHERE table_schema = 'public' 
AND table_name LIKE 'relay_%'
ORDER BY table_name;


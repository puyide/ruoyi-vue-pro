-- =============================================
-- 成长接力模块 - 支付凭证功能升级
-- 用于已有 relay_transfer 表的数据库升级
-- =============================================

-- 添加低价转让支付凭证相关字段
ALTER TABLE relay_transfer ADD COLUMN IF NOT EXISTS transfer_price INT NULL DEFAULT 0;
ALTER TABLE relay_transfer ADD COLUMN IF NOT EXISTS payment_proof_photos TEXT NULL;
ALTER TABLE relay_transfer ADD COLUMN IF NOT EXISTS payment_proof_time TIMESTAMP NULL;
ALTER TABLE relay_transfer ADD COLUMN IF NOT EXISTS payment_confirmed BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE relay_transfer ADD COLUMN IF NOT EXISTS payment_confirm_time TIMESTAMP NULL;
ALTER TABLE relay_transfer ADD COLUMN IF NOT EXISTS payment_remark VARCHAR(255) NULL;
ALTER TABLE relay_transfer ADD COLUMN IF NOT EXISTS payment_status SMALLINT NULL DEFAULT 0;

-- 添加字段注释
COMMENT ON COLUMN relay_transfer.transfer_price IS '转让价格(分)';
COMMENT ON COLUMN relay_transfer.payment_proof_photos IS '支付凭证图片(JSON数组)';
COMMENT ON COLUMN relay_transfer.payment_proof_time IS '支付凭证上传时间';
COMMENT ON COLUMN relay_transfer.payment_confirmed IS '卖家是否确认收款';
COMMENT ON COLUMN relay_transfer.payment_confirm_time IS '确认收款时间';
COMMENT ON COLUMN relay_transfer.payment_remark IS '支付备注';
COMMENT ON COLUMN relay_transfer.payment_status IS '支付状态: 0-待支付 1-已上传凭证 2-卖家已确认 3-有争议';

-- =============================================
-- 支付凭证状态说明
-- =============================================
-- 0 - 待支付：等待买家转账并上传凭证
-- 1 - 已上传凭证：买家已上传转账截图，等待卖家确认
-- 2 - 卖家已确认：卖家确认收到款项
-- 3 - 有争议：支付有争议，需要平台介入

-- =============================================
-- 低价转让完整流程
-- =============================================
-- 1. 买卖双方协商价格 (通过聊天)
-- 2. 买家通过微信/支付宝私下转账给卖家
-- 3. 买家在小程序上传转账凭证截图 → payment_status = 1
-- 4. 卖家查看凭证，确认收款 → payment_status = 2, payment_confirmed = true
-- 5. 双方进行物品交接
-- 6. 如有争议 → payment_status = 3，平台介入处理


package cn.iocoder.yudao.module.trade.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * Trade 错误码枚举类
 *
 * trade 系统，使用 1-011-000-000 段
 */
public interface ErrorCodeConstants {

    // ========== 订单相关 1-011-001-000 ============
    ErrorCode ORDER_NOT_EXISTS = new ErrorCode(1_011_001_000, "订单不存在");
    ErrorCode ORDER_ITEM_NOT_EXISTS = new ErrorCode(1_011_001_001, "订单项不存在");
    ErrorCode ORDER_STATUS_ERROR = new ErrorCode(1_011_001_002, "订单状态不正确");

    // ========== 售后相关 1-011-002-000 ============
    ErrorCode AFTER_SALE_NOT_EXISTS = new ErrorCode(1_011_002_000, "售后单不存在");

    // ========== 购物车相关 1-011-003-000 ============
    ErrorCode CART_ITEM_NOT_EXISTS = new ErrorCode(1_011_003_000, "购物车项不存在");

    // ========== 交接会话相关 1-011-010-000 ============
    ErrorCode HANDOVER_NOT_EXISTS = new ErrorCode(1_011_010_000, "交接会话不存在");
    ErrorCode HANDOVER_EXISTS = new ErrorCode(1_011_010_001, "该预约已创建交接会话");
    ErrorCode HANDOVER_NOT_PARTICIPANT = new ErrorCode(1_011_010_002, "您不是该交接会话的参与方");
    ErrorCode HANDOVER_NOT_LENDER = new ErrorCode(1_011_010_003, "您不是该交接会话的出借方");
    ErrorCode HANDOVER_NOT_BORROWER = new ErrorCode(1_011_010_004, "您不是该交接会话的借用方");
    ErrorCode HANDOVER_STATUS_ERROR = new ErrorCode(1_011_010_005, "交接状态不正确，无法操作");

    // ========== 反馈/评价相关 1-011-011-000 ============
    ErrorCode FEEDBACK_NOT_EXISTS = new ErrorCode(1_011_011_000, "反馈/评价不存在");
    ErrorCode FEEDBACK_EXISTS = new ErrorCode(1_011_011_001, "您已经评价过了");

}


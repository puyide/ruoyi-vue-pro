package cn.iocoder.yudao.module.member.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * Member 错误码枚举类
 * <p>
 * member 系统，使用 1-004-000-000 段
 */
public interface ErrorCodeConstants {

    // ========== 用户相关  1-004-001-000 ============
    ErrorCode USER_NOT_EXISTS = new ErrorCode(1_004_001_000, "用户不存在");
    ErrorCode USER_MOBILE_NOT_EXISTS = new ErrorCode(1_004_001_001, "手机号未注册用户");
    ErrorCode USER_MOBILE_USED = new ErrorCode(1_004_001_002, "修改手机失败，该手机号({})已经被使用");
    ErrorCode USER_POINT_NOT_ENOUGH = new ErrorCode(1_004_001_003, "用户积分余额不足");

    // ========== AUTH 模块 1-004-003-000 ==========
    ErrorCode AUTH_LOGIN_BAD_CREDENTIALS = new ErrorCode(1_004_003_000, "登录失败，账号密码不正确");
    ErrorCode AUTH_LOGIN_USER_DISABLED = new ErrorCode(1_004_003_001, "登录失败，账号被禁用");
    ErrorCode AUTH_SOCIAL_USER_NOT_FOUND = new ErrorCode(1_004_003_005, "登录失败，解析不到三方登录信息");
    ErrorCode AUTH_MOBILE_USED = new ErrorCode(1_004_003_007, "手机号已经被使用");
    ErrorCode NODEBB_NOT_ENABLED = new ErrorCode(1_004_003_100, "NodeBB 集成未启用");
    ErrorCode NODEBB_USER_NOT_SYNCED = new ErrorCode(1_004_003_101, "用户尚未同步到 NodeBB");

    // ========== 用户收件地址 1-004-004-000 ==========
    ErrorCode ADDRESS_NOT_EXISTS = new ErrorCode(1_004_004_000, "用户收件地址不存在");

    //========== 用户标签 1-004-006-000 ==========
    ErrorCode TAG_NOT_EXISTS = new ErrorCode(1_004_006_000, "用户标签不存在");
    ErrorCode TAG_NAME_EXISTS = new ErrorCode(1_004_006_001, "用户标签已经存在");
    ErrorCode TAG_HAS_USER = new ErrorCode(1_004_006_002, "用户标签下存在用户，无法删除");

    //========== 积分配置 1-004-007-000 ==========

    //========== 积分记录 1-004-008-000 ==========
    ErrorCode POINT_RECORD_BIZ_NOT_SUPPORT = new ErrorCode(1_004_008_000, "用户积分记录业务类型不支持");

    //========== 签到配置 1-004-009-000 ==========
    ErrorCode SIGN_IN_CONFIG_NOT_EXISTS = new ErrorCode(1_004_009_000, "签到天数规则不存在");
    ErrorCode SIGN_IN_CONFIG_EXISTS = new ErrorCode(1_004_009_001, "签到天数规则已存在");

    //========== 签到配置 1-004-010-000 ==========
    ErrorCode SIGN_IN_RECORD_TODAY_EXISTS = new ErrorCode(1_004_010_000, "今日已签到，请勿重复签到");

    //========== 用户等级 1-004-011-000 ==========
    ErrorCode LEVEL_NOT_EXISTS = new ErrorCode(1_004_011_000, "用户等级不存在");
    ErrorCode LEVEL_NAME_EXISTS = new ErrorCode(1_004_011_001, "用户等级名称[{}]已被使用");
    ErrorCode LEVEL_VALUE_EXISTS = new ErrorCode(1_004_011_002, "用户等级值[{}]已被[{}]使用");
    ErrorCode LEVEL_EXPERIENCE_MIN = new ErrorCode(1_004_011_003, "升级经验必须大于上一个等级[{}]设置的升级经验[{}]");
    ErrorCode LEVEL_EXPERIENCE_MAX = new ErrorCode(1_004_011_004, "升级经验必须小于下一个等级[{}]设置的升级经验[{}]");
    ErrorCode LEVEL_HAS_USER = new ErrorCode(1_004_011_005, "用户等级下存在用户，无法删除");

    ErrorCode EXPERIENCE_BIZ_NOT_SUPPORT = new ErrorCode(1_004_011_201, "用户经验业务类型不支持");

    //========== 用户分组 1-004-012-000 ==========
    ErrorCode GROUP_NOT_EXISTS = new ErrorCode(1_004_012_000, "用户分组不存在");
    ErrorCode GROUP_HAS_USER = new ErrorCode(1_004_012_001, "用户分组下存在用户，无法删除");

    //========== 儿童信息 1-004-013-000 ==========
    ErrorCode CHILDREN_NOT_EXISTS = new ErrorCode(1_004_013_000, "儿童信息不存在");

    //========== 信用分 1-004-014-000 ==========
    ErrorCode CREDIT_SCORE_NOT_EXISTS = new ErrorCode(1_004_014_000, "信用分记录不存在");
    ErrorCode CREDIT_SCORE_NOT_ENOUGH = new ErrorCode(1_004_014_001, "信用分不足");

    //========== 同行小组 1-004-015-000 ==========
    ErrorCode PEER_GROUP_NOT_EXISTS = new ErrorCode(1_004_015_000, "同行小组不存在");
    ErrorCode PEER_GROUP_NOT_ENABLED = new ErrorCode(1_004_015_001, "同行小组已关闭");
    ErrorCode PEER_GROUP_ALREADY_JOINED = new ErrorCode(1_004_015_002, "您已经是该小组成员");
    ErrorCode PEER_GROUP_MEMBER_FULL = new ErrorCode(1_004_015_003, "小组成员已满");
    ErrorCode PEER_GROUP_INVITE_ONLY = new ErrorCode(1_004_015_004, "该小组仅限邀请加入");
    ErrorCode PEER_GROUP_NOT_MEMBER = new ErrorCode(1_004_015_005, "您不是该小组成员");
    ErrorCode PEER_GROUP_OWNER_CANNOT_LEAVE = new ErrorCode(1_004_015_006, "群主不能退出，请先转让群主身份");
    ErrorCode PEER_GROUP_CANNOT_REMOVE_OWNER = new ErrorCode(1_004_015_007, "不能移除群主");
    ErrorCode PEER_GROUP_JOIN_REQUEST_NOT_FOUND = new ErrorCode(1_004_015_008, "加入申请不存在");
    ErrorCode PEER_GROUP_JOIN_REQUEST_ALREADY_PROCESSED = new ErrorCode(1_004_015_009, "加入申请已处理");
    ErrorCode PEER_GROUP_JOIN_REQUEST_EXISTS = new ErrorCode(1_004_015_010, "已有待审核的加入申请");

    //========== 训练模块 1-004-016-000 ==========
    ErrorCode TRAINING_TEMPLATE_NOT_EXISTS = new ErrorCode(1_004_016_000, "训练模板不存在");
    ErrorCode TRAINING_SESSION_NOT_EXISTS = new ErrorCode(1_004_016_001, "训练会话不存在");
    ErrorCode TRAINING_SESSION_STATUS_ERROR = new ErrorCode(1_004_016_002, "训练会话状态不正确");
    ErrorCode TRAINING_LOG_NOT_EXISTS = new ErrorCode(1_004_016_003, "训练日志不存在");
    ErrorCode TRAINING_LOG_ALREADY_EXISTS = new ErrorCode(1_004_016_004, "该训练会话已打卡");

    //========== 社区模块 1-004-017-000 ==========
    ErrorCode COMMUNITY_TOPIC_NOT_EXISTS = new ErrorCode(1_004_017_000, "帖子不存在");
    ErrorCode COMMUNITY_POST_NOT_EXISTS = new ErrorCode(1_004_017_001, "回帖不存在");
    ErrorCode COMMUNITY_NODEBB_USER_NOT_EXISTS = new ErrorCode(1_004_017_002, "社区用户不存在，请先登录");
    ErrorCode COMMUNITY_CONTENT_MODERATION_FAILED = new ErrorCode(1_004_017_003, "内容审核未通过：{}");
    ErrorCode COMMUNITY_TOPIC_CREATE_FAILED = new ErrorCode(1_004_017_004, "发帖失败，请稍后重试");
    ErrorCode COMMUNITY_REPLY_CREATE_FAILED = new ErrorCode(1_004_017_005, "回帖失败，请稍后重试");
    ErrorCode COMMUNITY_OPERATION_FAILED = new ErrorCode(1_004_017_006, "操作失败，请稍后重试");
    ErrorCode COMMUNITY_RATE_LIMIT_EXCEEDED = new ErrorCode(1_004_017_007, "操作过于频繁，请稍后再试");

}

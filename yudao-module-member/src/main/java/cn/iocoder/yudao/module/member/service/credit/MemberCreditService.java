package cn.iocoder.yudao.module.member.service.credit;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.member.controller.admin.credit.vo.CreditLogPageReqVO;
import cn.iocoder.yudao.module.member.dal.dataobject.credit.MemberCreditLogDO;
import cn.iocoder.yudao.module.member.dal.dataobject.credit.MemberCreditScoreDO;

import java.util.List;

/**
 * 会员信用分 Service 接口
 *
 * @author 芋道源码
 */
public interface MemberCreditService {

    /**
     * 获取用户信用分
     *
     * @param userId 用户ID
     * @return 信用分信息
     */
    MemberCreditScoreDO getCreditScore(Long userId);

    /**
     * 获取或初始化用户信用分
     * 如果用户不存在信用分记录，则初始化
     *
     * @param userId 用户ID
     * @return 信用分信息
     */
    MemberCreditScoreDO getOrInitCreditScore(Long userId);

    /**
     * 增加信用分
     *
     * @param userId 用户ID
     * @param changeType 变动类型
     * @param score 增加的分数（正数）
     * @param reason 变动原因
     * @param relatedOrderId 关联订单ID（可选）
     */
    void addCreditScore(Long userId, Integer changeType, Integer score, String reason, Long relatedOrderId);

    /**
     * 扣减信用分
     *
     * @param userId 用户ID
     * @param changeType 变动类型
     * @param score 扣减的分数（正数，方法内部会转为负数）
     * @param reason 变动原因
     * @param relatedOrderId 关联订单ID（可选）
     */
    void reduceCreditScore(Long userId, Integer changeType, Integer score, String reason, Long relatedOrderId);

    /**
     * 获取用户信用分变动记录列表
     *
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 变动记录列表
     */
    List<MemberCreditLogDO> getCreditLogList(Long userId, Integer limit);

    /**
     * 获取信用分变动记录分页（管理后台）
     *
     * @param pageReqVO 分页查询条件
     * @return 分页结果
     */
    PageResult<MemberCreditLogDO> getCreditLogPage(CreditLogPageReqVO pageReqVO);

    /**
     * 增加捐赠次数
     *
     * @param userId 用户ID
     */
    void incrementDonateCount(Long userId);

    /**
     * 增加借用次数
     *
     * @param userId 用户ID
     */
    void incrementBorrowCount(Long userId);

    /**
     * 增加按时归还次数
     *
     * @param userId 用户ID
     */
    void incrementReturnOnTimeCount(Long userId);

    /**
     * 增加逾期次数
     *
     * @param userId 用户ID
     */
    void incrementOverdueCount(Long userId);

    /**
     * 增加损坏次数
     *
     * @param userId 用户ID
     */
    void incrementDamageCount(Long userId);

    /**
     * 增加违规次数
     *
     * @param userId 用户ID
     */
    void incrementViolationCount(Long userId);

    /**
     * 检查用户信用分是否足够
     *
     * @param userId 用户ID
     * @param minScore 最低信用分要求
     * @return 是否满足
     */
    boolean checkCreditScore(Long userId, Integer minScore);

}


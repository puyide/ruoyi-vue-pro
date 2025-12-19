package cn.iocoder.yudao.module.member.service.credit;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.member.controller.admin.credit.vo.CreditLogPageReqVO;
import cn.iocoder.yudao.module.member.dal.dataobject.credit.MemberCreditLogDO;
import cn.iocoder.yudao.module.member.dal.dataobject.credit.MemberCreditScoreDO;
import cn.iocoder.yudao.module.member.dal.mysql.credit.MemberCreditLogMapper;
import cn.iocoder.yudao.module.member.dal.mysql.credit.MemberCreditScoreMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 会员信用分 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
@Slf4j
public class MemberCreditServiceImpl implements MemberCreditService {

    /**
     * 默认初始信用分
     */
    private static final Integer DEFAULT_CREDIT_SCORE = 100;

    /**
     * 最低信用分
     */
    private static final Integer MIN_CREDIT_SCORE = 0;

    /**
     * 最高信用分
     */
    private static final Integer MAX_CREDIT_SCORE = 150;

    @Resource
    private MemberCreditScoreMapper creditScoreMapper;

    @Resource
    private MemberCreditLogMapper creditLogMapper;

    @Override
    public MemberCreditScoreDO getCreditScore(Long userId) {
        return creditScoreMapper.selectByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MemberCreditScoreDO getOrInitCreditScore(Long userId) {
        MemberCreditScoreDO creditScore = creditScoreMapper.selectByUserId(userId);
        if (creditScore == null) {
            creditScore = initCreditScore(userId);
        }
        return creditScore;
    }

    /**
     * 初始化用户信用分
     */
    private MemberCreditScoreDO initCreditScore(Long userId) {
        MemberCreditScoreDO creditScore = MemberCreditScoreDO.builder()
                .userId(userId)
                .totalScore(DEFAULT_CREDIT_SCORE)
                .donateCount(0)
                .borrowCount(0)
                .returnOnTimeCount(0)
                .overdueCount(0)
                .damageCount(0)
                .violationCount(0)
                .scoreChangeReason("初始化信用分")
                .lastScoreChangeDate(LocalDateTime.now())
                .build();
        creditScoreMapper.insert(creditScore);
        log.info("[initCreditScore][用户({})初始化信用分成功，初始分数:{}]", userId, DEFAULT_CREDIT_SCORE);
        return creditScore;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addCreditScore(Long userId, Integer changeType, Integer score, String reason, Long relatedOrderId) {
        if (score == null || score <= 0) {
            return;
        }
        
        // 1. 获取或初始化信用分
        MemberCreditScoreDO creditScore = getOrInitCreditScore(userId);
        Integer beforeScore = creditScore.getTotalScore();
        
        // 2. 计算新分数（不超过最高分）
        Integer afterScore = Math.min(beforeScore + score, MAX_CREDIT_SCORE);
        Integer actualChange = afterScore - beforeScore;
        
        // 3. 更新信用分
        creditScore.setTotalScore(afterScore);
        creditScore.setScoreChangeReason(reason);
        creditScore.setLastScoreChangeDate(LocalDateTime.now());
        creditScoreMapper.updateById(creditScore);
        
        // 4. 记录变动日志
        createCreditLog(userId, changeType, actualChange, beforeScore, afterScore, reason, relatedOrderId);
        
        log.info("[addCreditScore][用户({})增加信用分:{}，原分数:{}，现分数:{}，原因:{}]",
                userId, actualChange, beforeScore, afterScore, reason);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reduceCreditScore(Long userId, Integer changeType, Integer score, String reason, Long relatedOrderId) {
        if (score == null || score <= 0) {
            return;
        }
        
        // 1. 获取或初始化信用分
        MemberCreditScoreDO creditScore = getOrInitCreditScore(userId);
        Integer beforeScore = creditScore.getTotalScore();
        
        // 2. 计算新分数（不低于最低分）
        Integer afterScore = Math.max(beforeScore - score, MIN_CREDIT_SCORE);
        Integer actualChange = afterScore - beforeScore; // 负数
        
        // 3. 更新信用分
        creditScore.setTotalScore(afterScore);
        creditScore.setScoreChangeReason(reason);
        creditScore.setLastScoreChangeDate(LocalDateTime.now());
        creditScoreMapper.updateById(creditScore);
        
        // 4. 记录变动日志
        createCreditLog(userId, changeType, actualChange, beforeScore, afterScore, reason, relatedOrderId);
        
        log.info("[reduceCreditScore][用户({})扣减信用分:{}，原分数:{}，现分数:{}，原因:{}]",
                userId, actualChange, beforeScore, afterScore, reason);
    }

    /**
     * 创建信用分变动日志
     */
    private void createCreditLog(Long userId, Integer changeType, Integer changeScore,
                                  Integer beforeScore, Integer afterScore, String reason, Long relatedOrderId) {
        MemberCreditLogDO creditLog = MemberCreditLogDO.builder()
                .userId(userId)
                .changeType(changeType)
                .changeScore(changeScore)
                .beforeScore(beforeScore)
                .afterScore(afterScore)
                .reason(reason)
                .relatedOrderId(relatedOrderId)
                .build();
        creditLogMapper.insert(creditLog);
    }

    @Override
    public List<MemberCreditLogDO> getCreditLogList(Long userId, Integer limit) {
        return creditLogMapper.selectListByUserId(userId, limit);
    }

    @Override
    public PageResult<MemberCreditLogDO> getCreditLogPage(CreditLogPageReqVO pageReqVO) {
        return creditLogMapper.selectPage(pageReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void incrementDonateCount(Long userId) {
        MemberCreditScoreDO creditScore = getOrInitCreditScore(userId);
        creditScore.setDonateCount(creditScore.getDonateCount() + 1);
        creditScoreMapper.updateById(creditScore);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void incrementBorrowCount(Long userId) {
        MemberCreditScoreDO creditScore = getOrInitCreditScore(userId);
        creditScore.setBorrowCount(creditScore.getBorrowCount() + 1);
        creditScoreMapper.updateById(creditScore);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void incrementReturnOnTimeCount(Long userId) {
        MemberCreditScoreDO creditScore = getOrInitCreditScore(userId);
        creditScore.setReturnOnTimeCount(creditScore.getReturnOnTimeCount() + 1);
        creditScoreMapper.updateById(creditScore);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void incrementOverdueCount(Long userId) {
        MemberCreditScoreDO creditScore = getOrInitCreditScore(userId);
        creditScore.setOverdueCount(creditScore.getOverdueCount() + 1);
        creditScoreMapper.updateById(creditScore);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void incrementDamageCount(Long userId) {
        MemberCreditScoreDO creditScore = getOrInitCreditScore(userId);
        creditScore.setDamageCount(creditScore.getDamageCount() + 1);
        creditScoreMapper.updateById(creditScore);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void incrementViolationCount(Long userId) {
        MemberCreditScoreDO creditScore = getOrInitCreditScore(userId);
        creditScore.setViolationCount(creditScore.getViolationCount() + 1);
        creditScoreMapper.updateById(creditScore);
    }

    @Override
    public boolean checkCreditScore(Long userId, Integer minScore) {
        MemberCreditScoreDO creditScore = getCreditScore(userId);
        if (creditScore == null) {
            // 未初始化则使用默认分数判断
            return DEFAULT_CREDIT_SCORE >= minScore;
        }
        return creditScore.getTotalScore() >= minScore;
    }

}


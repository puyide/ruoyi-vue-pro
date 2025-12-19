package cn.iocoder.yudao.module.member.service.training;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.member.controller.app.training.vo.AppTrainingLogCreateReqVO;
import cn.iocoder.yudao.module.member.controller.app.training.vo.AppTrainingLogPageReqVO;
import cn.iocoder.yudao.module.member.dal.dataobject.training.TrainingLogDO;
import cn.iocoder.yudao.module.member.dal.dataobject.training.TrainingSessionDO;
import cn.iocoder.yudao.module.member.dal.dataobject.training.TrainingTemplateDO;
import cn.iocoder.yudao.module.member.dal.mysql.training.TrainingLogMapper;
import cn.iocoder.yudao.module.member.enums.training.TrainingSessionStatusEnum;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.member.enums.ErrorCodeConstants.*;

/**
 * 训练日志 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class TrainingLogServiceImpl implements TrainingLogService {

    @Resource
    private TrainingLogMapper trainingLogMapper;

    @Resource
    private TrainingSessionService trainingSessionService;

    @Resource
    private TrainingTemplateService trainingTemplateService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createLog(Long userId, AppTrainingLogCreateReqVO createReqVO) {
        // 1. 校验会话存在
        TrainingSessionDO session = trainingSessionService.getSession(userId, createReqVO.getSessionId());
        if (session == null) {
            throw exception(TRAINING_SESSION_NOT_EXISTS);
        }

        // 2. 校验是否已经打过卡
        TrainingLogDO existingLog = trainingLogMapper.selectBySessionId(createReqVO.getSessionId());
        if (existingLog != null) {
            throw exception(TRAINING_LOG_ALREADY_EXISTS);
        }

        // 3. 获取模板信息
        TrainingTemplateDO template = trainingTemplateService.getTemplate(session.getTemplateId());

        // 4. 创建日志
        TrainingLogDO log = TrainingLogDO.builder()
                .sessionId(createReqVO.getSessionId())
                .childId(session.getChildId())
                .userId(userId)
                .templateId(session.getTemplateId())
                .domain(template != null ? template.getDomain() : null)
                .doneAt(LocalDateTime.now())
                .completed(createReqVO.getCompleted())
                .childMood(createReqVO.getChildMood())
                .successCount(createReqVO.getSuccessCount())
                .difficulties(createReqVO.getDifficulties() != null ? 
                        JSONUtil.toJsonStr(createReqVO.getDifficulties()) : null)
                .parentComment(createReqVO.getParentComment())
                .structuredData(createReqVO.getStructuredData() != null ?
                        JSONUtil.toJsonStr(createReqVO.getStructuredData()) : null)
                .sharedToCommunity(createReqVO.getSharedToCommunity() != null ? 
                        createReqVO.getSharedToCommunity() : false)
                .build();

        trainingLogMapper.insert(log);

        // 5. 更新会话状态为已完成
        trainingSessionService.completeSession(userId, createReqVO.getSessionId(), 
                createReqVO.getActualDurationMinutes());

        return log.getId();
    }

    @Override
    public TrainingLogDO getLog(Long userId, Long id) {
        TrainingLogDO log = trainingLogMapper.selectById(id);
        if (log == null || !log.getUserId().equals(userId)) {
            return null;
        }
        return log;
    }

    @Override
    public TrainingLogDO getLogBySessionId(Long sessionId) {
        return trainingLogMapper.selectBySessionId(sessionId);
    }

    @Override
    public PageResult<TrainingLogDO> getLogPage(Long userId, AppTrainingLogPageReqVO pageReqVO) {
        return trainingLogMapper.selectPage(userId, pageReqVO.getChildId(), pageReqVO.getDomain(),
                pageReqVO.getPageNo(), pageReqVO.getPageSize());
    }

    @Override
    public List<TrainingLogDO> getRecentLogs(Long userId, Long childId, Long templateId, int limit) {
        return trainingLogMapper.selectRecentLogs(userId, childId, templateId, limit);
    }

    @Override
    public TrainingStatistics getStatistics(Long userId, Long childId) {
        // 总训练次数
        Long totalSessions = trainingLogMapper.countTotal(userId, childId);

        // 训练天数
        Integer trainingDays = trainingLogMapper.countTrainingDays(userId, childId);

        // TODO: 计算连续训练天数（需要更复杂的查询）
        int consecutiveDays = trainingDays != null ? Math.min(trainingDays, 7) : 0;

        // 平均成功率（简化计算）
        double avgSuccessRate = 0.75; // 默认值，实际应根据日志计算

        return new TrainingStatistics(
                totalSessions != null ? totalSessions.intValue() : 0,
                trainingDays != null ? trainingDays : 0,
                consecutiveDays,
                avgSuccessRate
        );
    }

    @Override
    public AiFeedback generateAiFeedback(Long userId, Long logId) {
        TrainingLogDO log = getLog(userId, logId);
        if (log == null) {
            throw exception(TRAINING_LOG_NOT_EXISTS);
        }

        // 获取最近几次的训练日志
        List<TrainingLogDO> recentLogs = getRecentLogs(userId, log.getChildId(), log.getTemplateId(), 5);

        // 获取模板信息
        TrainingTemplateDO template = trainingTemplateService.getTemplate(log.getTemplateId());

        // 生成反馈（这里是简化版本，实际应调用 AI）
        String affirmation = generateAffirmation(log, recentLogs, template);
        String summary = generateSummary(log, recentLogs, template);
        String nextSuggestion = generateNextSuggestion(log, recentLogs);
        Integer suggestedLevel = calculateSuggestedLevel(log, recentLogs);

        // 保存 AI 反馈到日志
        Map<String, Object> feedback = new HashMap<>();
        feedback.put("affirmation", affirmation);
        feedback.put("summary", summary);
        feedback.put("next_suggestion", nextSuggestion);
        feedback.put("suggested_level", suggestedLevel);

        TrainingLogDO updateObj = new TrainingLogDO();
        updateObj.setId(logId);
        updateObj.setAiFeedback(JSONUtil.toJsonStr(feedback));
        trainingLogMapper.updateById(updateObj);

        return new AiFeedback(affirmation, summary, nextSuggestion, suggestedLevel);
    }

    /**
     * 生成肯定语
     */
    private String generateAffirmation(TrainingLogDO log, List<TrainingLogDO> recentLogs, TrainingTemplateDO template) {
        int totalCount = recentLogs.size();
        String templateName = template != null ? template.getName() : "这项训练";
        
        if (totalCount <= 1) {
            return String.format("太棒了！你开始了「%s」的第一次练习，这是非常重要的一步。", templateName);
        } else {
            return String.format("你已经为孩子练了 %d 次「%s」，这对将来的发展非常重要。", totalCount, templateName);
        }
    }

    /**
     * 生成总结
     */
    private String generateSummary(TrainingLogDO log, List<TrainingLogDO> recentLogs, TrainingTemplateDO template) {
        if (recentLogs.size() < 2) {
            return "继续坚持，我们会帮你记录孩子的每一点进步。";
        }

        // 计算成功次数趋势
        List<Integer> successCounts = recentLogs.stream()
                .map(l -> l.getSuccessCount() != null ? l.getSuccessCount() : 0)
                .toList();

        if (successCounts.size() >= 2) {
            int latest = successCounts.get(0);
            int previous = successCounts.get(1);
            if (latest > previous) {
                return String.format("从最近几次记录看，孩子的成功次数有所增加（%d → %d 次），继续保持！", previous, latest);
            } else if (latest < previous) {
                return "这次的成功次数比上次少了一点，没关系，每个孩子都有状态起伏。";
            }
        }

        return "从记录来看，孩子在稳步进步中，继续加油！";
    }

    /**
     * 生成下一步建议
     */
    private String generateNextSuggestion(TrainingLogDO log, List<TrainingLogDO> recentLogs) {
        // 根据完成情况和情绪给建议
        if (log.getCompleted() != null && !log.getCompleted()) {
            return "下次可以把时间缩短一点，只练 2-3 分钟，先把「成功体验」堆起来。";
        }

        if (log.getChildMood() != null && log.getChildMood() == 3) { // 情绪不好
            return "孩子今天情绪不太好，下次可以选一个他更喜欢的活动时间来练习。";
        }

        // 计算平均成功次数
        double avgSuccess = recentLogs.stream()
                .mapToInt(l -> l.getSuccessCount() != null ? l.getSuccessCount() : 0)
                .average()
                .orElse(0);

        if (avgSuccess >= 7) {
            return "表现很棒！下次可以尝试把距离拉远一点，或者换一个房间练习。";
        } else if (avgSuccess >= 4) {
            return "保持当前的练习方式，孩子正在慢慢进步。";
        } else {
            return "可以试着增加一些孩子喜欢的奖励，让练习更有吸引力。";
        }
    }

    /**
     * 计算建议的难度等级
     */
    private Integer calculateSuggestedLevel(TrainingLogDO log, List<TrainingLogDO> recentLogs) {
        if (recentLogs.size() < 3) {
            return 1; // 数据不足，保持 L1
        }

        // 计算最近几次的平均成功次数和完成率
        double avgSuccess = recentLogs.stream()
                .mapToInt(l -> l.getSuccessCount() != null ? l.getSuccessCount() : 0)
                .average()
                .orElse(0);

        long completedCount = recentLogs.stream()
                .filter(l -> l.getCompleted() != null && l.getCompleted())
                .count();

        double completionRate = (double) completedCount / recentLogs.size();

        // 根据数据建议等级
        if (avgSuccess >= 8 && completionRate >= 0.9) {
            return 3; // 可以尝试 L3
        } else if (avgSuccess >= 5 && completionRate >= 0.7) {
            return 2; // 可以尝试 L2
        } else {
            return 1; // 保持 L1
        }
    }

}


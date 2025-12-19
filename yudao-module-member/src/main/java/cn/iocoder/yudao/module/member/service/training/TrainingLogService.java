package cn.iocoder.yudao.module.member.service.training;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.member.controller.app.training.vo.AppTrainingLogCreateReqVO;
import cn.iocoder.yudao.module.member.controller.app.training.vo.AppTrainingLogPageReqVO;
import cn.iocoder.yudao.module.member.dal.dataobject.training.TrainingLogDO;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 训练日志 Service 接口
 *
 * @author 芋道源码
 */
public interface TrainingLogService {

    /**
     * 创建训练日志（打卡）
     *
     * @param userId 用户ID
     * @param createReqVO 创建信息
     * @return 日志ID
     */
    Long createLog(Long userId, @Valid AppTrainingLogCreateReqVO createReqVO);

    /**
     * 获得训练日志
     *
     * @param userId 用户ID
     * @param id 编号
     * @return 训练日志
     */
    TrainingLogDO getLog(Long userId, Long id);

    /**
     * 根据会话ID获得日志
     *
     * @param sessionId 会话ID
     * @return 训练日志
     */
    TrainingLogDO getLogBySessionId(Long sessionId);

    /**
     * 分页查询训练日志
     *
     * @param userId 用户ID
     * @param pageReqVO 分页请求
     * @return 分页结果
     */
    PageResult<TrainingLogDO> getLogPage(Long userId, @Valid AppTrainingLogPageReqVO pageReqVO);

    /**
     * 获取最近的训练日志
     *
     * @param userId 用户ID
     * @param childId 孩子ID
     * @param templateId 模板ID
     * @param limit 数量限制
     * @return 日志列表
     */
    List<TrainingLogDO> getRecentLogs(Long userId, Long childId, Long templateId, int limit);

    /**
     * 获取训练统计信息
     *
     * @param userId 用户ID
     * @param childId 孩子ID
     * @return 统计信息
     */
    TrainingStatistics getStatistics(Long userId, Long childId);

    /**
     * 生成 AI 反馈
     *
     * @param userId 用户ID
     * @param logId 日志ID
     * @return AI 反馈内容
     */
    AiFeedback generateAiFeedback(Long userId, Long logId);

    /**
     * 训练统计信息
     */
    record TrainingStatistics(
            int totalSessions,
            int trainingDays,
            int consecutiveDays,
            double avgSuccessRate
    ) {}

    /**
     * AI 反馈
     */
    record AiFeedback(
            String affirmation,
            String summary,
            String nextSuggestion,
            Integer suggestedLevel
    ) {}

}


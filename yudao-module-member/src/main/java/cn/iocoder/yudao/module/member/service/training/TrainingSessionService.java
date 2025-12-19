package cn.iocoder.yudao.module.member.service.training;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.member.controller.app.training.vo.AppTrainingSessionCreateReqVO;
import cn.iocoder.yudao.module.member.controller.app.training.vo.AppTrainingSessionPageReqVO;
import cn.iocoder.yudao.module.member.dal.dataobject.training.TrainingSessionDO;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 训练会话 Service 接口
 *
 * @author 芋道源码
 */
public interface TrainingSessionService {

    /**
     * 创建训练会话（生成训练卡片）
     *
     * @param userId 用户ID
     * @param createReqVO 创建信息
     * @return 会话ID
     */
    Long createSession(Long userId, @Valid AppTrainingSessionCreateReqVO createReqVO);

    /**
     * 获得训练会话
     *
     * @param userId 用户ID
     * @param id 编号
     * @return 训练会话
     */
    TrainingSessionDO getSession(Long userId, Long id);

    /**
     * 获得今日训练列表
     *
     * @param userId 用户ID
     * @param childId 孩子ID
     * @return 训练会话列表
     */
    List<TrainingSessionDO> getTodaySessionList(Long userId, Long childId);

    /**
     * 分页查询训练会话
     *
     * @param userId 用户ID
     * @param pageReqVO 分页请求
     * @return 分页结果
     */
    PageResult<TrainingSessionDO> getSessionPage(Long userId, @Valid AppTrainingSessionPageReqVO pageReqVO);

    /**
     * 开始训练会话
     *
     * @param userId 用户ID
     * @param id 会话ID
     */
    void startSession(Long userId, Long id);

    /**
     * 完成训练会话
     *
     * @param userId 用户ID
     * @param id 会话ID
     * @param actualDurationMinutes 实际时长（分钟）
     */
    void completeSession(Long userId, Long id, Integer actualDurationMinutes);

    /**
     * 跳过训练会话
     *
     * @param userId 用户ID
     * @param id 会话ID
     */
    void skipSession(Long userId, Long id);

    /**
     * 获取今日训练进度统计
     *
     * @param userId 用户ID
     * @param childId 孩子ID
     * @return 进度统计
     */
    TodayProgress getTodayProgress(Long userId, Long childId);

    /**
     * 今日训练进度
     */
    record TodayProgress(int total, int completed, int totalMinutes) {}

}


package cn.iocoder.yudao.module.member.dal.mysql.training;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.member.dal.dataobject.training.TrainingSessionDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 训练会话 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface TrainingSessionMapper extends BaseMapperX<TrainingSessionDO> {

    /**
     * 根据ID和用户ID查询
     *
     * @param id 会话ID
     * @param userId 用户ID
     * @return 训练会话
     */
    default TrainingSessionDO selectByIdAndUserId(Long id, Long userId) {
        return selectOne(TrainingSessionDO::getId, id, TrainingSessionDO::getUserId, userId);
    }

    /**
     * 根据孩子ID查询今日会话列表
     *
     * @param childId 孩子ID
     * @param userId 用户ID
     * @param todayStart 今日开始时间
     * @param todayEnd 今日结束时间
     * @return 训练会话列表
     */
    default List<TrainingSessionDO> selectTodayListByChildId(Long childId, Long userId, 
                                                              LocalDateTime todayStart, LocalDateTime todayEnd) {
        return selectList(new LambdaQueryWrapperX<TrainingSessionDO>()
                .eq(TrainingSessionDO::getChildId, childId)
                .eq(TrainingSessionDO::getUserId, userId)
                .between(TrainingSessionDO::getScheduledAt, todayStart, todayEnd)
                .orderByAsc(TrainingSessionDO::getScheduledAt));
    }

    /**
     * 查询用户的会话列表
     *
     * @param userId 用户ID
     * @param childId 孩子ID（可选）
     * @param status 状态（可选）
     * @return 训练会话列表
     */
    default List<TrainingSessionDO> selectListByUserId(Long userId, Long childId, Integer status) {
        return selectList(new LambdaQueryWrapperX<TrainingSessionDO>()
                .eq(TrainingSessionDO::getUserId, userId)
                .eqIfPresent(TrainingSessionDO::getChildId, childId)
                .eqIfPresent(TrainingSessionDO::getStatus, status)
                .orderByDesc(TrainingSessionDO::getScheduledAt));
    }

    /**
     * 分页查询会话
     *
     * @param userId 用户ID
     * @param childId 孩子ID（可选）
     * @param status 状态（可选）
     * @param pageNo 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    default PageResult<TrainingSessionDO> selectPage(Long userId, Long childId, Integer status, 
                                                      Integer pageNo, Integer pageSize) {
        PageParam pageParam = new PageParam();
        pageParam.setPageNo(pageNo);
        pageParam.setPageSize(pageSize);
        return selectPage(pageParam,
                new LambdaQueryWrapperX<TrainingSessionDO>()
                        .eq(TrainingSessionDO::getUserId, userId)
                        .eqIfPresent(TrainingSessionDO::getChildId, childId)
                        .eqIfPresent(TrainingSessionDO::getStatus, status)
                        .orderByDesc(TrainingSessionDO::getScheduledAt));
    }

    /**
     * 统计用户今日完成的会话数量
     *
     * @param userId 用户ID
     * @param childId 孩子ID
     * @param todayStart 今日开始时间
     * @param todayEnd 今日结束时间
     * @return 完成数量
     */
    default Long countTodayCompleted(Long userId, Long childId, LocalDateTime todayStart, LocalDateTime todayEnd) {
        return selectCount(new LambdaQueryWrapperX<TrainingSessionDO>()
                .eq(TrainingSessionDO::getUserId, userId)
                .eqIfPresent(TrainingSessionDO::getChildId, childId)
                .eq(TrainingSessionDO::getStatus, 2) // DONE
                .between(TrainingSessionDO::getCompletedAt, todayStart, todayEnd));
    }

    /**
     * 查询最近的会话列表（用于 AI 反馈）
     *
     * @param userId 用户ID
     * @param childId 孩子ID
     * @param templateId 模板ID
     * @param limit 数量限制
     * @return 训练会话列表
     */
    default List<TrainingSessionDO> selectRecentSessions(Long userId, Long childId, Long templateId, int limit) {
        return selectList(new LambdaQueryWrapperX<TrainingSessionDO>()
                .eq(TrainingSessionDO::getUserId, userId)
                .eq(TrainingSessionDO::getChildId, childId)
                .eq(TrainingSessionDO::getTemplateId, templateId)
                .eq(TrainingSessionDO::getStatus, 2) // DONE
                .orderByDesc(TrainingSessionDO::getCompletedAt)
                .last("LIMIT " + limit));
    }

}


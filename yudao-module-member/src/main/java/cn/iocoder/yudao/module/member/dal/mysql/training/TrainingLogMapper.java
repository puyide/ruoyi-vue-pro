package cn.iocoder.yudao.module.member.dal.mysql.training;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.member.dal.dataobject.training.TrainingLogDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 训练日志 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface TrainingLogMapper extends BaseMapperX<TrainingLogDO> {

    /**
     * 根据会话ID查询日志
     *
     * @param sessionId 会话ID
     * @return 训练日志
     */
    default TrainingLogDO selectBySessionId(Long sessionId) {
        return selectOne(TrainingLogDO::getSessionId, sessionId);
    }

    /**
     * 根据用户ID查询日志列表
     *
     * @param userId 用户ID
     * @param childId 孩子ID（可选）
     * @param domain 训练域（可选）
     * @return 训练日志列表
     */
    default List<TrainingLogDO> selectListByUserId(Long userId, Long childId, String domain) {
        return selectList(new LambdaQueryWrapperX<TrainingLogDO>()
                .eq(TrainingLogDO::getUserId, userId)
                .eqIfPresent(TrainingLogDO::getChildId, childId)
                .eqIfPresent(TrainingLogDO::getDomain, domain)
                .orderByDesc(TrainingLogDO::getDoneAt));
    }

    /**
     * 分页查询日志
     *
     * @param userId 用户ID
     * @param childId 孩子ID（可选）
     * @param domain 训练域（可选）
     * @param pageNo 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    default PageResult<TrainingLogDO> selectPage(Long userId, Long childId, String domain, 
                                                  Integer pageNo, Integer pageSize) {
        PageParam pageParam = new PageParam();
        pageParam.setPageNo(pageNo);
        pageParam.setPageSize(pageSize);
        return selectPage(pageParam,
                new LambdaQueryWrapperX<TrainingLogDO>()
                        .eq(TrainingLogDO::getUserId, userId)
                        .eqIfPresent(TrainingLogDO::getChildId, childId)
                        .eqIfPresent(TrainingLogDO::getDomain, domain)
                        .orderByDesc(TrainingLogDO::getDoneAt));
    }

    /**
     * 查询最近的日志（用于 AI 分析）
     *
     * @param userId 用户ID
     * @param childId 孩子ID
     * @param templateId 模板ID
     * @param limit 数量限制
     * @return 训练日志列表
     */
    default List<TrainingLogDO> selectRecentLogs(Long userId, Long childId, Long templateId, int limit) {
        return selectList(new LambdaQueryWrapperX<TrainingLogDO>()
                .eq(TrainingLogDO::getUserId, userId)
                .eq(TrainingLogDO::getChildId, childId)
                .eq(TrainingLogDO::getTemplateId, templateId)
                .orderByDesc(TrainingLogDO::getDoneAt)
                .last("LIMIT " + limit));
    }

    /**
     * 统计某模板的完成次数
     *
     * @param userId 用户ID
     * @param childId 孩子ID
     * @param templateId 模板ID
     * @return 完成次数
     */
    default Long countByTemplate(Long userId, Long childId, Long templateId) {
        return selectCount(new LambdaQueryWrapperX<TrainingLogDO>()
                .eq(TrainingLogDO::getUserId, userId)
                .eq(TrainingLogDO::getChildId, childId)
                .eq(TrainingLogDO::getTemplateId, templateId)
                .eq(TrainingLogDO::getCompleted, true));
    }

    /**
     * 统计连续训练天数
     *
     * @param userId 用户ID
     * @param childId 孩子ID
     * @return 连续天数
     */
    @Select("SELECT COUNT(DISTINCT DATE(done_at)) FROM training_log " +
            "WHERE user_id = #{userId} AND child_id = #{childId} AND deleted = false " +
            "AND done_at >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)")
    Integer countTrainingDays(Long userId, Long childId);

    /**
     * 统计总训练次数
     *
     * @param userId 用户ID
     * @param childId 孩子ID
     * @return 总次数
     */
    default Long countTotal(Long userId, Long childId) {
        return selectCount(new LambdaQueryWrapperX<TrainingLogDO>()
                .eq(TrainingLogDO::getUserId, userId)
                .eqIfPresent(TrainingLogDO::getChildId, childId));
    }

    /**
     * 计算某模板的平均成功次数
     *
     * @param userId 用户ID
     * @param childId 孩子ID
     * @param templateId 模板ID
     * @return 平均成功次数
     */
    @Select("SELECT AVG(success_count) FROM training_log " +
            "WHERE user_id = #{userId} AND child_id = #{childId} AND template_id = #{templateId} " +
            "AND deleted = false AND success_count IS NOT NULL")
    Double avgSuccessCount(Long userId, Long childId, Long templateId);

}


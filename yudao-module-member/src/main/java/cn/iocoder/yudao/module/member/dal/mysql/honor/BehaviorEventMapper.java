package cn.iocoder.yudao.module.member.dal.mysql.honor;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.member.dal.dataobject.honor.BehaviorEventDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 行为事件 Mapper
 *
 * @author 星语家园
 */
@Mapper
public interface BehaviorEventMapper extends BaseMapperX<BehaviorEventDO> {

    /**
     * 查询用户某类型事件是否存在
     */
    default boolean existsByUserIdAndEventType(Long userId, String eventType) {
        return selectCount(new LambdaQueryWrapperX<BehaviorEventDO>()
                .eq(BehaviorEventDO::getUserId, userId)
                .eq(BehaviorEventDO::getEventType, eventType)) > 0;
    }

    /**
     * 统计用户某类型事件次数
     */
    default Long selectCountByUserIdAndEventType(Long userId, String eventType) {
        return selectCount(new LambdaQueryWrapperX<BehaviorEventDO>()
                .eq(BehaviorEventDO::getUserId, userId)
                .eq(BehaviorEventDO::getEventType, eventType));
    }

    /**
     * 查询用户最近N天的事件
     */
    default List<BehaviorEventDO> selectRecentByUserId(Long userId, int days) {
        LocalDateTime startTime = LocalDateTime.now().minusDays(days);
        return selectList(new LambdaQueryWrapperX<BehaviorEventDO>()
                .eq(BehaviorEventDO::getUserId, userId)
                .ge(BehaviorEventDO::getCreateTime, startTime)
                .orderByDesc(BehaviorEventDO::getCreateTime));
    }

    /**
     * 查询用户某类型最新事件
     */
    default BehaviorEventDO selectLatestByUserIdAndEventType(Long userId, String eventType) {
        return selectOne(new LambdaQueryWrapperX<BehaviorEventDO>()
                .eq(BehaviorEventDO::getUserId, userId)
                .eq(BehaviorEventDO::getEventType, eventType)
                .orderByDesc(BehaviorEventDO::getCreateTime)
                .last("LIMIT 1"));
    }
}


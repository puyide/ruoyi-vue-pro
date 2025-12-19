package cn.iocoder.yudao.module.trade.dal.mysql.handover;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.trade.dal.dataobject.handover.EquipmentFeedbackDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 器材交接反馈/评价 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface EquipmentFeedbackMapper extends BaseMapperX<EquipmentFeedbackDO> {

    /**
     * 根据交接会话ID查询反馈列表
     */
    default List<EquipmentFeedbackDO> selectListByHandoverId(Long handoverId) {
        return selectList(new LambdaQueryWrapperX<EquipmentFeedbackDO>()
                .eq(EquipmentFeedbackDO::getHandoverId, handoverId)
                .orderByDesc(EquipmentFeedbackDO::getId));
    }

    /**
     * 根据交接会话ID和反馈者ID查询
     */
    default EquipmentFeedbackDO selectByHandoverIdAndFromUserId(Long handoverId, Long fromUserId) {
        return selectOne(new LambdaQueryWrapperX<EquipmentFeedbackDO>()
                .eq(EquipmentFeedbackDO::getHandoverId, handoverId)
                .eq(EquipmentFeedbackDO::getFromUserId, fromUserId));
    }

    /**
     * 查询用户收到的评价列表
     */
    default List<EquipmentFeedbackDO> selectListByToUserId(Long toUserId) {
        return selectList(new LambdaQueryWrapperX<EquipmentFeedbackDO>()
                .eq(EquipmentFeedbackDO::getToUserId, toUserId)
                .orderByDesc(EquipmentFeedbackDO::getId));
    }

    /**
     * 计算用户的平均评分
     */
    default Double selectAvgRatingByToUserId(Long toUserId) {
        List<EquipmentFeedbackDO> list = selectList(new LambdaQueryWrapperX<EquipmentFeedbackDO>()
                .eq(EquipmentFeedbackDO::getToUserId, toUserId)
                .isNotNull(EquipmentFeedbackDO::getRating));
        if (list.isEmpty()) {
            return null;
        }
        return list.stream()
                .mapToInt(EquipmentFeedbackDO::getRating)
                .average()
                .orElse(0.0);
    }

}


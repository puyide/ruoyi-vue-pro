package cn.iocoder.yudao.module.trade.dal.mysql.handover;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.trade.dal.dataobject.handover.EquipmentHandoverMessageDO;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 交接会话消息 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface EquipmentHandoverMessageMapper extends BaseMapperX<EquipmentHandoverMessageDO> {

    /**
     * 查询交接会话的消息列表
     */
    default List<EquipmentHandoverMessageDO> selectListByHandoverId(Long handoverId) {
        return selectList(new LambdaQueryWrapperX<EquipmentHandoverMessageDO>()
                .eq(EquipmentHandoverMessageDO::getHandoverId, handoverId)
                .orderByAsc(EquipmentHandoverMessageDO::getId));
    }

    /**
     * 查询交接会话的消息列表（分页）
     */
    default PageResult<EquipmentHandoverMessageDO> selectPageByHandoverId(Long handoverId, 
            cn.iocoder.yudao.framework.common.pojo.PageParam pageParam) {
        return selectPage(pageParam, new LambdaQueryWrapperX<EquipmentHandoverMessageDO>()
                .eq(EquipmentHandoverMessageDO::getHandoverId, handoverId)
                .orderByDesc(EquipmentHandoverMessageDO::getId));
    }

    /**
     * 查询交接会话的最新消息
     */
    default EquipmentHandoverMessageDO selectLatestByHandoverId(Long handoverId) {
        return selectOne(new LambdaQueryWrapperX<EquipmentHandoverMessageDO>()
                .eq(EquipmentHandoverMessageDO::getHandoverId, handoverId)
                .orderByDesc(EquipmentHandoverMessageDO::getId)
                .last("LIMIT 1"));
    }

    /**
     * 标记消息为已读
     */
    default int markAsRead(Long handoverId, Long userId) {
        return update(new EquipmentHandoverMessageDO().setIsRead(true),
                new LambdaUpdateWrapper<EquipmentHandoverMessageDO>()
                        .eq(EquipmentHandoverMessageDO::getHandoverId, handoverId)
                        .ne(EquipmentHandoverMessageDO::getSenderUserId, userId)
                        .eq(EquipmentHandoverMessageDO::getIsRead, false));
    }

    /**
     * 统计未读消息数
     */
    default Long countUnread(Long handoverId, Long userId) {
        return selectCount(new LambdaQueryWrapperX<EquipmentHandoverMessageDO>()
                .eq(EquipmentHandoverMessageDO::getHandoverId, handoverId)
                .ne(EquipmentHandoverMessageDO::getSenderUserId, userId)
                .eq(EquipmentHandoverMessageDO::getIsRead, false));
    }

}


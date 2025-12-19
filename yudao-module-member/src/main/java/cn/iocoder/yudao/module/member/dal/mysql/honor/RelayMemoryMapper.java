package cn.iocoder.yudao.module.member.dal.mysql.honor;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.member.dal.dataobject.honor.RelayMemoryDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 接力记忆 Mapper
 *
 * @author 星语家园
 */
@Mapper
public interface RelayMemoryMapper extends BaseMapperX<RelayMemoryDO> {

    /**
     * 查询用户传递出去的物品记忆
     */
    default List<RelayMemoryDO> selectByFromUserId(Long userId) {
        return selectList(new LambdaQueryWrapperX<RelayMemoryDO>()
                .eq(RelayMemoryDO::getFromUserId, userId)
                .orderByDesc(RelayMemoryDO::getCreateTime));
    }

    /**
     * 查询用户接收的物品记忆
     */
    default List<RelayMemoryDO> selectByToUserId(Long userId) {
        return selectList(new LambdaQueryWrapperX<RelayMemoryDO>()
                .eq(RelayMemoryDO::getToUserId, userId)
                .orderByDesc(RelayMemoryDO::getCreateTime));
    }

    /**
     * 查询物品的接力链
     */
    default List<RelayMemoryDO> selectByItemId(Long itemId) {
        return selectList(new LambdaQueryWrapperX<RelayMemoryDO>()
                .eq(RelayMemoryDO::getItemId, itemId)
                .orderByAsc(RelayMemoryDO::getSequenceNum));
    }

    /**
     * 查询原始物品的所有接力记忆
     */
    default List<RelayMemoryDO> selectByOriginalItemId(Long originalItemId) {
        return selectList(new LambdaQueryWrapperX<RelayMemoryDO>()
                .eq(RelayMemoryDO::getOriginalItemId, originalItemId)
                .orderByAsc(RelayMemoryDO::getSequenceNum));
    }

    /**
     * 统计用户传递物品数量
     */
    default Long selectCountByFromUserId(Long userId) {
        return selectCount(RelayMemoryDO::getFromUserId, userId);
    }
}


package cn.iocoder.yudao.module.product.dal.mysql.relay;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.product.dal.dataobject.relay.RelayTransferDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 接力交接记录 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface RelayTransferMapper extends BaseMapperX<RelayTransferDO> {

    default RelayTransferDO selectByRequestId(Long requestId) {
        return selectOne(new LambdaQueryWrapperX<RelayTransferDO>()
                .eq(RelayTransferDO::getRequestId, requestId));
    }

    default List<RelayTransferDO> selectByUserId(Long userId) {
        return selectList(new LambdaQueryWrapperX<RelayTransferDO>()
                .eq(RelayTransferDO::getGiverId, userId)
                .or()
                .eq(RelayTransferDO::getReceiverId, userId)
                .orderByDesc(RelayTransferDO::getId));
    }

    /**
     * 查询需要归还的借用记录
     */
    default List<RelayTransferDO> selectPendingReturn(Long receiverId) {
        return selectList(new LambdaQueryWrapperX<RelayTransferDO>()
                .eq(RelayTransferDO::getReceiverId, receiverId)
                .eq(RelayTransferDO::getReturnRequired, true)
                .eq(RelayTransferDO::getReturnStatus, 0) // 未归还
                .orderByAsc(RelayTransferDO::getReturnDate));
    }

}


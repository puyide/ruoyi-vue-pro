package cn.iocoder.yudao.module.product.dal.mysql.relay;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.product.dal.dataobject.relay.RelayThankDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 接力感谢/评价 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface RelayThankMapper extends BaseMapperX<RelayThankDO> {

    /**
     * 查询用户收到的感谢
     */
    default List<RelayThankDO> selectReceivedThanks(Long toUserId) {
        return selectList(new LambdaQueryWrapperX<RelayThankDO>()
                .eq(RelayThankDO::getToUserId, toUserId)
                .eq(RelayThankDO::getIsPublic, true)
                .orderByDesc(RelayThankDO::getId));
    }

    /**
     * 查询某个交接的感谢
     */
    default List<RelayThankDO> selectByTransferId(Long transferId) {
        return selectList(new LambdaQueryWrapperX<RelayThankDO>()
                .eq(RelayThankDO::getTransferId, transferId));
    }

}


package cn.iocoder.yudao.module.product.dal.mysql.relay;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.product.dal.dataobject.relay.RelayRequestDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 接力申请 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface RelayRequestMapper extends BaseMapperX<RelayRequestDO> {

    /**
     * 查询我收到的申请（作为发布者）
     */
    default PageResult<RelayRequestDO> selectReceivedPage(Long giverId, Integer status, 
                                                           int pageNo, int pageSize) {
        PageParam pageParam = new PageParam();
        pageParam.setPageNo(pageNo);
        pageParam.setPageSize(pageSize);
        return selectPage(pageParam,
                new LambdaQueryWrapperX<RelayRequestDO>()
                        .eq(RelayRequestDO::getGiverId, giverId)
                        .eqIfPresent(RelayRequestDO::getStatus, status)
                        .orderByDesc(RelayRequestDO::getId));
    }

    /**
     * 查询我发出的申请（作为申请者）
     */
    default PageResult<RelayRequestDO> selectSentPage(Long requesterId, Integer status,
                                                       int pageNo, int pageSize) {
        PageParam pageParam = new PageParam();
        pageParam.setPageNo(pageNo);
        pageParam.setPageSize(pageSize);
        return selectPage(pageParam,
                new LambdaQueryWrapperX<RelayRequestDO>()
                        .eq(RelayRequestDO::getRequesterId, requesterId)
                        .eqIfPresent(RelayRequestDO::getStatus, status)
                        .orderByDesc(RelayRequestDO::getId));
    }

    /**
     * 查询某个好物的所有申请
     */
    default List<RelayRequestDO> selectByItemId(Long itemId) {
        return selectList(new LambdaQueryWrapperX<RelayRequestDO>()
                .eq(RelayRequestDO::getItemId, itemId)
                .orderByDesc(RelayRequestDO::getId));
    }

    /**
     * 统计待处理申请数量
     */
    default Long selectPendingCount(Long giverId) {
        return selectCount(new LambdaQueryWrapperX<RelayRequestDO>()
                .eq(RelayRequestDO::getGiverId, giverId)
                .eq(RelayRequestDO::getStatus, 0)); // 待处理
    }

    /**
     * 检查是否已申请
     */
    default RelayRequestDO selectByItemIdAndRequesterId(Long itemId, Long requesterId) {
        return selectOne(new LambdaQueryWrapperX<RelayRequestDO>()
                .eq(RelayRequestDO::getItemId, itemId)
                .eq(RelayRequestDO::getRequesterId, requesterId));
    }

}


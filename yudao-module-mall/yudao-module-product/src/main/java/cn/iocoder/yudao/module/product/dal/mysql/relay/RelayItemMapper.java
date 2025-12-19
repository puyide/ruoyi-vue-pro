package cn.iocoder.yudao.module.product.dal.mysql.relay;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.product.dal.dataobject.relay.RelayItemDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 接力好物 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface RelayItemMapper extends BaseMapperX<RelayItemDO> {

    default PageResult<RelayItemDO> selectPage(Long userId, Integer relayMethod, 
                                                String type, String ageGroup, 
                                                Integer status, int pageNo, int pageSize) {
        PageParam pageParam = new PageParam();
        pageParam.setPageNo(pageNo);
        pageParam.setPageSize(pageSize);
        return selectPage(pageParam,
                new LambdaQueryWrapperX<RelayItemDO>()
                        .eqIfPresent(RelayItemDO::getUserId, userId)
                        .eqIfPresent(RelayItemDO::getRelayMethod, relayMethod)
                        .eqIfPresent(RelayItemDO::getType, type)
                        .eqIfPresent(RelayItemDO::getAgeGroup, ageGroup)
                        .eqIfPresent(RelayItemDO::getStatus, status)
                        .orderByDesc(RelayItemDO::getId));
    }

    default PageResult<RelayItemDO> selectAvailablePage(Integer relayMethod, String type, 
                                                         String ageGroup, int pageNo, int pageSize) {
        PageParam pageParam = new PageParam();
        pageParam.setPageNo(pageNo);
        pageParam.setPageSize(pageSize);
        return selectPage(pageParam,
                new LambdaQueryWrapperX<RelayItemDO>()
                        .eq(RelayItemDO::getStatus, 1) // 接力中
                        .eqIfPresent(RelayItemDO::getRelayMethod, relayMethod)
                        .eqIfPresent(RelayItemDO::getType, type)
                        .likeIfPresent(RelayItemDO::getAgeGroup, ageGroup)
                        .orderByDesc(RelayItemDO::getId));
    }

    default PageResult<RelayItemDO> selectMyPublishedPage(Long userId, int pageNo, int pageSize) {
        PageParam pageParam = new PageParam();
        pageParam.setPageNo(pageNo);
        pageParam.setPageSize(pageSize);
        return selectPage(pageParam,
                new LambdaQueryWrapperX<RelayItemDO>()
                        .eq(RelayItemDO::getUserId, userId)
                        .orderByDesc(RelayItemDO::getId));
    }

}


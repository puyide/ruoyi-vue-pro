package cn.iocoder.yudao.module.member.dal.mysql.honor;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.member.dal.dataobject.honor.RelayThankMessageDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 感谢私信 Mapper
 *
 * @author 星语家园
 */
@Mapper
public interface RelayThankMessageMapper extends BaseMapperX<RelayThankMessageDO> {

    /**
     * 查询用户收到的感谢
     */
    default List<RelayThankMessageDO> selectByToUserId(Long userId) {
        return selectList(new LambdaQueryWrapperX<RelayThankMessageDO>()
                .eq(RelayThankMessageDO::getToUserId, userId)
                .orderByDesc(RelayThankMessageDO::getCreateTime));
    }

    /**
     * 查询用户未读感谢数量
     */
    default Long selectUnreadCountByToUserId(Long userId) {
        return selectCount(new LambdaQueryWrapperX<RelayThankMessageDO>()
                .eq(RelayThankMessageDO::getToUserId, userId)
                .eq(RelayThankMessageDO::getIsRead, false));
    }

    /**
     * 查询用户发送的感谢
     */
    default List<RelayThankMessageDO> selectByFromUserId(Long userId) {
        return selectList(new LambdaQueryWrapperX<RelayThankMessageDO>()
                .eq(RelayThankMessageDO::getFromUserId, userId)
                .orderByDesc(RelayThankMessageDO::getCreateTime));
    }

    /**
     * 统计用户收到的感谢数量
     */
    default Long selectCountByToUserId(Long userId) {
        return selectCount(RelayThankMessageDO::getToUserId, userId);
    }
}


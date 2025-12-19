package cn.iocoder.yudao.module.member.dal.mysql.honor;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.member.dal.dataobject.honor.MemberStatsInternalDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 后台统计 Mapper
 *
 * @author 星语家园
 */
@Mapper
public interface MemberStatsInternalMapper extends BaseMapperX<MemberStatsInternalDO> {

    /**
     * 根据用户ID查询
     */
    default MemberStatsInternalDO selectByUserId(Long userId) {
        return selectOne(MemberStatsInternalDO::getUserId, userId);
    }
}


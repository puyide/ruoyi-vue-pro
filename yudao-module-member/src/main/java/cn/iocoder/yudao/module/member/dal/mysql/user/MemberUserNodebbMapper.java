package cn.iocoder.yudao.module.member.dal.mysql.user;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.member.dal.dataobject.user.MemberUserNodebbDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员与 NodeBB 账户映射 Mapper
 */
@Mapper
public interface MemberUserNodebbMapper extends BaseMapperX<MemberUserNodebbDO> {

    default MemberUserNodebbDO selectByUserId(Long userId) {
        return selectById(userId);
    }

}


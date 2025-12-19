package cn.iocoder.yudao.module.member.dal.mysql.nodebb;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.member.dal.dataobject.nodebb.MemberNodebbUserDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员用户 - NodeBB 用户映射 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface MemberNodebbUserMapper extends BaseMapperX<MemberNodebbUserDO> {

    /**
     * 根据会员用户 ID 查询
     *
     * @param userId 会员用户 ID
     * @return NodeBB 用户映射记录
     */
    default MemberNodebbUserDO selectByUserId(Long userId) {
        return selectOne(MemberNodebbUserDO::getUserId, userId);
    }

    /**
     * 根据 NodeBB UID 查询
     *
     * @param nodebbUid NodeBB 用户 UID
     * @return NodeBB 用户映射记录
     */
    default MemberNodebbUserDO selectByNodebbUid(Integer nodebbUid) {
        return selectOne(MemberNodebbUserDO::getNodebbUid, nodebbUid);
    }

}

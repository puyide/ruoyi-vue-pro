package cn.iocoder.yudao.module.member.dal.mysql.children;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.member.dal.dataobject.children.MemberChildrenDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 会员模块 - 儿童个人信息 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface MemberChildrenMapper extends BaseMapperX<MemberChildrenDO> {

    /**
     * 根据ID和用户ID查询
     *
     * @param id 儿童信息ID
     * @param userId 用户ID（监护人）
     * @return 儿童信息
     */
    default MemberChildrenDO selectByIdAndUserId(Long id, Long userId) {
        return selectOne(MemberChildrenDO::getId, id, MemberChildrenDO::getUserId, userId);
    }

    /**
     * 根据用户ID查询列表
     *
     * @param userId 用户ID（监护人）
     * @return 儿童信息列表
     */
    default List<MemberChildrenDO> selectListByUserId(Long userId) {
        return selectList(MemberChildrenDO::getUserId, userId);
    }

}


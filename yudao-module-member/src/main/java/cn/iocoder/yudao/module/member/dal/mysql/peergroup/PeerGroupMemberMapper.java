package cn.iocoder.yudao.module.member.dal.mysql.peergroup;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.member.controller.admin.peergroup.vo.PeerGroupMemberPageReqVO;
import cn.iocoder.yudao.module.member.dal.dataobject.peergroup.PeerGroupMemberDO;
import cn.iocoder.yudao.module.member.enums.peergroup.PeerGroupMemberStatusEnum;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 同行小组成员 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface PeerGroupMemberMapper extends BaseMapperX<PeerGroupMemberDO> {

    default PageResult<PeerGroupMemberDO> selectPage(PeerGroupMemberPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PeerGroupMemberDO>()
                .eqIfPresent(PeerGroupMemberDO::getGroupId, reqVO.getGroupId())
                .eqIfPresent(PeerGroupMemberDO::getUserId, reqVO.getUserId())
                .eqIfPresent(PeerGroupMemberDO::getRole, reqVO.getRole())
                .eqIfPresent(PeerGroupMemberDO::getStatus, reqVO.getStatus())
                .orderByDesc(PeerGroupMemberDO::getJoinedAt));
    }

    /**
     * 查询用户在指定小组的成员记录
     */
    default PeerGroupMemberDO selectByGroupIdAndUserId(Long groupId, Long userId) {
        return selectOne(new LambdaQueryWrapperX<PeerGroupMemberDO>()
                .eq(PeerGroupMemberDO::getGroupId, groupId)
                .eq(PeerGroupMemberDO::getUserId, userId));
    }

    /**
     * 查询用户加入的所有活跃小组ID列表
     */
    default List<Long> selectActiveGroupIdsByUserId(Long userId) {
        return selectObjs(new LambdaQueryWrapperX<PeerGroupMemberDO>()
                .select(PeerGroupMemberDO::getGroupId)
                .eq(PeerGroupMemberDO::getUserId, userId)
                .in(PeerGroupMemberDO::getStatus, 
                    PeerGroupMemberStatusEnum.NORMAL.getStatus(),
                    PeerGroupMemberStatusEnum.MUTED.getStatus()));
    }

    /**
     * 查询用户加入的所有活跃小组
     */
    default List<PeerGroupMemberDO> selectActiveByUserId(Long userId) {
        return selectList(new LambdaQueryWrapperX<PeerGroupMemberDO>()
                .eq(PeerGroupMemberDO::getUserId, userId)
                .in(PeerGroupMemberDO::getStatus,
                    PeerGroupMemberStatusEnum.NORMAL.getStatus(),
                    PeerGroupMemberStatusEnum.MUTED.getStatus())
                .orderByDesc(PeerGroupMemberDO::getJoinedAt));
    }

    /**
     * 查询小组的所有活跃成员
     */
    default List<PeerGroupMemberDO> selectActiveByGroupId(Long groupId) {
        return selectList(new LambdaQueryWrapperX<PeerGroupMemberDO>()
                .eq(PeerGroupMemberDO::getGroupId, groupId)
                .in(PeerGroupMemberDO::getStatus,
                    PeerGroupMemberStatusEnum.NORMAL.getStatus(),
                    PeerGroupMemberStatusEnum.MUTED.getStatus())
                .orderByAsc(PeerGroupMemberDO::getJoinedAt));
    }

    /**
     * 统计小组活跃成员数
     */
    default Long selectActiveCountByGroupId(Long groupId) {
        return selectCount(new LambdaQueryWrapperX<PeerGroupMemberDO>()
                .eq(PeerGroupMemberDO::getGroupId, groupId)
                .in(PeerGroupMemberDO::getStatus,
                    PeerGroupMemberStatusEnum.NORMAL.getStatus(),
                    PeerGroupMemberStatusEnum.MUTED.getStatus()));
    }

    /**
     * 查询小组管理员（群主+管理员）
     */
    default List<PeerGroupMemberDO> selectAdminsByGroupId(Long groupId) {
        return selectList(new LambdaQueryWrapperX<PeerGroupMemberDO>()
                .eq(PeerGroupMemberDO::getGroupId, groupId)
                .in(PeerGroupMemberDO::getRole, "owner", "admin")
                .eq(PeerGroupMemberDO::getStatus, PeerGroupMemberStatusEnum.NORMAL.getStatus()));
    }

}


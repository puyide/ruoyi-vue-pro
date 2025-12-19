package cn.iocoder.yudao.module.member.dal.mysql.peergroup;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.member.controller.admin.peergroup.vo.PeerGroupJoinRequestPageReqVO;
import cn.iocoder.yudao.module.member.dal.dataobject.peergroup.PeerGroupJoinRequestDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 同行小组加入申请 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface PeerGroupJoinRequestMapper extends BaseMapperX<PeerGroupJoinRequestDO> {

    default PageResult<PeerGroupJoinRequestDO> selectPage(PeerGroupJoinRequestPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PeerGroupJoinRequestDO>()
                .eqIfPresent(PeerGroupJoinRequestDO::getGroupId, reqVO.getGroupId())
                .eqIfPresent(PeerGroupJoinRequestDO::getUserId, reqVO.getUserId())
                .eqIfPresent(PeerGroupJoinRequestDO::getStatus, reqVO.getStatus())
                .orderByDesc(PeerGroupJoinRequestDO::getCreateTime));
    }

    /**
     * 查询用户对指定小组的待审核申请
     */
    default PeerGroupJoinRequestDO selectPendingByGroupIdAndUserId(Long groupId, Long userId) {
        return selectOne(new LambdaQueryWrapperX<PeerGroupJoinRequestDO>()
                .eq(PeerGroupJoinRequestDO::getGroupId, groupId)
                .eq(PeerGroupJoinRequestDO::getUserId, userId)
                .eq(PeerGroupJoinRequestDO::getStatus, 0)); // 0-待审核
    }

    /**
     * 查询小组的待审核申请列表
     */
    default List<PeerGroupJoinRequestDO> selectPendingByGroupId(Long groupId) {
        return selectList(new LambdaQueryWrapperX<PeerGroupJoinRequestDO>()
                .eq(PeerGroupJoinRequestDO::getGroupId, groupId)
                .eq(PeerGroupJoinRequestDO::getStatus, 0)
                .orderByAsc(PeerGroupJoinRequestDO::getCreateTime));
    }

}


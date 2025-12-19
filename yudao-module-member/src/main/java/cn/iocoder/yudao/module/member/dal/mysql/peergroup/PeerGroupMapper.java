package cn.iocoder.yudao.module.member.dal.mysql.peergroup;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.member.controller.admin.peergroup.vo.PeerGroupPageReqVO;
import cn.iocoder.yudao.module.member.dal.dataobject.peergroup.PeerGroupDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 同行小组 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface PeerGroupMapper extends BaseMapperX<PeerGroupDO> {

    default PageResult<PeerGroupDO> selectPage(PeerGroupPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PeerGroupDO>()
                .likeIfPresent(PeerGroupDO::getName, reqVO.getName())
                .eqIfPresent(PeerGroupDO::getCategory, reqVO.getCategory())
                .eqIfPresent(PeerGroupDO::getAgeGroup, reqVO.getAgeGroup())
                .eqIfPresent(PeerGroupDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(PeerGroupDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(PeerGroupDO::getId));
    }

    default List<PeerGroupDO> selectListByStatus(Integer status) {
        return selectList(PeerGroupDO::getStatus, status);
    }

    default List<PeerGroupDO> selectListByCategory(String category) {
        return selectList(new LambdaQueryWrapperX<PeerGroupDO>()
                .eqIfPresent(PeerGroupDO::getCategory, category)
                .eq(PeerGroupDO::getStatus, 0)
                .orderByDesc(PeerGroupDO::getMemberCount));
    }

    /**
     * 增加成员数
     */
    @Update("UPDATE peer_group SET member_count = member_count + 1 WHERE id = #{id}")
    int incrementMemberCount(@Param("id") Long id);

    /**
     * 减少成员数
     */
    @Update("UPDATE peer_group SET member_count = GREATEST(member_count - 1, 0) WHERE id = #{id}")
    int decrementMemberCount(@Param("id") Long id);

    /**
     * 更新 NodeBB 同步状态
     */
    default void updateNodebbSyncStatus(Long id, Integer syncStatus, Long nodebbGroupId, Long nodebbCategoryId) {
        PeerGroupDO updateObj = new PeerGroupDO();
        updateObj.setId(id);
        updateObj.setNodebbSyncStatus(syncStatus);
        updateObj.setNodebbGroupId(nodebbGroupId);
        updateObj.setNodebbCategoryId(nodebbCategoryId);
        updateById(updateObj);
    }

}


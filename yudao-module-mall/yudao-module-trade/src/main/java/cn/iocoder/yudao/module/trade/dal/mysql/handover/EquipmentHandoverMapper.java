package cn.iocoder.yudao.module.trade.dal.mysql.handover;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.trade.controller.app.handover.vo.AppHandoverPageReqVO;
import cn.iocoder.yudao.module.trade.dal.dataobject.handover.EquipmentHandoverDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 器材交接会话 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface EquipmentHandoverMapper extends BaseMapperX<EquipmentHandoverDO> {

    /**
     * 根据预约ID查询交接会话
     */
    default EquipmentHandoverDO selectByReservationId(Long reservationId) {
        return selectOne(EquipmentHandoverDO::getReservationId, reservationId);
    }

    /**
     * 查询用户参与的交接会话列表（作为出借方或借用方）
     */
    default List<EquipmentHandoverDO> selectListByUserId(Long userId) {
        return selectList(new LambdaQueryWrapperX<EquipmentHandoverDO>()
                .eq(EquipmentHandoverDO::getLenderUserId, userId)
                .or()
                .eq(EquipmentHandoverDO::getBorrowerUserId, userId)
                .orderByDesc(EquipmentHandoverDO::getLastMessageTime));
    }

    /**
     * 分页查询用户参与的交接会话
     */
    default PageResult<EquipmentHandoverDO> selectPage(Long userId, AppHandoverPageReqVO pageReqVO) {
        return selectPage(pageReqVO, new LambdaQueryWrapperX<EquipmentHandoverDO>()
                .and(w -> w.eq(EquipmentHandoverDO::getLenderUserId, userId)
                        .or()
                        .eq(EquipmentHandoverDO::getBorrowerUserId, userId))
                .eqIfPresent(EquipmentHandoverDO::getHandoverStatus, pageReqVO.getStatus())
                .orderByDesc(EquipmentHandoverDO::getLastMessageTime));
    }

    /**
     * 查询出借方的交接会话分页
     */
    default PageResult<EquipmentHandoverDO> selectLenderPage(Long userId, AppHandoverPageReqVO pageReqVO) {
        return selectPage(pageReqVO, new LambdaQueryWrapperX<EquipmentHandoverDO>()
                .eq(EquipmentHandoverDO::getLenderUserId, userId)
                .eqIfPresent(EquipmentHandoverDO::getHandoverStatus, pageReqVO.getStatus())
                .orderByDesc(EquipmentHandoverDO::getLastMessageTime));
    }

    /**
     * 查询借用方的交接会话分页
     */
    default PageResult<EquipmentHandoverDO> selectBorrowerPage(Long userId, AppHandoverPageReqVO pageReqVO) {
        return selectPage(pageReqVO, new LambdaQueryWrapperX<EquipmentHandoverDO>()
                .eq(EquipmentHandoverDO::getBorrowerUserId, userId)
                .eqIfPresent(EquipmentHandoverDO::getHandoverStatus, pageReqVO.getStatus())
                .orderByDesc(EquipmentHandoverDO::getLastMessageTime));
    }

}


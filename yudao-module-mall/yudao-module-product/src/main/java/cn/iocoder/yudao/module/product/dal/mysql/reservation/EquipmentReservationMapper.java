package cn.iocoder.yudao.module.product.dal.mysql.reservation;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.product.controller.admin.reservation.vo.ReservationPageReqVO;
import cn.iocoder.yudao.module.product.dal.dataobject.reservation.EquipmentReservationDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 器材预约 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface EquipmentReservationMapper extends BaseMapperX<EquipmentReservationDO> {

    default PageResult<EquipmentReservationDO> selectPage(ReservationPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<EquipmentReservationDO>()
                .eqIfPresent(EquipmentReservationDO::getEquipmentId, reqVO.getEquipmentId())
                .eqIfPresent(EquipmentReservationDO::getUserId, reqVO.getUserId())
                .eqIfPresent(EquipmentReservationDO::getReservationStatus, reqVO.getReservationStatus())
                .eqIfPresent(EquipmentReservationDO::getOwnerUserId, reqVO.getOwnerUserId())
                .betweenIfPresent(EquipmentReservationDO::getReservationDate, reqVO.getReservationDate())
                .orderByDesc(EquipmentReservationDO::getId));
    }

    default List<EquipmentReservationDO> selectListByEquipmentIdAndStatus(Long equipmentId, Integer status) {
        return selectList(new LambdaQueryWrapperX<EquipmentReservationDO>()
                .eq(EquipmentReservationDO::getEquipmentId, equipmentId)
                .eq(EquipmentReservationDO::getReservationStatus, status)
                .orderByAsc(EquipmentReservationDO::getPlanBorrowDate));
    }

    default List<EquipmentReservationDO> selectListByUserIdAndStatus(Long userId, Integer status) {
        return selectList(new LambdaQueryWrapperX<EquipmentReservationDO>()
                .eq(EquipmentReservationDO::getUserId, userId)
                .eq(EquipmentReservationDO::getReservationStatus, status)
                .orderByDesc(EquipmentReservationDO::getReservationDate));
    }

}


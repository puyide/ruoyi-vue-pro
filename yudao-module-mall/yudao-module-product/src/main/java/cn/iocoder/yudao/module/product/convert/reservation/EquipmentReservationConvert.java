package cn.iocoder.yudao.module.product.convert.reservation;

import cn.iocoder.yudao.module.product.controller.app.reservation.vo.AppReservationCreateReqVO;
import cn.iocoder.yudao.module.product.controller.app.reservation.vo.AppReservationRespVO;
import cn.iocoder.yudao.module.product.dal.dataobject.reservation.EquipmentReservationDO;
import cn.iocoder.yudao.module.product.enums.reservation.DepositStatusEnum;
import cn.iocoder.yudao.module.product.enums.reservation.ReservationStatusEnum;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 器材预约 Convert
 *
 * @author 芋道源码
 */
@Mapper
public interface EquipmentReservationConvert {

    EquipmentReservationConvert INSTANCE = Mappers.getMapper(EquipmentReservationConvert.class);

    EquipmentReservationDO convert(AppReservationCreateReqVO bean);

    @Mapping(target = "reservationStatusName", source = "reservationStatus", qualifiedByName = "statusToName")
    @Mapping(target = "depositStatusName", source = "depositStatus", qualifiedByName = "depositStatusToName")
    AppReservationRespVO convert(EquipmentReservationDO bean);

    List<AppReservationRespVO> convertList(List<EquipmentReservationDO> list);

    /**
     * 预约状态转换为名称
     */
    @Named("statusToName")
    default String statusToName(Integer status) {
        if (status == null) {
            return "未知";
        }
        ReservationStatusEnum statusEnum = ReservationStatusEnum.valueOf(status);
        return statusEnum != null ? statusEnum.getName() : "未知";
    }

    /**
     * 押金状态转换为名称
     */
    @Named("depositStatusToName")
    default String depositStatusToName(Integer status) {
        if (status == null) {
            return "未知";
        }
        DepositStatusEnum statusEnum = DepositStatusEnum.valueOf(status);
        return statusEnum != null ? statusEnum.getName() : "未知";
    }

}


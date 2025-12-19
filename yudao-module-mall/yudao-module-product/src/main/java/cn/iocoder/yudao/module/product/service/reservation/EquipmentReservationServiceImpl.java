package cn.iocoder.yudao.module.product.service.reservation;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.product.controller.admin.reservation.vo.ReservationPageReqVO;
import cn.iocoder.yudao.module.product.controller.app.reservation.vo.AppReservationCreateReqVO;
import cn.iocoder.yudao.module.product.controller.app.reservation.vo.AppReservationPageReqVO;
import cn.iocoder.yudao.module.product.convert.reservation.EquipmentReservationConvert;
import cn.iocoder.yudao.module.product.dal.dataobject.reservation.EquipmentReservationDO;
import cn.iocoder.yudao.module.product.dal.dataobject.spu.ProductSpuDO;
import cn.iocoder.yudao.module.product.dal.mysql.reservation.EquipmentReservationMapper;
import cn.iocoder.yudao.module.product.dal.mysql.spu.ProductSpuMapper;
import cn.iocoder.yudao.module.product.enums.reservation.DepositStatusEnum;
import cn.iocoder.yudao.module.product.enums.reservation.ReservationStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.product.enums.ErrorCodeConstants.*;

/**
 * 器材预约 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
@Slf4j
public class EquipmentReservationServiceImpl implements EquipmentReservationService {

    @Resource
    private EquipmentReservationMapper reservationMapper;

    @Resource
    private ProductSpuMapper spuMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createReservation(Long userId, AppReservationCreateReqVO createReqVO) {
        // 1. 校验器材是否存在
        ProductSpuDO spu = spuMapper.selectById(createReqVO.getEquipmentId());
        if (spu == null) {
            throw exception(SPU_NOT_EXISTS);
        }
        
        // 2. 不能预约自己的器材
        // 注意：这里假设SPU有owner字段，如果没有需要调整
        // if (userId.equals(spu.getOwnerUserId())) {
        //     throw exception(RESERVATION_CANNOT_SELF);
        // }
        
        // 3. 创建预约
        EquipmentReservationDO reservation = EquipmentReservationConvert.INSTANCE.convert(createReqVO);
        reservation.setUserId(userId);
        // reservation.setOwnerUserId(spu.getOwnerUserId()); // 如果SPU有owner字段
        reservation.setReservationStatus(ReservationStatusEnum.PENDING.getStatus());
        reservation.setReservationDate(LocalDateTime.now());
        reservation.setDepositStatus(DepositStatusEnum.PENDING.getStatus());
        // reservation.setDepositAmount(spu.getDepositAmount()); // 如果SPU有押金字段
        
        reservationMapper.insert(reservation);
        
        log.info("[createReservation][用户({})创建预约成功，预约ID:{}，器材ID:{}]", 
                userId, reservation.getId(), createReqVO.getEquipmentId());
        
        return reservation.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmReservation(Long userId, Long reservationId) {
        // 1. 校验预约是否存在
        EquipmentReservationDO reservation = reservationMapper.selectById(reservationId);
        if (reservation == null) {
            throw exception(RESERVATION_NOT_EXISTS);
        }
        
        // 2. 校验是否是器材所有者
        if (!userId.equals(reservation.getOwnerUserId())) {
            throw exception(RESERVATION_NOT_OWNER);
        }
        
        // 3. 校验状态
        if (!ReservationStatusEnum.PENDING.getStatus().equals(reservation.getReservationStatus())) {
            throw exception(RESERVATION_STATUS_ERROR);
        }
        
        // 4. 更新状态
        reservation.setReservationStatus(ReservationStatusEnum.CONFIRMED.getStatus());
        reservationMapper.updateById(reservation);
        
        log.info("[confirmReservation][所有者({})确认预约成功，预约ID:{}]", userId, reservationId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelReservation(Long userId, Long reservationId, String cancelReason) {
        // 1. 校验预约是否存在
        EquipmentReservationDO reservation = reservationMapper.selectById(reservationId);
        if (reservation == null) {
            throw exception(RESERVATION_NOT_EXISTS);
        }
        
        // 2. 校验是否有权限取消（预约人或所有者都可以取消）
        if (!userId.equals(reservation.getUserId()) && !userId.equals(reservation.getOwnerUserId())) {
            throw exception(RESERVATION_CANCEL_DENIED);
        }
        
        // 3. 校验状态（只有待确认和已确认状态可以取消）
        Integer status = reservation.getReservationStatus();
        if (!ReservationStatusEnum.PENDING.getStatus().equals(status) 
                && !ReservationStatusEnum.CONFIRMED.getStatus().equals(status)) {
            throw exception(RESERVATION_STATUS_ERROR);
        }
        
        // 4. 更新状态
        reservation.setReservationStatus(ReservationStatusEnum.CANCELLED.getStatus());
        reservation.setCancelReason(cancelReason);
        reservationMapper.updateById(reservation);
        
        log.info("[cancelReservation][用户({})取消预约成功，预约ID:{}，原因:{}]", userId, reservationId, cancelReason);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeReservation(Long reservationId) {
        // 1. 校验预约是否存在
        EquipmentReservationDO reservation = reservationMapper.selectById(reservationId);
        if (reservation == null) {
            throw exception(RESERVATION_NOT_EXISTS);
        }
        
        // 2. 更新状态
        reservation.setReservationStatus(ReservationStatusEnum.COMPLETED.getStatus());
        reservationMapper.updateById(reservation);
        
        log.info("[completeReservation][预约({})完成]", reservationId);
    }

    @Override
    public EquipmentReservationDO getReservation(Long reservationId) {
        return reservationMapper.selectById(reservationId);
    }

    @Override
    public PageResult<EquipmentReservationDO> getMyReservationPage(Long userId, AppReservationPageReqVO pageReqVO) {
        return reservationMapper.selectPage(pageReqVO, new LambdaQueryWrapperX<EquipmentReservationDO>()
                .eq(EquipmentReservationDO::getUserId, userId)
                .eqIfPresent(EquipmentReservationDO::getReservationStatus, pageReqVO.getReservationStatus())
                .orderByDesc(EquipmentReservationDO::getId));
    }

    @Override
    public PageResult<EquipmentReservationDO> getReceivedReservationPage(Long userId, AppReservationPageReqVO pageReqVO) {
        return reservationMapper.selectPage(pageReqVO, new LambdaQueryWrapperX<EquipmentReservationDO>()
                .eq(EquipmentReservationDO::getOwnerUserId, userId)
                .eqIfPresent(EquipmentReservationDO::getReservationStatus, pageReqVO.getReservationStatus())
                .orderByDesc(EquipmentReservationDO::getId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recordActualBorrow(Long reservationId) {
        EquipmentReservationDO reservation = reservationMapper.selectById(reservationId);
        if (reservation == null) {
            throw exception(RESERVATION_NOT_EXISTS);
        }
        reservation.setActualBorrowDate(LocalDateTime.now());
        reservationMapper.updateById(reservation);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recordActualReturn(Long reservationId) {
        EquipmentReservationDO reservation = reservationMapper.selectById(reservationId);
        if (reservation == null) {
            throw exception(RESERVATION_NOT_EXISTS);
        }
        reservation.setActualReturnDate(LocalDateTime.now());
        reservationMapper.updateById(reservation);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDepositStatus(Long reservationId, Integer depositStatus) {
        EquipmentReservationDO reservation = reservationMapper.selectById(reservationId);
        if (reservation == null) {
            throw exception(RESERVATION_NOT_EXISTS);
        }
        reservation.setDepositStatus(depositStatus);
        reservationMapper.updateById(reservation);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindHandover(Long reservationId, Long handoverId) {
        EquipmentReservationDO reservation = reservationMapper.selectById(reservationId);
        if (reservation == null) {
            throw exception(RESERVATION_NOT_EXISTS);
        }
        reservation.setHandoverId(handoverId);
        reservationMapper.updateById(reservation);
    }

    // ==================== 管理后台 ====================

    @Override
    public PageResult<EquipmentReservationDO> getReservationPage(ReservationPageReqVO pageReqVO) {
        return reservationMapper.selectPage(pageReqVO, new LambdaQueryWrapperX<EquipmentReservationDO>()
                .eqIfPresent(EquipmentReservationDO::getReservationStatus, pageReqVO.getReservationStatus())
                .eqIfPresent(EquipmentReservationDO::getDepositStatus, pageReqVO.getDepositStatus())
                .betweenIfPresent(EquipmentReservationDO::getCreateTime, pageReqVO.getCreateTime())
                .orderByDesc(EquipmentReservationDO::getId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelReservation(Long reservationId, String cancelReason) {
        // 1. 校验预约是否存在
        EquipmentReservationDO reservation = reservationMapper.selectById(reservationId);
        if (reservation == null) {
            throw exception(RESERVATION_NOT_EXISTS);
        }
        
        // 2. 校验状态（只有待确认和已确认状态可以取消）
        Integer status = reservation.getReservationStatus();
        if (!ReservationStatusEnum.PENDING.getStatus().equals(status) 
                && !ReservationStatusEnum.CONFIRMED.getStatus().equals(status)) {
            throw exception(RESERVATION_STATUS_ERROR);
        }
        
        // 3. 更新状态
        reservation.setReservationStatus(ReservationStatusEnum.CANCELLED.getStatus());
        reservation.setCancelReason(cancelReason);
        reservationMapper.updateById(reservation);
        
        log.info("[cancelReservation][管理员取消预约，预约ID:{}，原因:{}]", reservationId, cancelReason);
    }

}


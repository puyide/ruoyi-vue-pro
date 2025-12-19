package cn.iocoder.yudao.module.product.service.reservation;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.product.controller.admin.reservation.vo.ReservationPageReqVO;
import cn.iocoder.yudao.module.product.controller.app.reservation.vo.AppReservationCreateReqVO;
import cn.iocoder.yudao.module.product.controller.app.reservation.vo.AppReservationPageReqVO;
import cn.iocoder.yudao.module.product.dal.dataobject.reservation.EquipmentReservationDO;

import jakarta.validation.Valid;

/**
 * 器材预约 Service 接口
 *
 * @author 芋道源码
 */
public interface EquipmentReservationService {

    /**
     * 创建器材预约
     *
     * @param userId 用户ID（预约人）
     * @param createReqVO 创建信息
     * @return 预约ID
     */
    Long createReservation(Long userId, @Valid AppReservationCreateReqVO createReqVO);

    /**
     * 确认预约（器材所有者操作）
     *
     * @param userId 用户ID（器材所有者）
     * @param reservationId 预约ID
     */
    void confirmReservation(Long userId, Long reservationId);

    /**
     * 取消预约
     *
     * @param userId 用户ID
     * @param reservationId 预约ID
     * @param cancelReason 取消原因
     */
    void cancelReservation(Long userId, Long reservationId, String cancelReason);

    /**
     * 完成预约
     *
     * @param reservationId 预约ID
     */
    void completeReservation(Long reservationId);

    /**
     * 获取预约详情
     *
     * @param reservationId 预约ID
     * @return 预约信息
     */
    EquipmentReservationDO getReservation(Long reservationId);

    /**
     * 获取我的预约分页
     *
     * @param userId 用户ID
     * @param pageReqVO 分页查询条件
     * @return 分页结果
     */
    PageResult<EquipmentReservationDO> getMyReservationPage(Long userId, AppReservationPageReqVO pageReqVO);

    /**
     * 获取我收到的预约分页（作为器材所有者）
     *
     * @param userId 用户ID（器材所有者）
     * @param pageReqVO 分页查询条件
     * @return 分页结果
     */
    PageResult<EquipmentReservationDO> getReceivedReservationPage(Long userId, AppReservationPageReqVO pageReqVO);

    /**
     * 记录实际借用时间
     *
     * @param reservationId 预约ID
     */
    void recordActualBorrow(Long reservationId);

    /**
     * 记录实际归还时间
     *
     * @param reservationId 预约ID
     */
    void recordActualReturn(Long reservationId);

    /**
     * 更新押金状态
     *
     * @param reservationId 预约ID
     * @param depositStatus 押金状态
     */
    void updateDepositStatus(Long reservationId, Integer depositStatus);

    /**
     * 关联交接会话
     *
     * @param reservationId 预约ID
     * @param handoverId 交接会话ID
     */
    void bindHandover(Long reservationId, Long handoverId);

    // ==================== 管理后台 ====================

    /**
     * 获取预约分页（管理后台）
     *
     * @param pageReqVO 分页查询条件
     * @return 分页结果
     */
    PageResult<EquipmentReservationDO> getReservationPage(ReservationPageReqVO pageReqVO);

    /**
     * 取消预约（管理后台，无需校验用户）
     *
     * @param reservationId 预约ID
     * @param cancelReason 取消原因
     */
    void cancelReservation(Long reservationId, String cancelReason);

}


package cn.iocoder.yudao.module.product.controller.admin.reservation;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.product.controller.admin.reservation.vo.ReservationPageReqVO;
import cn.iocoder.yudao.module.product.controller.admin.reservation.vo.ReservationRespVO;
import cn.iocoder.yudao.module.product.dal.dataobject.reservation.EquipmentReservationDO;
import cn.iocoder.yudao.module.product.service.reservation.EquipmentReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 器材预约
 *
 * @author 芋道源码
 */
@Tag(name = "管理后台 - 器材预约")
@RestController
@RequestMapping("/product/reservation")
@Validated
public class ReservationController {

    @Resource
    private EquipmentReservationService equipmentReservationService;

    @GetMapping("/page")
    @Operation(summary = "获取器材预约分页")
    @PreAuthorize("@ss.hasPermission('product:reservation:query')")
    public CommonResult<PageResult<ReservationRespVO>> getReservationPage(@Valid ReservationPageReqVO pageReqVO) {
        PageResult<EquipmentReservationDO> pageResult = equipmentReservationService.getReservationPage(pageReqVO);
        // TODO: 转换成 ReservationRespVO，填充用户昵称、器材名称等
        return success(new PageResult<>());
    }

    @GetMapping("/get")
    @Operation(summary = "获取器材预约详情")
    @Parameter(name = "id", description = "预约ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('product:reservation:query')")
    public CommonResult<ReservationRespVO> getReservation(@RequestParam("id") Long id) {
        EquipmentReservationDO reservation = equipmentReservationService.getReservation(id);
        if (reservation == null) {
            return success(null);
        }
        ReservationRespVO respVO = new ReservationRespVO();
        respVO.setId(reservation.getId());
        respVO.setEquipmentId(reservation.getEquipmentId());
        respVO.setUserId(reservation.getUserId());
        respVO.setOwnerUserId(reservation.getOwnerUserId());
        respVO.setReservationStatus(reservation.getReservationStatus());
        respVO.setReservationDate(reservation.getReservationDate());
        respVO.setPlanBorrowDate(reservation.getPlanBorrowDate());
        respVO.setPlanBorrowDays(reservation.getPlanBorrowDays());
        respVO.setActualBorrowDate(reservation.getActualBorrowDate());
        respVO.setActualReturnDate(reservation.getActualReturnDate());
        respVO.setDepositAmount(reservation.getDepositAmount());
        respVO.setDepositStatus(reservation.getDepositStatus());
        respVO.setRemark(reservation.getRemark());
        respVO.setCancelReason(reservation.getCancelReason());
        respVO.setHandoverId(reservation.getHandoverId());
        respVO.setCreateTime(reservation.getCreateTime());
        // TODO: 填充用户昵称、器材名称等
        return success(respVO);
    }

    @PutMapping("/cancel")
    @Operation(summary = "取消预约")
    @PreAuthorize("@ss.hasPermission('product:reservation:update')")
    public CommonResult<Boolean> cancelReservation(@RequestParam("id") Long id, 
                                                    @RequestParam("reason") String reason) {
        equipmentReservationService.cancelReservation(id, reason);
        return success(true);
    }

    @PutMapping("/admin/confirm")
    @Operation(summary = "管理员确认预约")
    @Parameter(name = "id", description = "预约ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('product:reservation:update')")
    public CommonResult<Boolean> confirmReservation(@RequestParam("id") Long id) {
        // TODO: 实现管理员确认预约
        return success(true);
    }

    @PutMapping("/admin/complete")
    @Operation(summary = "管理员完成预约")
    @Parameter(name = "id", description = "预约ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('product:reservation:update')")
    public CommonResult<Boolean> completeReservation(@RequestParam("id") Long id) {
        // TODO: 实现管理员完成预约
        return success(true);
    }

}


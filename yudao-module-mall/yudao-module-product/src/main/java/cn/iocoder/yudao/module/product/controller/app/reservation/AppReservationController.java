package cn.iocoder.yudao.module.product.controller.app.reservation;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.product.controller.app.reservation.vo.AppReservationCreateReqVO;
import cn.iocoder.yudao.module.product.controller.app.reservation.vo.AppReservationPageReqVO;
import cn.iocoder.yudao.module.product.controller.app.reservation.vo.AppReservationRespVO;
import cn.iocoder.yudao.module.product.convert.reservation.EquipmentReservationConvert;
import cn.iocoder.yudao.module.product.dal.dataobject.reservation.EquipmentReservationDO;
import cn.iocoder.yudao.module.product.service.reservation.EquipmentReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

/**
 * 用户 APP - 器材预约
 *
 * @author 芋道源码
 */
@Tag(name = "用户 APP - 器材预约")
@RestController
@RequestMapping("/product/reservation")
@Validated
public class AppReservationController {

    @Resource
    private EquipmentReservationService reservationService;

    @PostMapping("/create")
    @Operation(summary = "创建器材预约")
    public CommonResult<Long> createReservation(@Valid @RequestBody AppReservationCreateReqVO createReqVO) {
        return success(reservationService.createReservation(getLoginUserId(), createReqVO));
    }

    @PutMapping("/confirm")
    @Operation(summary = "确认预约（器材所有者操作）")
    @Parameter(name = "id", description = "预约ID", required = true, example = "1024")
    public CommonResult<Boolean> confirmReservation(@RequestParam("id") Long id) {
        reservationService.confirmReservation(getLoginUserId(), id);
        return success(true);
    }

    @PutMapping("/cancel")
    @Operation(summary = "取消预约")
    @Parameter(name = "id", description = "预约ID", required = true, example = "1024")
    @Parameter(name = "reason", description = "取消原因", example = "计划有变")
    public CommonResult<Boolean> cancelReservation(@RequestParam("id") Long id,
                                                    @RequestParam(value = "reason", required = false) String reason) {
        reservationService.cancelReservation(getLoginUserId(), id, reason);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取预约详情")
    @Parameter(name = "id", description = "预约ID", required = true, example = "1024")
    public CommonResult<AppReservationRespVO> getReservation(@RequestParam("id") Long id) {
        EquipmentReservationDO reservation = reservationService.getReservation(id);
        return success(EquipmentReservationConvert.INSTANCE.convert(reservation));
    }

    @GetMapping("/my-page")
    @Operation(summary = "获取我的预约分页")
    public CommonResult<PageResult<AppReservationRespVO>> getMyReservationPage(@Valid AppReservationPageReqVO pageReqVO) {
        PageResult<EquipmentReservationDO> pageResult = reservationService.getMyReservationPage(getLoginUserId(), pageReqVO);
        return success(new PageResult<>(
                EquipmentReservationConvert.INSTANCE.convertList(pageResult.getList()),
                pageResult.getTotal()));
    }

    @GetMapping("/received-page")
    @Operation(summary = "获取我收到的预约分页（作为器材所有者）")
    public CommonResult<PageResult<AppReservationRespVO>> getReceivedReservationPage(@Valid AppReservationPageReqVO pageReqVO) {
        PageResult<EquipmentReservationDO> pageResult = reservationService.getReceivedReservationPage(getLoginUserId(), pageReqVO);
        return success(new PageResult<>(
                EquipmentReservationConvert.INSTANCE.convertList(pageResult.getList()),
                pageResult.getTotal()));
    }

}


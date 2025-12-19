package cn.iocoder.yudao.module.trade.controller.admin.handover;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.trade.controller.admin.handover.vo.HandoverPageReqVO;
import cn.iocoder.yudao.module.trade.controller.admin.handover.vo.HandoverRespVO;
import cn.iocoder.yudao.module.trade.controller.admin.handover.vo.MessageSendReqVO;
import cn.iocoder.yudao.module.trade.dal.dataobject.handover.EquipmentFeedbackDO;
import cn.iocoder.yudao.module.trade.dal.dataobject.handover.EquipmentHandoverDO;
import cn.iocoder.yudao.module.trade.dal.dataobject.handover.EquipmentHandoverMessageDO;
import cn.iocoder.yudao.module.trade.service.handover.EquipmentHandoverService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 交接会话
 *
 * @author 芋道源码
 */
@Tag(name = "管理后台 - 交接会话")
@RestController
@RequestMapping("/trade/handover/admin")
@Validated
public class HandoverController {

    @Resource
    private EquipmentHandoverService equipmentHandoverService;

    @GetMapping("/page")
    @Operation(summary = "获取交接会话分页")
    @PreAuthorize("@ss.hasPermission('trade:handover:query')")
    public CommonResult<PageResult<HandoverRespVO>> getHandoverPage(@Valid HandoverPageReqVO pageReqVO) {
        PageResult<EquipmentHandoverDO> pageResult = equipmentHandoverService.getHandoverPage(pageReqVO);
        // TODO: 转换成 HandoverRespVO，填充用户昵称、器材名称等
        return success(new PageResult<>());
    }

    @GetMapping("/get")
    @Operation(summary = "获取交接会话详情")
    @Parameter(name = "id", description = "交接会话ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('trade:handover:query')")
    public CommonResult<HandoverRespVO> getHandover(@RequestParam("id") Long id) {
        EquipmentHandoverDO handover = equipmentHandoverService.getHandover(id);
        if (handover == null) {
            return success(null);
        }
        HandoverRespVO respVO = new HandoverRespVO();
        respVO.setId(handover.getId());
        respVO.setReservationId(handover.getReservationId());
        respVO.setEquipmentId(handover.getEquipmentId());
        respVO.setLenderUserId(handover.getLenderUserId());
        respVO.setBorrowerUserId(handover.getBorrowerUserId());
        respVO.setHandoverStatus(handover.getHandoverStatus());
        respVO.setHandoverMode(handover.getHandoverMode());
        respVO.setHandoverAddress(handover.getHandoverAddress());
        respVO.setHandoverTime(handover.getHandoverTime());
        respVO.setActualHandoverTime(handover.getActualHandoverTime());
        respVO.setEquipmentCondition(handover.getEquipmentCondition());
        respVO.setEquipmentPhotos(handover.getEquipmentPhotos());
        respVO.setExpressNo(handover.getExpressNo());
        respVO.setExpressCompany(handover.getExpressCompany());
        respVO.setExpressStatus(handover.getExpressStatus());
        respVO.setReturnCondition(handover.getReturnCondition());
        respVO.setReturnPhotos(handover.getReturnPhotos());
        respVO.setReturnTime(handover.getReturnTime());
        respVO.setLenderVerifyResult(handover.getLenderVerifyResult());
        respVO.setBorrowerVerifyResult(handover.getBorrowerVerifyResult());
        respVO.setLastMessageTime(handover.getLastMessageTime());
        respVO.setCreateTime(handover.getCreateTime());
        // TODO: 填充用户昵称、器材名称等
        return success(respVO);
    }

    @GetMapping("/message/list")
    @Operation(summary = "获取交接会话消息列表")
    @Parameter(name = "handoverId", description = "交接会话ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('trade:handover:query')")
    public CommonResult<List<EquipmentHandoverMessageDO>> getMessageList(@RequestParam("handoverId") Long handoverId) {
        List<EquipmentHandoverMessageDO> messageList = equipmentHandoverService.getMessageList(handoverId);
        return success(messageList);
    }

    @GetMapping("/feedback/list")
    @Operation(summary = "获取交接会话反馈列表")
    @Parameter(name = "handoverId", description = "交接会话ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('trade:handover:query')")
    public CommonResult<List<EquipmentFeedbackDO>> getFeedbackList(@RequestParam("handoverId") Long handoverId) {
        List<EquipmentFeedbackDO> feedbackList = equipmentHandoverService.getFeedbackList(handoverId);
        return success(feedbackList);
    }

    @PutMapping("/update-status")
    @Operation(summary = "更新交接状态")
    @PreAuthorize("@ss.hasPermission('trade:handover:update')")
    public CommonResult<Boolean> updateHandoverStatus(@RequestParam("id") Long id,
                                                       @RequestParam("status") Integer status) {
        equipmentHandoverService.updateHandoverStatus(id, status);
        return success(true);
    }

    @PostMapping("/message/send")
    @Operation(summary = "管理员发送系统消息")
    @PreAuthorize("@ss.hasPermission('trade:handover:update')")
    public CommonResult<Boolean> sendAdminMessage(@Valid @RequestBody MessageSendReqVO reqVO) {
        equipmentHandoverService.sendAdminMessage(reqVO.getHandoverId(), reqVO.getContent());
        return success(true);
    }

}


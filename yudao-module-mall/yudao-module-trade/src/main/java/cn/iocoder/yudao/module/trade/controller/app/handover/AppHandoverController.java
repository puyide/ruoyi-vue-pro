package cn.iocoder.yudao.module.trade.controller.app.handover;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.trade.controller.app.handover.vo.*;
import cn.iocoder.yudao.module.trade.convert.handover.EquipmentHandoverConvert;
import cn.iocoder.yudao.module.trade.dal.dataobject.handover.EquipmentFeedbackDO;
import cn.iocoder.yudao.module.trade.dal.dataobject.handover.EquipmentHandoverDO;
import cn.iocoder.yudao.module.trade.dal.dataobject.handover.EquipmentHandoverMessageDO;
import cn.iocoder.yudao.module.trade.service.handover.EquipmentHandoverService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

/**
 * 用户 APP - 器材交接会话
 *
 * @author 芋道源码
 */
@Tag(name = "用户 APP - 器材交接会话")
@RestController
@RequestMapping("/trade/handover")
@Validated
public class AppHandoverController {

    @Resource
    private EquipmentHandoverService handoverService;

    // ==================== 交接会话 ====================

    @PostMapping("/create")
    @Operation(summary = "创建交接会话")
    public CommonResult<Long> createHandover(@Valid @RequestBody AppHandoverCreateReqVO createReqVO) {
        return success(handoverService.createHandover(getLoginUserId(), createReqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获取交接会话详情")
    @Parameter(name = "id", description = "交接会话ID", required = true, example = "1024")
    public CommonResult<AppHandoverRespVO> getHandover(@RequestParam("id") Long id) {
        EquipmentHandoverDO handover = handoverService.getHandover(id);
        AppHandoverRespVO respVO = EquipmentHandoverConvert.INSTANCE.convert(handover);
        // 设置当前用户的未读数
        if (handover != null) {
            Long userId = getLoginUserId();
            if (userId.equals(handover.getLenderUserId())) {
                respVO.setUnreadCount(handover.getLenderUnreadCount());
            } else if (userId.equals(handover.getBorrowerUserId())) {
                respVO.setUnreadCount(handover.getBorrowerUnreadCount());
            }
        }
        return success(respVO);
    }

    @GetMapping("/page")
    @Operation(summary = "获取我的交接会话分页")
    public CommonResult<PageResult<AppHandoverRespVO>> getHandoverPage(@Valid AppHandoverPageReqVO pageReqVO) {
        PageResult<EquipmentHandoverDO> pageResult = handoverService.getHandoverPage(getLoginUserId(), pageReqVO);
        return success(new PageResult<>(
                EquipmentHandoverConvert.INSTANCE.convertList(pageResult.getList()),
                pageResult.getTotal()));
    }

    @PutMapping("/confirm-receive")
    @Operation(summary = "确认收到器材（借用方操作）")
    @Parameter(name = "id", description = "交接会话ID", required = true, example = "1024")
    @Parameter(name = "condition", description = "器材状态描述", example = "器材完好")
    public CommonResult<Boolean> confirmReceive(@RequestParam("id") Long id,
                                                 @RequestParam(value = "condition", required = false) String condition,
                                                 @RequestParam(value = "photos", required = false) List<String> photos) {
        handoverService.confirmReceive(getLoginUserId(), id, condition, photos);
        return success(true);
    }

    @PutMapping("/confirm-return")
    @Operation(summary = "确认收到归还的器材（出借方操作）")
    @Parameter(name = "id", description = "交接会话ID", required = true, example = "1024")
    @Parameter(name = "condition", description = "归还时器材状态", example = "器材完好")
    public CommonResult<Boolean> confirmReturn(@RequestParam("id") Long id,
                                                @RequestParam(value = "condition", required = false) String condition,
                                                @RequestParam(value = "photos", required = false) List<String> photos) {
        handoverService.confirmReturn(getLoginUserId(), id, condition, photos);
        return success(true);
    }

    @PutMapping("/verify")
    @Operation(summary = "验收器材")
    @Parameter(name = "id", description = "交接会话ID", required = true, example = "1024")
    @Parameter(name = "result", description = "验收结果：1-通过 2-有异常", required = true, example = "1")
    @Parameter(name = "remark", description = "验收备注", example = "器材完好")
    public CommonResult<Boolean> verifyEquipment(@RequestParam("id") Long id,
                                                  @RequestParam("result") Integer result,
                                                  @RequestParam(value = "remark", required = false) String remark) {
        handoverService.verifyEquipment(getLoginUserId(), id, result, remark);
        return success(true);
    }

    @PutMapping("/update-express")
    @Operation(summary = "更新快递信息")
    @Parameter(name = "id", description = "交接会话ID", required = true, example = "1024")
    @Parameter(name = "expressCompany", description = "快递公司", required = true, example = "顺丰速运")
    @Parameter(name = "expressNo", description = "快递单号", required = true, example = "SF1234567890")
    public CommonResult<Boolean> updateExpressInfo(@RequestParam("id") Long id,
                                                    @RequestParam("expressCompany") String expressCompany,
                                                    @RequestParam("expressNo") String expressNo) {
        handoverService.updateExpressInfo(id, expressCompany, expressNo);
        return success(true);
    }

    // ==================== 消息 ====================

    @PostMapping("/message/send")
    @Operation(summary = "发送消息")
    public CommonResult<Long> sendMessage(@Valid @RequestBody AppMessageSendReqVO sendReqVO) {
        return success(handoverService.sendMessage(getLoginUserId(), sendReqVO));
    }

    @GetMapping("/message/list")
    @Operation(summary = "获取消息列表")
    @Parameter(name = "handoverId", description = "交接会话ID", required = true, example = "1024")
    public CommonResult<List<AppMessageRespVO>> getMessageList(@RequestParam("handoverId") Long handoverId) {
        List<EquipmentHandoverMessageDO> list = handoverService.getMessageList(handoverId);
        List<AppMessageRespVO> respList = EquipmentHandoverConvert.INSTANCE.convertMessageList(list);
        
        // 标记是否是自己发送的消息
        Long currentUserId = getLoginUserId();
        respList.forEach(msg -> msg.setIsSelf(currentUserId.equals(msg.getSenderUserId())));
        
        return success(respList);
    }

    @PutMapping("/message/read")
    @Operation(summary = "标记消息已读")
    @Parameter(name = "handoverId", description = "交接会话ID", required = true, example = "1024")
    public CommonResult<Boolean> markMessagesAsRead(@RequestParam("handoverId") Long handoverId) {
        handoverService.markMessagesAsRead(getLoginUserId(), handoverId);
        return success(true);
    }

    // ==================== 反馈/评价 ====================

    @PostMapping("/feedback/create")
    @Operation(summary = "创建反馈/评价")
    public CommonResult<Long> createFeedback(@Valid @RequestBody AppFeedbackCreateReqVO createReqVO) {
        return success(handoverService.createFeedback(getLoginUserId(), createReqVO));
    }

    @GetMapping("/feedback/list")
    @Operation(summary = "获取交接会话的反馈列表")
    @Parameter(name = "handoverId", description = "交接会话ID", required = true, example = "1024")
    public CommonResult<List<AppFeedbackRespVO>> getFeedbackList(@RequestParam("handoverId") Long handoverId) {
        List<EquipmentFeedbackDO> list = handoverService.getFeedbackList(handoverId);
        return success(EquipmentHandoverConvert.INSTANCE.convertFeedbackList(list));
    }

    @GetMapping("/feedback/my-received")
    @Operation(summary = "获取我收到的评价列表")
    public CommonResult<List<AppFeedbackRespVO>> getMyReceivedFeedback() {
        List<EquipmentFeedbackDO> list = handoverService.getUserReceivedFeedback(getLoginUserId());
        return success(EquipmentHandoverConvert.INSTANCE.convertFeedbackList(list));
    }

    @GetMapping("/feedback/average-rating")
    @Operation(summary = "获取用户平均评分")
    @Parameter(name = "userId", description = "用户ID", example = "1024")
    public CommonResult<Double> getUserAverageRating(@RequestParam(value = "userId", required = false) Long userId) {
        if (userId == null) {
            userId = getLoginUserId();
        }
        return success(handoverService.getUserAverageRating(userId));
    }

}


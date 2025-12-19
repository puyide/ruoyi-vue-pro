package cn.iocoder.yudao.module.trade.service.handover;

import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.trade.controller.admin.handover.vo.HandoverPageReqVO;
import cn.iocoder.yudao.module.trade.controller.app.handover.vo.*;
import cn.iocoder.yudao.module.trade.convert.handover.EquipmentHandoverConvert;
import cn.iocoder.yudao.module.trade.dal.dataobject.handover.EquipmentFeedbackDO;
import cn.iocoder.yudao.module.trade.dal.dataobject.handover.EquipmentHandoverDO;
import cn.iocoder.yudao.module.trade.dal.dataobject.handover.EquipmentHandoverMessageDO;
import cn.iocoder.yudao.module.trade.dal.mysql.handover.EquipmentFeedbackMapper;
import cn.iocoder.yudao.module.trade.dal.mysql.handover.EquipmentHandoverMapper;
import cn.iocoder.yudao.module.trade.dal.mysql.handover.EquipmentHandoverMessageMapper;
import cn.iocoder.yudao.module.trade.enums.handover.HandoverStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.trade.enums.ErrorCodeConstants.*;

/**
 * 器材交接会话 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
@Slf4j
public class EquipmentHandoverServiceImpl implements EquipmentHandoverService {

    @Resource
    private EquipmentHandoverMapper handoverMapper;

    @Resource
    private EquipmentHandoverMessageMapper messageMapper;

    @Resource
    private EquipmentFeedbackMapper feedbackMapper;

    // ==================== 交接会话 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createHandover(Long userId, AppHandoverCreateReqVO createReqVO) {
        // 1. 检查预约是否已有交接会话
        EquipmentHandoverDO existHandover = handoverMapper.selectByReservationId(createReqVO.getReservationId());
        if (existHandover != null) {
            throw exception(HANDOVER_EXISTS);
        }

        // 2. 创建交接会话
        EquipmentHandoverDO handover = EquipmentHandoverConvert.INSTANCE.convert(createReqVO);
        handover.setLenderUserId(userId);
        // borrowerUserId 应从预约中获取
        handover.setHandoverStatus(HandoverStatusEnum.PENDING.getStatus());
        handover.setLenderUnreadCount(0);
        handover.setBorrowerUnreadCount(0);
        handover.setLastMessageTime(LocalDateTime.now());
        
        handoverMapper.insert(handover);

        // 3. 发送系统消息
        sendSystemMessage(handover.getId(), "交接会话已创建，请双方协商具体交接细节");

        log.info("[createHandover][用户({})创建交接会话成功，ID:{}]", userId, handover.getId());
        return handover.getId();
    }

    @Override
    public EquipmentHandoverDO getHandover(Long handoverId) {
        return handoverMapper.selectById(handoverId);
    }

    @Override
    public PageResult<EquipmentHandoverDO> getHandoverPage(Long userId, AppHandoverPageReqVO pageReqVO) {
        String roleType = pageReqVO.getRoleType();
        if ("lender".equals(roleType)) {
            return handoverMapper.selectLenderPage(userId, pageReqVO);
        } else if ("borrower".equals(roleType)) {
            return handoverMapper.selectBorrowerPage(userId, pageReqVO);
        }
        return handoverMapper.selectPage(userId, pageReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateHandoverStatus(Long handoverId, Integer status) {
        EquipmentHandoverDO handover = handoverMapper.selectById(handoverId);
        if (handover == null) {
            throw exception(HANDOVER_NOT_EXISTS);
        }
        
        handover.setHandoverStatus(status);
        handoverMapper.updateById(handover);

        // 发送状态变更系统消息
        HandoverStatusEnum statusEnum = HandoverStatusEnum.valueOf(status);
        String statusName = statusEnum != null ? statusEnum.getName() : "未知";
        sendSystemMessage(handoverId, "交接状态已更新为：" + statusName);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmReceive(Long userId, Long handoverId, String condition, List<String> photos) {
        EquipmentHandoverDO handover = handoverMapper.selectById(handoverId);
        if (handover == null) {
            throw exception(HANDOVER_NOT_EXISTS);
        }
        
        // 校验是否是借用方
        if (!userId.equals(handover.getBorrowerUserId())) {
            throw exception(HANDOVER_NOT_BORROWER);
        }

        // 更新交接信息
        handover.setActualHandoverTime(LocalDateTime.now());
        handover.setEquipmentCondition(condition);
        handover.setEquipmentPhotos(photos != null ? JSONUtil.toJsonStr(photos) : null);
        handover.setHandoverStatus(HandoverStatusEnum.IN_PROGRESS.getStatus());
        handover.setBorrowerVerifyResult(0); // 待验收
        handoverMapper.updateById(handover);

        sendSystemMessage(handoverId, "借用方已确认收到器材");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmReturn(Long userId, Long handoverId, String condition, List<String> photos) {
        EquipmentHandoverDO handover = handoverMapper.selectById(handoverId);
        if (handover == null) {
            throw exception(HANDOVER_NOT_EXISTS);
        }

        // 校验是否是出借方
        if (!userId.equals(handover.getLenderUserId())) {
            throw exception(HANDOVER_NOT_LENDER);
        }

        // 更新归还信息
        handover.setReturnTime(LocalDateTime.now());
        handover.setReturnCondition(condition);
        handover.setReturnPhotos(photos != null ? JSONUtil.toJsonStr(photos) : null);
        handover.setLenderVerifyResult(0); // 待验收
        handoverMapper.updateById(handover);

        sendSystemMessage(handoverId, "出借方已确认收到归还的器材");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void verifyEquipment(Long userId, Long handoverId, Integer result, String remark) {
        EquipmentHandoverDO handover = handoverMapper.selectById(handoverId);
        if (handover == null) {
            throw exception(HANDOVER_NOT_EXISTS);
        }

        boolean isLender = userId.equals(handover.getLenderUserId());
        boolean isBorrower = userId.equals(handover.getBorrowerUserId());
        
        if (!isLender && !isBorrower) {
            throw exception(HANDOVER_NOT_PARTICIPANT);
        }

        // 更新验收结果
        if (isLender) {
            handover.setLenderVerifyResult(result);
            handover.setLenderVerifyRemark(remark);
        } else {
            handover.setBorrowerVerifyResult(result);
            handover.setBorrowerVerifyRemark(remark);
        }

        // 如果双方都验收通过，则完成交接
        if (handover.getLenderVerifyResult() != null && handover.getLenderVerifyResult() == 1
                && handover.getBorrowerVerifyResult() != null && handover.getBorrowerVerifyResult() == 1) {
            handover.setHandoverStatus(HandoverStatusEnum.COMPLETED.getStatus());
            sendSystemMessage(handoverId, "双方验收通过，交接已完成！感谢您的互助精神！");
        }

        handoverMapper.updateById(handover);

        String verifyResult = result == 1 ? "通过" : "有异常";
        String role = isLender ? "出借方" : "借用方";
        sendSystemMessage(handoverId, role + "验收结果：" + verifyResult + (remark != null ? "，备注：" + remark : ""));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateExpressInfo(Long handoverId, String expressCompany, String expressNo) {
        EquipmentHandoverDO handover = handoverMapper.selectById(handoverId);
        if (handover == null) {
            throw exception(HANDOVER_NOT_EXISTS);
        }
        
        handover.setExpressCompany(expressCompany);
        handover.setExpressNo(expressNo);
        handover.setExpressStatus(1); // 已发货
        handoverMapper.updateById(handover);

        sendSystemMessage(handoverId, "快递已发出，" + expressCompany + "：" + expressNo);
    }

    // ==================== 消息 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long sendMessage(Long userId, AppMessageSendReqVO sendReqVO) {
        EquipmentHandoverDO handover = handoverMapper.selectById(sendReqVO.getHandoverId());
        if (handover == null) {
            throw exception(HANDOVER_NOT_EXISTS);
        }

        // 校验用户是否是会话参与方
        if (!userId.equals(handover.getLenderUserId()) && !userId.equals(handover.getBorrowerUserId())) {
            throw exception(HANDOVER_NOT_PARTICIPANT);
        }

        // 创建消息
        EquipmentHandoverMessageDO message = new EquipmentHandoverMessageDO();
        message.setHandoverId(sendReqVO.getHandoverId());
        message.setSenderUserId(userId);
        message.setMessageType(sendReqVO.getMessageType());
        message.setContent(sendReqVO.getContent());
        message.setIsRead(false);
        message.setIsSensitive(false); // TODO: 敏感词检测

        // 处理媒体URL
        if (sendReqVO.getMediaUrls() != null && !sendReqVO.getMediaUrls().isEmpty()) {
            message.setMediaUrls(JSONUtil.toJsonStr(sendReqVO.getMediaUrls()));
        }

        // 处理位置信息
        if (sendReqVO.getLongitude() != null && sendReqVO.getLatitude() != null) {
            Map<String, Object> locationInfo = new HashMap<>();
            locationInfo.put("longitude", sendReqVO.getLongitude());
            locationInfo.put("latitude", sendReqVO.getLatitude());
            locationInfo.put("name", sendReqVO.getLocationName());
            message.setLocationInfo(JSONUtil.toJsonStr(locationInfo));
        }

        messageMapper.insert(message);

        // 更新会话的最后消息时间和未读数
        handover.setLastMessageTime(LocalDateTime.now());
        if (userId.equals(handover.getLenderUserId())) {
            handover.setBorrowerUnreadCount(handover.getBorrowerUnreadCount() + 1);
        } else {
            handover.setLenderUnreadCount(handover.getLenderUnreadCount() + 1);
        }
        handoverMapper.updateById(handover);

        return message.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long sendSystemMessage(Long handoverId, String content) {
        EquipmentHandoverMessageDO message = new EquipmentHandoverMessageDO();
        message.setHandoverId(handoverId);
        message.setSenderUserId(0L); // 0表示系统消息
        message.setMessageType(1); // 系统消息
        message.setContent(content);
        message.setIsRead(false);
        message.setIsSensitive(false);

        messageMapper.insert(message);

        // 更新会话的最后消息时间
        EquipmentHandoverDO handover = handoverMapper.selectById(handoverId);
        if (handover != null) {
            handover.setLastMessageTime(LocalDateTime.now());
            handover.setLenderUnreadCount(handover.getLenderUnreadCount() + 1);
            handover.setBorrowerUnreadCount(handover.getBorrowerUnreadCount() + 1);
            handoverMapper.updateById(handover);
        }

        return message.getId();
    }

    @Override
    public List<EquipmentHandoverMessageDO> getMessageList(Long handoverId) {
        return messageMapper.selectListByHandoverId(handoverId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markMessagesAsRead(Long userId, Long handoverId) {
        EquipmentHandoverDO handover = handoverMapper.selectById(handoverId);
        if (handover == null) {
            return;
        }

        // 标记消息为已读
        messageMapper.markAsRead(handoverId, userId);

        // 重置未读数
        if (userId.equals(handover.getLenderUserId())) {
            handover.setLenderUnreadCount(0);
        } else if (userId.equals(handover.getBorrowerUserId())) {
            handover.setBorrowerUnreadCount(0);
        }
        handoverMapper.updateById(handover);
    }

    // ==================== 反馈/评价 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createFeedback(Long userId, AppFeedbackCreateReqVO createReqVO) {
        EquipmentHandoverDO handover = handoverMapper.selectById(createReqVO.getHandoverId());
        if (handover == null) {
            throw exception(HANDOVER_NOT_EXISTS);
        }

        // 校验用户是否是会话参与方
        boolean isLender = userId.equals(handover.getLenderUserId());
        boolean isBorrower = userId.equals(handover.getBorrowerUserId());
        if (!isLender && !isBorrower) {
            throw exception(HANDOVER_NOT_PARTICIPANT);
        }

        // 检查是否已评价
        EquipmentFeedbackDO existFeedback = feedbackMapper.selectByHandoverIdAndFromUserId(
                createReqVO.getHandoverId(), userId);
        if (existFeedback != null) {
            throw exception(FEEDBACK_EXISTS);
        }

        // 确定被评价者
        Long toUserId = isLender ? handover.getBorrowerUserId() : handover.getLenderUserId();

        // 创建反馈
        EquipmentFeedbackDO feedback = new EquipmentFeedbackDO();
        feedback.setHandoverId(createReqVO.getHandoverId());
        feedback.setFeedbackType(createReqVO.getFeedbackType());
        feedback.setFromUserId(userId);
        feedback.setToUserId(toUserId);
        feedback.setRating(createReqVO.getRating());
        feedback.setContent(createReqVO.getContent());
        feedback.setTags(createReqVO.getTags() != null ? JSONUtil.toJsonStr(createReqVO.getTags()) : null);
        feedback.setIsAnonymous(createReqVO.getIsAnonymous() != null ? createReqVO.getIsAnonymous() : false);

        feedbackMapper.insert(feedback);

        // 发送系统消息
        String feedbackTypeName = createReqVO.getFeedbackType() == 1 ? "感谢" : "评价";
        sendSystemMessage(createReqVO.getHandoverId(), "收到一条新的" + feedbackTypeName);

        log.info("[createFeedback][用户({})创建反馈成功，ID:{}]", userId, feedback.getId());
        return feedback.getId();
    }

    @Override
    public List<EquipmentFeedbackDO> getFeedbackList(Long handoverId) {
        return feedbackMapper.selectListByHandoverId(handoverId);
    }

    @Override
    public List<EquipmentFeedbackDO> getUserReceivedFeedback(Long userId) {
        return feedbackMapper.selectListByToUserId(userId);
    }

    @Override
    public Double getUserAverageRating(Long userId) {
        return feedbackMapper.selectAvgRatingByToUserId(userId);
    }

    // ==================== 管理后台 ====================

    @Override
    public PageResult<EquipmentHandoverDO> getHandoverPage(HandoverPageReqVO pageReqVO) {
        return handoverMapper.selectPage(pageReqVO, new LambdaQueryWrapperX<EquipmentHandoverDO>()
                .eqIfPresent(EquipmentHandoverDO::getHandoverStatus, pageReqVO.getHandoverStatus())
                .eqIfPresent(EquipmentHandoverDO::getHandoverMode, pageReqVO.getHandoverMode())
                .betweenIfPresent(EquipmentHandoverDO::getCreateTime, pageReqVO.getCreateTime())
                .orderByDesc(EquipmentHandoverDO::getId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendAdminMessage(Long handoverId, String content) {
        EquipmentHandoverDO handover = handoverMapper.selectById(handoverId);
        if (handover == null) {
            throw exception(HANDOVER_NOT_EXISTS);
        }

        // 发送管理员系统消息
        sendSystemMessage(handoverId, "[管理员消息] " + content);
        
        log.info("[sendAdminMessage][管理员发送消息，交接会话ID:{}]", handoverId);
    }

}


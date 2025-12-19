package cn.iocoder.yudao.module.trade.service.handover;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.trade.controller.admin.handover.vo.HandoverPageReqVO;
import cn.iocoder.yudao.module.trade.controller.app.handover.vo.*;
import cn.iocoder.yudao.module.trade.dal.dataobject.handover.EquipmentFeedbackDO;
import cn.iocoder.yudao.module.trade.dal.dataobject.handover.EquipmentHandoverDO;
import cn.iocoder.yudao.module.trade.dal.dataobject.handover.EquipmentHandoverMessageDO;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 器材交接会话 Service 接口
 *
 * @author 芋道源码
 */
public interface EquipmentHandoverService {

    // ==================== 交接会话 ====================

    /**
     * 创建交接会话
     *
     * @param userId 用户ID（出借方）
     * @param createReqVO 创建信息
     * @return 交接会话ID
     */
    Long createHandover(Long userId, @Valid AppHandoverCreateReqVO createReqVO);

    /**
     * 获取交接会话详情
     *
     * @param handoverId 交接会话ID
     * @return 交接会话信息
     */
    EquipmentHandoverDO getHandover(Long handoverId);

    /**
     * 获取用户参与的交接会话列表
     *
     * @param userId 用户ID
     * @param pageReqVO 分页查询条件
     * @return 分页结果
     */
    PageResult<EquipmentHandoverDO> getHandoverPage(Long userId, AppHandoverPageReqVO pageReqVO);

    /**
     * 更新交接状态
     *
     * @param handoverId 交接会话ID
     * @param status 新状态
     */
    void updateHandoverStatus(Long handoverId, Integer status);

    /**
     * 确认交接（借用方确认收到器材）
     *
     * @param userId 用户ID（借用方）
     * @param handoverId 交接会话ID
     * @param condition 器材状态描述
     * @param photos 器材照片
     */
    void confirmReceive(Long userId, Long handoverId, String condition, List<String> photos);

    /**
     * 确认归还（出借方确认收到归还的器材）
     *
     * @param userId 用户ID（出借方）
     * @param handoverId 交接会话ID
     * @param condition 归还时器材状态
     * @param photos 归还时照片
     */
    void confirmReturn(Long userId, Long handoverId, String condition, List<String> photos);

    /**
     * 验收器材（双方验收）
     *
     * @param userId 用户ID
     * @param handoverId 交接会话ID
     * @param result 验收结果：1-通过 2-有异常
     * @param remark 验收备注
     */
    void verifyEquipment(Long userId, Long handoverId, Integer result, String remark);

    /**
     * 更新快递信息
     *
     * @param handoverId 交接会话ID
     * @param expressCompany 快递公司
     * @param expressNo 快递单号
     */
    void updateExpressInfo(Long handoverId, String expressCompany, String expressNo);

    // ==================== 消息 ====================

    /**
     * 发送消息
     *
     * @param userId 发送者用户ID
     * @param sendReqVO 消息内容
     * @return 消息ID
     */
    Long sendMessage(Long userId, @Valid AppMessageSendReqVO sendReqVO);

    /**
     * 发送系统消息
     *
     * @param handoverId 交接会话ID
     * @param content 消息内容
     * @return 消息ID
     */
    Long sendSystemMessage(Long handoverId, String content);

    /**
     * 获取消息列表
     *
     * @param handoverId 交接会话ID
     * @return 消息列表
     */
    List<EquipmentHandoverMessageDO> getMessageList(Long handoverId);

    /**
     * 标记消息已读
     *
     * @param userId 用户ID
     * @param handoverId 交接会话ID
     */
    void markMessagesAsRead(Long userId, Long handoverId);

    // ==================== 反馈/评价 ====================

    /**
     * 创建反馈/评价
     *
     * @param userId 用户ID（反馈者）
     * @param createReqVO 创建信息
     * @return 反馈ID
     */
    Long createFeedback(Long userId, @Valid AppFeedbackCreateReqVO createReqVO);

    /**
     * 获取交接会话的反馈列表
     *
     * @param handoverId 交接会话ID
     * @return 反馈列表
     */
    List<EquipmentFeedbackDO> getFeedbackList(Long handoverId);

    /**
     * 获取用户收到的评价列表
     *
     * @param userId 用户ID
     * @return 评价列表
     */
    List<EquipmentFeedbackDO> getUserReceivedFeedback(Long userId);

    /**
     * 获取用户的平均评分
     *
     * @param userId 用户ID
     * @return 平均评分
     */
    Double getUserAverageRating(Long userId);

    // ==================== 管理后台 ====================

    /**
     * 获取交接会话分页（管理后台）
     *
     * @param pageReqVO 分页查询条件
     * @return 分页结果
     */
    PageResult<EquipmentHandoverDO> getHandoverPage(HandoverPageReqVO pageReqVO);

    /**
     * 发送管理员消息（系统消息）
     *
     * @param handoverId 交接会话ID
     * @param content 消息内容
     */
    void sendAdminMessage(Long handoverId, String content);

}


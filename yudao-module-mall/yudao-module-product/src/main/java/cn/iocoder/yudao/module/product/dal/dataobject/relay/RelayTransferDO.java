package cn.iocoder.yudao.module.product.dal.dataobject.relay;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 接力交接记录 DO
 *
 * @author 芋道源码
 */
@TableName("relay_transfer")
@KeySequence("relay_transfer_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelayTransferDO extends BaseDO {

    /**
     * 交接ID
     */
    @TableId
    private Long id;

    /**
     * 好物ID
     */
    private Long itemId;

    /**
     * 关联申请ID
     */
    private Long requestId;

    /**
     * 出借/赠送方用户ID
     */
    private Long giverId;

    /**
     * 接收方用户ID
     */
    private Long receiverId;

    /**
     * 交接方式
     * 1-当面交接 2-快递
     */
    private Integer transferMethod;

    /**
     * 快递公司
     */
    private String expressCompany;

    /**
     * 快递单号
     */
    private String expressNo;

    /**
     * 交接时间
     */
    private LocalDateTime transferTime;

    /**
     * 出借方是否确认
     */
    private Boolean giverConfirm;

    /**
     * 接收方是否确认
     */
    private Boolean receiverConfirm;

    /**
     * 状态
     * 0-待交接 1-交接中 2-已完成
     */
    private Integer status;

    /**
     * 是否需要归还(借用模式)
     */
    private Boolean returnRequired;

    /**
     * 预计归还日期
     */
    private LocalDate returnDate;

    /**
     * 归还状态
     * 0-未归还 1-已归还 2-逾期
     */
    private Integer returnStatus;

    // ==================== 低价转让-支付凭证相关 ====================

    /**
     * 转让价格(分)
     */
    private Integer transferPrice;

    /**
     * 支付凭证图片(JSON数组)
     */
    private String paymentProofPhotos;

    /**
     * 支付凭证上传时间
     */
    private LocalDateTime paymentProofTime;

    /**
     * 卖家是否确认收款
     */
    private Boolean paymentConfirmed;

    /**
     * 确认收款时间
     */
    private LocalDateTime paymentConfirmTime;

    /**
     * 支付备注
     */
    private String paymentRemark;

    /**
     * 支付状态
     * 0-待支付 1-已上传凭证 2-卖家已确认 3-有争议
     *
     * @see cn.iocoder.yudao.module.product.enums.relay.PaymentStatusEnum
     */
    private Integer paymentStatus;

}


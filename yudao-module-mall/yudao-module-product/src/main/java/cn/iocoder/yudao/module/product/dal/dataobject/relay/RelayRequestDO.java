package cn.iocoder.yudao.module.product.dal.dataobject.relay;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 接力申请 DO
 *
 * @author 芋道源码
 */
@TableName("relay_request")
@KeySequence("relay_request_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelayRequestDO extends BaseDO {

    /**
     * 申请ID
     */
    @TableId
    private Long id;

    /**
     * 好物ID
     */
    private Long itemId;

    /**
     * 申请者用户ID
     */
    private Long requesterId;

    /**
     * 发布者用户ID
     */
    private Long giverId;

    /**
     * 申请留言
     */
    private String message;

    /**
     * 状态
     * 0-待处理 1-已同意 2-已拒绝 3-已取消 4-已完成
     *
     * @see cn.iocoder.yudao.module.product.enums.relay.RelayRequestStatusEnum
     */
    private Integer status;

    /**
     * 申请者微信号
     */
    private String requesterWechat;

    /**
     * 发布者微信号
     */
    private String giverWechat;

    /**
     * 是否已交换联系方式
     */
    private Boolean contactExchanged;

    /**
     * 同意时间
     */
    private LocalDateTime acceptTime;

    /**
     * 拒绝原因
     */
    private String rejectReason;

}


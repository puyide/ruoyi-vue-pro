package cn.iocoder.yudao.module.product.dal.dataobject.relay;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 接力好物 DO
 *
 * @author 芋道源码
 */
@TableName("relay_item")
@KeySequence("relay_item_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelayItemDO extends BaseDO {

    /**
     * 好物ID
     */
    @TableId
    private Long id;

    /**
     * 发布者用户ID
     */
    private Long userId;

    /**
     * 好物名称
     */
    private String title;

    /**
     * 物品说明
     */
    private String description;

    /**
     * 照片列表(JSON数组)
     */
    private String photos;

    /**
     * 新旧程度
     * brand_new-全新, 95_new-95成新, 9_new-9成新, 8_new-8成新, 7_new-7成新及以下
     */
    private String condition;

    /**
     * 好物类型
     * language-语言训练, sensory-感统训练, attention-注意力, 
     * social-社交, emotion-情绪管理, other-其他
     */
    private String type;

    /**
     * 适用年龄
     * 0-3, 3-6, 6-12, all
     */
    private String ageGroup;

    /**
     * 交易方式
     * 1-赠送 2-借用 3-低价转让
     *
     * @see cn.iocoder.yudao.module.product.enums.relay.RelayMethodEnum
     */
    private Integer relayMethod;

    /**
     * 交付方式
     * 1-当面交接 2-快递寄送
     *
     * @see cn.iocoder.yudao.module.product.enums.relay.DeliveryMethodEnum
     */
    private Integer deliveryMethod;

    /**
     * 价格(分)，仅低价转让时有效
     */
    private Integer price;

    /**
     * 状态
     * 0-待审核 1-接力中 2-已匹配 3-交接中 4-已完成 5-审核拒绝 6-已下架
     *
     * @see cn.iocoder.yudao.module.product.enums.relay.RelayItemStatusEnum
     */
    private Integer status;

    /**
     * 浏览次数
     */
    private Integer viewCount;

    /**
     * 申请次数
     */
    private Integer requestCount;

    /**
     * 匹配的用户ID
     */
    private Long matchedUserId;

    /**
     * 匹配时间
     */
    private LocalDateTime matchedTime;

    /**
     * 完成时间
     */
    private LocalDateTime completedTime;

    /**
     * 审核拒绝原因
     */
    private String rejectReason;

}


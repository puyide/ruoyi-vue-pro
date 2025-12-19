package cn.iocoder.yudao.module.product.dal.dataobject.relay;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 接力感谢/评价 DO
 *
 * @author 芋道源码
 */
@TableName("relay_thank")
@KeySequence("relay_thank_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelayThankDO extends BaseDO {

    /**
     * 感谢ID
     */
    @TableId
    private Long id;

    /**
     * 好物ID
     */
    private Long itemId;

    /**
     * 交接记录ID
     */
    private Long transferId;

    /**
     * 发送者用户ID
     */
    private Long fromUserId;

    /**
     * 接收者用户ID
     */
    private Long toUserId;

    /**
     * 感谢内容
     */
    private String content;

    /**
     * 是否公开显示
     */
    private Boolean isPublic;

}


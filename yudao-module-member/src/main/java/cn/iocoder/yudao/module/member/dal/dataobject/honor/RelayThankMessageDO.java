package cn.iocoder.yudao.module.member.dal.dataobject.honor;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 感谢私信 DO
 * 
 * 不公开，不精选
 *
 * @author 星语家园
 */
@TableName("relay_thank_message")
@KeySequence("relay_thank_message_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelayThankMessageDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;

    /**
     * 发送者用户ID
     */
    private Long fromUserId;

    /**
     * 接收者用户ID
     */
    private Long toUserId;

    /**
     * 关联接力记忆ID
     */
    private Long relayMemoryId;

    /**
     * 物品ID
     */
    private Long itemId;

    /**
     * 感谢内容
     */
    private String message;

    /**
     * 是否已读
     */
    private Boolean isRead;
}


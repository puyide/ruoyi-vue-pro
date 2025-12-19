package cn.iocoder.yudao.module.member.dal.dataobject.honor;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 接力记忆 DO
 * 
 * 不统计"帮助了多少人"，只记录"发生过"
 *
 * @author 星语家园
 */
@TableName("relay_memory")
@KeySequence("relay_memory_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelayMemoryDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;

    /**
     * 物品ID
     */
    private Long itemId;

    /**
     * 原始物品ID（追溯第一位捐赠者）
     */
    private Long originalItemId;

    /**
     * 传递者用户ID
     */
    private Long fromUserId;

    /**
     * 接收者用户ID
     */
    private Long toUserId;

    /**
     * 接力序号（第几棒）
     */
    private Integer sequenceNum;

    /**
     * 那一刻的心情
     */
    private String momentMessage;
}


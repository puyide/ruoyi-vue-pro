package cn.iocoder.yudao.module.member.dal.dataobject.honor;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 勋章定义 DO
 * 
 * 设计理念：叙事型勋章，只确认"发生过"，不评价"好不好"
 *
 * @author 星语家园
 */
@TableName("member_badge_definition")
@KeySequence("member_badge_definition_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BadgeDefinitionDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;

    /**
     * 勋章编码
     */
    private String code;

    /**
     * 勋章名称（叙事型）
     */
    private String name;

    /**
     * 触发时的温暖文案
     */
    private String description;

    /**
     * 图标（emoji或图片URL）
     */
    private String icon;

    /**
     * 类别
     * 
     * @see cn.iocoder.yudao.module.member.enums.honor.BadgeCategoryEnum
     */
    private String category;

    /**
     * 触发事件类型
     * 
     * @see cn.iocoder.yudao.module.member.enums.honor.BehaviorEventTypeEnum
     */
    private String triggerEvent;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态 0-启用 1-禁用
     */
    private Integer status;
}


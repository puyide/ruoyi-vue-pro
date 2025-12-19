package cn.iocoder.yudao.module.member.dal.dataobject.training;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 训练模板 DO
 * 
 * 存储 60 个标准化训练模板元数据
 * 模板是训练的骨架，AI 根据模板生成个性化训练卡片
 *
 * @author 芋道源码
 */
@TableName("training_template")
@KeySequence("training_template_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainingTemplateDO extends BaseDO {

    /**
     * 模板ID
     */
    @TableId
    private Long id;

    /**
     * 模板编码
     * 例如: S01, L03, E05
     * S=社交, L=语言, E=情绪, C=认知, G=感统, D=生活
     */
    private String code;

    /**
     * 训练域
     * @see cn.iocoder.yudao.module.member.enums.training.TrainingDomainEnum
     */
    private String domain;

    /**
     * 训练名称
     * 例如: 回应名字、共同注意：跟随指物
     */
    private String name;

    /**
     * 简要目标（一句话）
     * 例如: 当家长叫名字时，孩子能有转头、停顿或看向家长的反应。
     */
    private String briefGoal;

    /**
     * 推荐训练频率
     * 例如: 每天 5-10 次，穿插在日常生活中
     */
    private String recommendedFrequency;

    /**
     * 基础场景描述
     * 给 AI 的参考，用于生成具体训练内容
     */
    private String baseScenario;

    /**
     * Level 1 规则（JSON）
     * 引导型训练的具体规则
     */
    private String level1Rules;

    /**
     * Level 2 规则（JSON）
     * 部分独立型训练的具体规则
     */
    private String level2Rules;

    /**
     * Level 3 规则（JSON）
     * 泛化挑战型训练的具体规则
     */
    private String level3Rules;

    /**
     * 额外元数据（JSON）
     * 预留扩展字段
     */
    private String extraMeta;

    /**
     * 是否启用
     */
    private Boolean isActive;

    /**
     * 排序
     */
    private Integer sort;

}


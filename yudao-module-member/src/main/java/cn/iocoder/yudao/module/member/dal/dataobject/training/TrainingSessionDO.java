package cn.iocoder.yudao.module.member.dal.dataobject.training;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 训练会话 DO
 * 
 * AI 实例化出的"个性化训练卡片"
 * 每次根据模板 + 孩子信息 + 难度生成
 *
 * @author 芋道源码
 */
@TableName("training_session")
@KeySequence("training_session_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainingSessionDO extends BaseDO {

    /**
     * 会话ID
     */
    @TableId
    private Long id;

    /**
     * 孩子ID
     * 关联 member_children.id
     */
    private Long childId;

    /**
     * 用户ID（家长）
     * 关联 member_user.id
     */
    private Long userId;

    /**
     * 模板ID
     * 关联 training_template.id
     */
    private Long templateId;

    /**
     * 计划训练时间
     */
    private LocalDateTime scheduledAt;

    /**
     * 难度等级 1/2/3
     * @see cn.iocoder.yudao.module.member.enums.training.TrainingDifficultyLevelEnum
     */
    private Integer difficultyLevel;

    /**
     * 传给大模型的输入 JSON
     * 包含: child_profile, environment, template, difficulty_level
     */
    private String aiInputJson;

    /**
     * 大模型生成的训练卡片 JSON
     * 包含: title, domain, goal_explained, materials, steps,
     *       parent_script, success_criteria, reinforcement, 
     *       common_pitfalls, estimated_duration_minutes, level_hint
     */
    private String aiOutputJson;

    /**
     * 会话状态
     * 0-已计划 1-进行中 2-已完成 3-已跳过
     * @see cn.iocoder.yudao.module.member.enums.training.TrainingSessionStatusEnum
     */
    private Integer status;

    /**
     * 开始时间
     */
    private LocalDateTime startedAt;

    /**
     * 完成时间
     */
    private LocalDateTime completedAt;

    /**
     * 实际训练时长（分钟）
     */
    private Integer actualDurationMinutes;

}


package cn.iocoder.yudao.module.member.dal.dataobject.training;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 训练日志 DO
 * 
 * 家长打卡记录 & 训练结果数据
 *
 * @author 芋道源码
 */
@TableName("training_log")
@KeySequence("training_log_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainingLogDO extends BaseDO {

    /**
     * 日志ID
     */
    @TableId
    private Long id;

    /**
     * 会话ID
     * 关联 training_session.id
     */
    private Long sessionId;

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
     * 冗余字段，便于统计
     */
    private Long templateId;

    /**
     * 训练域
     * 冗余字段，便于统计
     */
    private String domain;

    /**
     * 打卡时间
     */
    private LocalDateTime doneAt;

    /**
     * 是否完成
     * true-大致完成, false-中途结束
     */
    private Boolean completed;

    /**
     * 孩子情绪状态
     * 1-还不错 2-一般 3-情绪不好
     * @see cn.iocoder.yudao.module.member.enums.training.ChildMoodEnum
     */
    private Integer childMood;

    /**
     * 成功次数
     * 例如: 呼唤名字，有几次成功回应
     */
    private Integer successCount;

    /**
     * 遇到的困难标签（JSON数组）
     * 例如: ["环境太吵", "孩子不看我", "容易分心"]
     */
    private String difficulties;

    /**
     * 家长备注
     */
    private String parentComment;

    /**
     * 结构化数据（JSON）
     * 记录一些可量化的指标，例如: {"max_wait_sec": 8, "attention_span": 5}
     */
    private String structuredData;

    /**
     * 是否分享到社区
     */
    private Boolean sharedToCommunity;

    /**
     * AI 反馈内容（JSON）
     * 包含: affirmation, summary, next_suggestion
     */
    private String aiFeedback;

}


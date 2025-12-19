package cn.iocoder.yudao.module.member.dal.dataobject.children;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 会员模块 - 儿童个人信息 DO
 *
 * @author 芋道源码
 */
@TableName("member_children")
@KeySequence("member_children_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberChildrenDO extends BaseDO {

    /**
     * 自增主键
     */
    @TableId
    private Long id;

    /**
     * 关联用户ID（监护人）
     *
     * 关联 member_user.id
     */
    private Long userId;

    /**
     * 孩子昵称
     */
    private String nickname;

    /**
     * 孩子头像地址
     */
    private String avatarUrl;

    /**
     * 性别：male-男性，female-女性，unknown-未知
     */
    private String gender;

    /**
     * 实际年龄（岁）
     */
    private Integer ageYears;

    /**
     * 年龄组：0-3 / 3-6 / 6-12 / 12+
     */
    private String ageGroup;

    /**
     * 诊断日期
     */
    private LocalDateTime diagnosisDate;

    /**
     * 监护人关系：father-父亲，mother-母亲，grandfather-祖父，grandmother-祖母，other-其他
     */
    private String guardianRelation;

    /**
     * 状态标签（JSON数组）
     * 例如：["LANGUAGE_DELAY", "SOCIAL_DIFFICULTY", "SENSORY_SENSITIVITY", "REPETITIVE_BEHAVIOR"]
     */
    private String statusTags;

    /**
     * 目标标签（JSON数组）
     * 例如：["LANGUAGE_MIMIC", "KINDERGARTEN_PREP", "SOCIAL_SKILLS", "DAILY_LIVING"]
     */
    private String goalTags;

    /**
     * 备注说明
     */
    private String description;

}


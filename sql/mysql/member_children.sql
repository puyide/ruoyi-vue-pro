-- ----------------------------
-- Table structure for member_children
-- 会员模块 - 儿童个人信息表（自闭症互助平台）
-- ----------------------------
CREATE TABLE IF NOT EXISTS `member_children` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `user_id` bigint NOT NULL COMMENT '关联用户ID（监护人）',
    `nickname` varchar(50) NOT NULL DEFAULT '' COMMENT '孩子昵称',
    `avatar_url` varchar(512) DEFAULT '' COMMENT '孩子头像地址',
    `gender` varchar(10) NOT NULL DEFAULT 'unknown' COMMENT '性别：male-男性，female-女性，unknown-未知',
    `age_years` tinyint DEFAULT NULL COMMENT '实际年龄（岁）',
    `age_group` varchar(20) DEFAULT '' COMMENT '年龄组：0-3 / 3-6 / 6-12 / 12+',
    `diagnosis_date` datetime DEFAULT NULL COMMENT '诊断日期',
    `guardian_relation` varchar(20) DEFAULT '' COMMENT '监护人关系：father-父亲，mother-母亲，grandfather-祖父，grandmother-祖母，other-其他',
    `status_tags` varchar(2000) DEFAULT '[]' COMMENT '状态标签（JSON数组）：例如 ["LANGUAGE_DELAY", "SOCIAL_DIFFICULTY", "SENSORY_SENSITIVITY", "REPETITIVE_BEHAVIOR"]',
    `goal_tags` varchar(2000) DEFAULT '[]' COMMENT '目标标签（JSON数组）：例如 ["LANGUAGE_MIMIC", "KINDERGARTEN_PREP", "SOCIAL_SKILLS", "DAILY_LIVING"]',
    `description` varchar(500) DEFAULT '' COMMENT '备注说明',
    `creator` varchar(64) DEFAULT '' COMMENT '创建者',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` varchar(64) DEFAULT '' COMMENT '更新者',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除：0-未删除，1-已删除',
    `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`, `deleted`) USING BTREE COMMENT '用户ID索引',
    KEY `idx_age_group` (`age_group`) USING BTREE COMMENT '年龄组索引',
    KEY `idx_create_time` (`create_time`) USING BTREE COMMENT '创建时间索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会员模块 - 儿童个人信息表';


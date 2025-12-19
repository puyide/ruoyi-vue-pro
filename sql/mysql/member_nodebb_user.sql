-- ----------------------------
-- Table structure for member_nodebb_user
-- 会员用户 - NodeBB 用户映射表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `member_nodebb_user` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `user_id` bigint NOT NULL COMMENT '会员用户 ID',
    `nodebb_uid` int DEFAULT NULL COMMENT 'NodeBB 用户 UID',
    `nodebb_username` varchar(255) DEFAULT NULL COMMENT 'NodeBB 用户名',
    `sync_status` tinyint NOT NULL DEFAULT 0 COMMENT '同步状态：1-成功，0-失败',
    `sync_error_msg` varchar(500) DEFAULT NULL COMMENT '同步失败原因',
    `creator` varchar(64) DEFAULT '' COMMENT '创建者',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` varchar(64) DEFAULT '' COMMENT '更新者',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uk_user_id` (`user_id`, `deleted`) USING BTREE COMMENT '会员用户唯一索引',
    KEY `idx_nodebb_uid` (`nodebb_uid`) USING BTREE COMMENT 'NodeBB UID 索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会员用户 - NodeBB 用户映射表';


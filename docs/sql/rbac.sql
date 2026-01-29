-- ----------------------------
-- 1. 系统用户表
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`
(
    `id`          bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username`    varchar(64)  NOT NULL COMMENT '用户账号',
    `password`    varchar(100) NOT NULL COMMENT '密码',
    `nickname`    varchar(30)  NOT NULL COMMENT '用户昵称',
    `remark`      varchar(500) DEFAULT NULL COMMENT '备注',
    `dept_id`     bigint(20)   DEFAULT NULL COMMENT '部门ID',
    `email`       varchar(50)  DEFAULT '' COMMENT '用户邮箱',
    `mobile`      varchar(11)  DEFAULT '' COMMENT '手机号码',
    `sex`         tinyint(4)   DEFAULT '0' COMMENT '用户性别',
    `avatar`      varchar(255) DEFAULT '' COMMENT '用户头像',
    `status`      tinyint(4)   DEFAULT '0' COMMENT '帐号状态（0正常 1停用）',
    `login_ip`    varchar(128) DEFAULT '' COMMENT '最后登录IP',
    `login_date`  datetime     DEFAULT NULL COMMENT '最后登录时间',
    `creator`     varchar(64)  DEFAULT '' COMMENT '创建者',
    `create_time` datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`     varchar(64)  DEFAULT '' COMMENT '更新者',
    `update_time` datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     bit(1)       DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='系统用户表';

-- ----------------------------
-- 2. 角色表
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`
(
    `id`          bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    `name`        varchar(30)  NOT NULL COMMENT '角色名称',
    `code`        varchar(100) NOT NULL COMMENT '角色权限字符串',
    `sort`        int(4)       NOT NULL COMMENT '显示顺序',
    `status`      tinyint(4)   NOT NULL COMMENT '角色状态（0正常 1停用）',
    `remark`      varchar(500) DEFAULT NULL COMMENT '备注',
    `creator`     varchar(64)  DEFAULT '' COMMENT '创建者',
    `create_time` datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`     varchar(64)  DEFAULT '' COMMENT '更新者',
    `update_time` datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     bit(1)       DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='角色表';

-- ----------------------------
-- 3. 菜单/权限表
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`
(
    `id`          bigint(20)  NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
    `name`        varchar(50) NOT NULL COMMENT '菜单名称',
    `permission`  varchar(100) DEFAULT '' COMMENT '权限标识',
    `type`        char(1)     NOT NULL COMMENT '菜单类型（M目录 C菜单 F按钮）',
    `parent_id`   bigint(20)   DEFAULT '0' COMMENT '父菜单ID',
    `sort`        int(4)       DEFAULT '0' COMMENT '显示顺序',
    `path`        varchar(200) DEFAULT '' COMMENT '路由地址',
    `component`   varchar(255) DEFAULT NULL COMMENT '组件路径',
    `icon`        varchar(100) DEFAULT '#' COMMENT '菜单图标',
    `status`      tinyint(4)   DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
    `visible`     bit(1)       DEFAULT b'1' COMMENT '是否可见',
    `keep_alive`  bit(1)       DEFAULT b'1' COMMENT '是否缓存',
    `creator`     varchar(64)  DEFAULT '' COMMENT '创建者',
    `create_time` datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`     varchar(64)  DEFAULT '' COMMENT '更新者',
    `update_time` datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     bit(1)       DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='菜单权限表';

-- ----------------------------
-- 4. 用户和角色关联表
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`
(
    `id`      bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id` bigint(20) NOT NULL COMMENT '用户ID',
    `role_id` bigint(20) NOT NULL COMMENT '角色ID',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_role_id` (`role_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户和角色关联表';

-- ----------------------------
-- 5. 角色和菜单关联表
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`
(
    `id`      bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `role_id` bigint(20) NOT NULL COMMENT '角色ID',
    `menu_id` bigint(20) NOT NULL COMMENT '菜单ID',
    PRIMARY KEY (`id`),
    KEY `idx_role_id` (`role_id`),
    KEY `idx_menu_id` (`menu_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='角色和菜单关联表';

-- JWT Demo 数据库初始化脚本

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `jwt_demo` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `jwt_demo`;

-- 用户表
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint NOT NULL COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `email` varchar(100) NOT NULL COMMENT '邮箱',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '用户状态：0-禁用，1-正常',
  `login_fail_count` int NOT NULL DEFAULT '0' COMMENT '登录失败次数',
  `lock_time` datetime DEFAULT NULL COMMENT '账户锁定时间',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`),
  KEY `idx_status` (`status`),
  KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 角色表
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `id` bigint NOT NULL COMMENT '角色ID',
  `role_name` varchar(50) NOT NULL COMMENT '角色名称',
  `description` varchar(200) DEFAULT NULL COMMENT '角色描述',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_name` (`role_name`),
  KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 权限表
DROP TABLE IF EXISTS `permission`;
CREATE TABLE `permission` (
  `id` bigint NOT NULL COMMENT '权限ID',
  `permission_name` varchar(100) NOT NULL COMMENT '权限名称',
  `url` varchar(200) DEFAULT NULL COMMENT '权限URL',
  `method` varchar(10) DEFAULT NULL COMMENT 'HTTP方法',
  `description` varchar(200) DEFAULT NULL COMMENT '权限描述',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_permission_name` (`permission_name`),
  KEY `idx_url_method` (`url`, `method`),
  KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

-- 用户角色关联表
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- 角色权限关联表
DROP TABLE IF EXISTS `role_permission`;
CREATE TABLE `role_permission` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `permission_id` bigint NOT NULL COMMENT '权限ID',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- 插入初始数据

-- 插入角色数据
INSERT INTO `role` (`id`, `role_name`, `description`) VALUES
(1, 'ADMIN', '管理员角色'),
(2, 'USER', '普通用户角色');

-- 插入权限数据
INSERT INTO `permission` (`id`, `permission_name`, `url`, `method`, `description`) VALUES
(1, 'user:read', '/user/profile', 'GET', '查看用户信息'),
(2, 'user:update', '/user/profile', 'PUT', '更新用户信息'),
(3, 'user:password', '/user/password', 'PUT', '修改密码'),
(4, 'admin:user:list', '/user/admin/users', 'GET', '查看所有用户'),
(5, 'admin:user:create', '/user/admin/users', 'POST', '创建用户'),
(6, 'admin:user:update', '/user/admin/users/*', 'PUT', '更新用户'),
(7, 'admin:user:delete', '/user/admin/users/*', 'DELETE', '删除用户');

-- 插入角色权限关联数据
INSERT INTO `role_permission` (`id`, `role_id`, `permission_id`) VALUES
-- 普通用户权限
(1, 2, 1), -- USER角色可以查看用户信息
(2, 2, 2), -- USER角色可以更新用户信息
(3, 2, 3), -- USER角色可以修改密码
-- 管理员权限（继承普通用户权限并添加管理权限）
(4, 1, 1), -- ADMIN角色可以查看用户信息
(5, 1, 2), -- ADMIN角色可以更新用户信息
(6, 1, 3), -- ADMIN角色可以修改密码
(7, 1, 4), -- ADMIN角色可以查看所有用户
(8, 1, 5), -- ADMIN角色可以创建用户
(9, 1, 6), -- ADMIN角色可以更新用户
(10, 1, 7); -- ADMIN角色可以删除用户

-- 插入测试用户数据
-- admin用户密码: Admin123 (BCrypt加密后的值)
-- user用户密码: User123 (BCrypt加密后的值)
INSERT INTO `user` (`id`, `username`, `password`, `email`, `status`) VALUES
(1, 'admin', '$2a$10$DowJonesIndexCryptHashForAdmin123PasswordWithUpperCase', 'admin@example.com', 1),
(2, 'user', '$2a$10$DowJonesIndexCryptHashForUser123PasswordWithUpperCase', 'user@example.com', 1);

-- 插入用户角色关联数据
INSERT INTO `user_role` (`id`, `user_id`, `role_id`) VALUES
(1, 1, 1), -- admin用户拥有ADMIN角色
(2, 2, 2); -- user用户拥有USER角色

COMMIT;
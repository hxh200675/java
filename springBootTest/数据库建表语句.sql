-- =========================================================
--  影院登录/注册项目 - 数据库建库建表语句（文本留存）
--  说明：
--   1) 建库语句也可由后端 JDBC URL 的 createDatabaseIfNotExist=true 自动执行；
--   2) 建表语句由后端 src/main/resources/schema.sql 在每次启动时自动执行；
--   3) 以下语句可在任意 MySQL 客户端手动执行，效果一致。
-- =========================================================

-- ---------- 建库 ----------
CREATE DATABASE IF NOT EXISTS `cinema_db`
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_general_ci;

USE `cinema_db`;

-- ---------- 建表 ----------
CREATE TABLE IF NOT EXISTS `user` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `account`     VARCHAR(64)  NOT NULL COMMENT '账号（手机号或邮箱）',
    `password`    VARCHAR(100) NOT NULL COMMENT '密码（BCrypt 加密存储）',
    `nickname`    VARCHAR(64)  DEFAULT NULL COMMENT '昵称',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1-正常 0-禁用',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_account` (`account`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '用户表';

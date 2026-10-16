/*
 Navicat Premium Dump SQL

 Source Server         : test
 Source Server Type    : MySQL
 Source Server Version : 80039 (8.0.39)
 Source Host           : localhost:3306
 Source Schema         : cinema_db

 Target Server Type    : MySQL
 Target Server Version : 80039 (8.0.39)
 File Encoding         : 65001

 Date: 16/10/2026 16:54:16
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for movie
-- ----------------------------
DROP TABLE IF EXISTS `movie`;
CREATE TABLE `movie`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '影片名称',
  `category` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类：电影/电视剧/综艺/动漫/纪录片',
  `genre` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '类型/题材',
  `score` decimal(3, 1) NULL DEFAULT 0.0 COMMENT '评分 0~10',
  `year` int NULL DEFAULT 0 COMMENT '年份',
  `region` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '地区',
  `duration` int NULL DEFAULT 0 COMMENT '时长（分钟）',
  `description` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '简介',
  `release_date` date NULL DEFAULT NULL COMMENT '上映日期',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category`(`category` ASC) USING BTREE,
  INDEX `idx_score`(`score` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '影片表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of movie
-- ----------------------------
INSERT INTO `movie` VALUES (1, '星际回响', '电影', '科幻', 9.1, 2026, '美国', 142, '跨越光年的相遇，人类在星际中寻找新的家园。', '2026-07-15', '2026-10-16 16:34:06', '2026-10-16 16:34:06');
INSERT INTO `movie` VALUES (2, '雾港旧梦', '电影', '悬疑', 8.4, 2025, '中国', 118, '一场弥漫在雾气中的旧梦，真相藏在港口的阴影里。', '2025-11-02', '2026-10-16 16:34:06', '2026-10-16 16:34:06');
INSERT INTO `movie` VALUES (3, '盛夏告白', '电影', '爱情', 8.8, 2026, '日本', 105, '夏日海风里，青春最勇敢的一次告白。', '2026-06-21', '2026-10-16 16:34:06', '2026-10-16 16:34:06');
INSERT INTO `movie` VALUES (4, '荒野猎手', '电影', '动作', 8.0, 2024, '美国', 128, '孤身一人，于荒野中追寻最后的猎物。', '2024-09-13', '2026-10-16 16:34:06', '2026-10-16 16:34:06');
INSERT INTO `movie` VALUES (5, '深海之歌', '动漫', '奇幻', 9.3, 2026, '日本', 110, '潜入深海，聆听来自蔚蓝世界的第一首歌。', '2026-08-08', '2026-10-16 16:34:06', '2026-10-16 16:34:06');
INSERT INTO `movie` VALUES (6, '霓虹迷城', '电影', '犯罪', 7.9, 2025, '中国', 122, '雨夜霓虹下，罪恶与温情交织成城。', '2025-12-19', '2026-10-16 16:34:06', '2026-10-16 16:34:06');
INSERT INTO `movie` VALUES (7, '长安十二时辰', '电视剧', '古装', 8.7, 2019, '中国', 45, '一天十二时辰，一座城的生死竞速。', '2019-06-27', '2026-10-16 16:34:06', '2026-10-16 16:34:06');
INSERT INTO `movie` VALUES (8, '欢乐喜剧人', '综艺', '喜剧', 7.6, 2026, '中国', 90, '笑声不断，欢乐无极限的喜剧舞台。', '2026-01-10', '2026-10-16 16:34:06', '2026-10-16 16:34:06');
INSERT INTO `movie` VALUES (9, '星际探索纪', '纪录片', '自然', 9.0, 2025, '英国', 98, '镜头穿越荒野，记录星球最原始的心跳。', '2025-03-22', '2026-10-16 16:34:06', '2026-10-16 16:34:06');
INSERT INTO `movie` VALUES (10, '次元战记', '动漫', '热血', 8.5, 2026, '日本', 115, '跨越次元的少年，燃尽热血的战斗物语。', '2026-04-30', '2026-10-16 16:34:06', '2026-10-16 16:34:06');
INSERT INTO `movie` VALUES (11, '都市夜归人', '电视剧', '都市', 7.8, 2026, '中国', 42, '霓虹与孤独并存，城市夜归人的故事。', '2026-02-14', '2026-10-16 16:34:06', '2026-10-16 16:34:06');
INSERT INTO `movie` VALUES (12, '美食环游记', '综艺', '美食', 8.2, 2026, '中国', 85, '一路烟火，用味蕾环游整个世界。', '2026-05-01', '2026-10-16 16:34:06', '2026-10-16 16:34:06');

-- ----------------------------
-- Table structure for movie_session
-- ----------------------------
DROP TABLE IF EXISTS `movie_session`;
CREATE TABLE `movie_session`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `movie_id` bigint NOT NULL COMMENT '影片 id',
  `cinema` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '影院名称',
  `hall` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '影厅',
  `show_time` datetime NOT NULL COMMENT '放映时间',
  `price` decimal(8, 2) NOT NULL DEFAULT 0.00 COMMENT '票价',
  `total_seats` int NOT NULL DEFAULT 0 COMMENT '总座位',
  `available_seats` int NOT NULL DEFAULT 0 COMMENT '剩余座位',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_movie_id`(`movie_id` ASC) USING BTREE,
  INDEX `idx_show_time`(`show_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '放映场次表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of movie_session
-- ----------------------------
INSERT INTO `movie_session` VALUES (1, 1, '星汇影城', '1号IMAX厅', '2026-10-16 19:30:00', 58.00, 120, 116, '2026-10-16 16:34:06', '2026-10-16 16:34:06');
INSERT INTO `movie_session` VALUES (2, 1, '星汇影城', '1号IMAX厅', '2026-10-17 14:00:00', 48.00, 120, 120, '2026-10-16 16:34:06', '2026-10-16 16:34:06');
INSERT INTO `movie_session` VALUES (3, 1, '万达影城', '3号激光厅', '2026-10-16 21:10:00', 52.00, 100, 95, '2026-10-16 16:34:06', '2026-10-16 16:34:06');
INSERT INTO `movie_session` VALUES (4, 5, '星汇影城', '2号杜比厅', '2026-10-16 15:20:00', 45.00, 100, 100, '2026-10-16 16:34:06', '2026-10-16 16:34:06');
INSERT INTO `movie_session` VALUES (5, 5, '万达影城', '1号厅', '2026-10-17 10:30:00', 40.00, 120, 110, '2026-10-16 16:34:06', '2026-10-16 16:34:06');
INSERT INTO `movie_session` VALUES (6, 7, '横店影城', '大剧场', '2026-10-16 20:00:00', 36.00, 80, 60, '2026-10-16 16:34:06', '2026-10-16 16:34:06');
INSERT INTO `movie_session` VALUES (7, 10, 'CGV影城', '4号厅', '2026-10-17 13:30:00', 50.00, 110, 107, '2026-10-16 16:34:06', '2026-10-16 16:34:06');
INSERT INTO `movie_session` VALUES (8, 10, '星汇影城', '3号厅', '2026-10-16 18:00:00', 50.00, 120, 119, '2026-10-16 16:34:06', '2026-10-16 16:34:06');
INSERT INTO `movie_session` VALUES (9, 3, '万达影城', '2号厅', '2026-10-16 16:40:00', 42.00, 100, 99, '2026-10-16 16:34:06', '2026-10-16 16:34:06');
INSERT INTO `movie_session` VALUES (10, 5, 'CGV影城', '1号IMAX厅', '2026-10-16 22:00:00', 55.00, 140, 140, '2026-10-16 16:34:06', '2026-10-16 16:34:06');

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户 id',
  `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '下单用户名',
  `movie_id` bigint NOT NULL COMMENT '影片 id',
  `movie_title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '影片名称',
  `session_id` bigint NOT NULL COMMENT '场次 id',
  `cinema` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '影院',
  `hall` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '影厅',
  `show_time` datetime NULL DEFAULT NULL COMMENT '放映时间',
  `seats` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '座位（逗号分隔）',
  `seat_count` int NULL DEFAULT 0 COMMENT '座位数量',
  `amount` decimal(8, 2) NULL DEFAULT 0.00 COMMENT '订单金额',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1-待使用 2-已使用 0-已取消',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of orders
-- ----------------------------
INSERT INTO `orders` VALUES (1, 'M17921398342333150', NULL, 'aitest01', 1, '星际回响', 1, '星汇影城', '1号IMAX厅', '2026-10-16 19:30:00', '3排5座,3排6座', 2, 116.00, 1, '2026-10-16 16:37:14', '2026-10-16 16:37:14');
INSERT INTO `orders` VALUES (2, 'M17921398387362618', NULL, 'qwer', 10, '次元战记', 7, 'CGV影城', '4号厅', '2026-10-17 13:30:00', '5排7座', 1, 50.00, 1, '2026-10-16 16:37:19', '2026-10-16 16:37:19');
INSERT INTO `orders` VALUES (3, 'M17921400282139455', NULL, 'qwer', 3, '盛夏告白', 9, '万达影城', '2号厅', '2026-10-16 16:40:00', '7排9座', 1, 42.00, 1, '2026-10-16 16:40:28', '2026-10-16 16:40:28');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `account` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名（登录账号）',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码（BCrypt 加密存储）',
  `nickname` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '昵称',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1-正常 0-禁用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像地址',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_account`(`account` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'qwer', '$2a$10$.PcQhcbMgyCcumQuR2FX3e3yofMJeQNZc9LhywabEiiAkp7wBzgYy', 'qwer', 1, '2026-10-16 15:45:51', '2026-10-16 15:45:51', NULL);
INSERT INTO `user` VALUES (2, 'aitest01', '$2a$10$0V4iXJc5/qUNt3Jy0XtIROdf1q/EgMXQAHKUf6b7m6mKFDBYAd3gW', 'aitest01', 1, '2026-10-16 16:01:46', '2026-10-16 16:01:46', NULL);
INSERT INTO `user` VALUES (3, 'ptest01', '$2a$10$OWDxg/gnB9qmibqpsqDhauEbEOp3J43AfxF1whoIOYJfoxS6yvNHa', '影迷小张', 1, '2026-10-16 16:51:17', '2026-10-16 16:51:17', '/uploads/avatar_3_1792140703160.png');

SET FOREIGN_KEY_CHECKS = 1;

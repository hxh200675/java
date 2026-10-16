-- 自动建表脚本：每次应用启动由 spring.sql.init 执行
-- 配合 JDBC URL 中的 createDatabaseIfNotExist=true 一起实现“自动建库建表”
-- CREATE TABLE IF NOT EXISTS / INSERT IGNORE 均为幂等操作，可重复执行

CREATE TABLE IF NOT EXISTS `user` (
    `id`         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `account`    VARCHAR(64)  NOT NULL COMMENT '用户名（登录账号）',
    `password`   VARCHAR(100) NOT NULL COMMENT '密码（BCrypt 加密存储）',
    `nickname`   VARCHAR(64)  DEFAULT NULL COMMENT '昵称',
    `avatar`     VARCHAR(255) DEFAULT NULL COMMENT '头像地址',
    `status`     TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1-正常 0-禁用',
    `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_account` (`account`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '用户表';

-- ============ 影片表 ============
CREATE TABLE IF NOT EXISTS `movie` (
    `id`           BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `title`        VARCHAR(128) NOT NULL COMMENT '影片名称',
    `category`     VARCHAR(32)  NOT NULL COMMENT '分类：电影/电视剧/综艺/动漫/纪录片',
    `genre`        VARCHAR(32)  DEFAULT '' COMMENT '类型/题材',
    `score`        DECIMAL(3,1) DEFAULT 0.0 COMMENT '评分 0~10',
    `year`         INT          DEFAULT 0 COMMENT '年份',
    `region`       VARCHAR(32)  DEFAULT '' COMMENT '地区',
    `duration`     INT          DEFAULT 0 COMMENT '时长（分钟）',
    `description`  VARCHAR(512) DEFAULT '' COMMENT '简介',
    `release_date` DATE         DEFAULT NULL COMMENT '上映日期',
    `create_time`  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_category` (`category`),
    KEY `idx_score` (`score`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '影片表';

-- ============ 放映场次表 ============
CREATE TABLE IF NOT EXISTS `movie_session` (
    `id`              BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `movie_id`        BIGINT      NOT NULL COMMENT '影片 id',
    `cinema`          VARCHAR(64) NOT NULL COMMENT '影院名称',
    `hall`            VARCHAR(32) NOT NULL COMMENT '影厅',
    `show_time`       DATETIME    NOT NULL COMMENT '放映时间',
    `price`           DECIMAL(8,2) NOT NULL DEFAULT 0.00 COMMENT '票价',
    `total_seats`     INT         NOT NULL DEFAULT 0 COMMENT '总座位',
    `available_seats` INT         NOT NULL DEFAULT 0 COMMENT '剩余座位',
    `occupied_seats`  VARCHAR(2000) NOT NULL DEFAULT '' COMMENT '已占用座位（逗号分隔，如 1排1座,1排2座）',
    `create_time`     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_movie_id` (`movie_id`),
    KEY `idx_show_time` (`show_time`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '放映场次表';

-- ============ 订单表 ============
CREATE TABLE IF NOT EXISTS `orders` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `order_no`     VARCHAR(64)  NOT NULL COMMENT '订单号',
    `user_id`      BIGINT       DEFAULT NULL COMMENT '用户 id',
    `username`     VARCHAR(64)  NOT NULL COMMENT '下单用户名',
    `movie_id`     BIGINT       NOT NULL COMMENT '影片 id',
    `movie_title`  VARCHAR(128) DEFAULT '' COMMENT '影片名称',
    `session_id`   BIGINT       NOT NULL COMMENT '场次 id',
    `cinema`       VARCHAR(64)  DEFAULT '' COMMENT '影院',
    `hall`         VARCHAR(32)  DEFAULT '' COMMENT '影厅',
    `show_time`    DATETIME     DEFAULT NULL COMMENT '放映时间',
    `seats`        VARCHAR(128) DEFAULT '' COMMENT '座位（逗号分隔）',
    `seat_count`   INT          DEFAULT 0 COMMENT '座位数量',
    `amount`       DECIMAL(8,2) DEFAULT 0.00 COMMENT '订单金额',
    `status`       TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1-待使用 2-已使用 0-已取消',
    `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_username` (`username`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '订单表';

-- ============ 初始影片数据（幂等） ============
INSERT IGNORE INTO `movie` (`id`,`title`,`category`,`genre`,`score`,`year`,`region`,`duration`,`description`,`release_date`) VALUES
(1,'星际回响','电影','科幻',9.1,2026,'美国',142,'跨越光年的相遇，人类在星际中寻找新的家园。','2026-07-15'),
(2,'雾港旧梦','电影','悬疑',8.4,2025,'中国',118,'一场弥漫在雾气中的旧梦，真相藏在港口的阴影里。','2025-11-02'),
(3,'盛夏告白','电影','爱情',8.8,2026,'日本',105,'夏日海风里，青春最勇敢的一次告白。','2026-06-21'),
(4,'荒野猎手','电影','动作',8.0,2024,'美国',128,'孤身一人，于荒野中追寻最后的猎物。','2024-09-13'),
(5,'深海之歌','动漫','奇幻',9.3,2026,'日本',110,'潜入深海，聆听来自蔚蓝世界的第一首歌。','2026-08-08'),
(6,'霓虹迷城','电影','犯罪',7.9,2025,'中国',122,'雨夜霓虹下，罪恶与温情交织成城。','2025-12-19'),
(7,'长安十二时辰','电视剧','古装',8.7,2019,'中国',45,'一天十二时辰，一座城的生死竞速。','2019-06-27'),
(8,'欢乐喜剧人','综艺','喜剧',7.6,2026,'中国',90,'笑声不断，欢乐无极限的喜剧舞台。','2026-01-10'),
(9,'星际探索纪','纪录片','自然',9.0,2025,'英国',98,'镜头穿越荒野，记录星球最原始的心跳。','2025-03-22'),
(10,'次元战记','动漫','热血',8.5,2026,'日本',115,'跨越次元的少年，燃尽热血的战斗物语。','2026-04-30'),
(11,'都市夜归人','电视剧','都市',7.8,2026,'中国',42,'霓虹与孤独并存，城市夜归人的故事。','2026-02-14'),
(12,'美食环游记','综艺','美食',8.2,2026,'中国',85,'一路烟火，用味蕾环游整个世界。','2026-05-01');

-- ============ 初始放映场次数据（幂等） ============
INSERT IGNORE INTO `movie_session` (`id`,`movie_id`,`cinema`,`hall`,`show_time`,`price`,`total_seats`,`available_seats`,`occupied_seats`) VALUES
(1,1,'星汇影城','1号IMAX厅','2026-10-16 19:30:00',58.00,120,118,'1排1座,1排2座'),
(2,1,'星汇影城','1号IMAX厅','2026-10-17 14:00:00',48.00,120,120,''),
(3,1,'万达影城','3号激光厅','2026-10-16 21:10:00',52.00,100,95,'1排1座,1排2座,1排3座,1排4座,1排5座'),
(4,5,'星汇影城','2号杜比厅','2026-10-16 15:20:00',45.00,100,100,''),
(5,5,'万达影城','1号厅','2026-10-17 10:30:00',40.00,120,110,'1排1座,1排2座,1排3座,1排4座,1排5座,1排6座,1排7座,1排8座,1排9座,1排10座'),
(6,7,'横店影城','大剧场','2026-10-16 20:00:00',36.00,80,60,'1排1座,1排2座,1排3座,1排4座,1排5座,1排6座,1排7座,1排8座,1排9座,1排10座,1排11座,1排12座,2排1座,2排2座,2排3座,2排4座,2排5座,2排6座,2排7座,2排8座'),
(7,10,'CGV影城','4号厅','2026-10-17 13:30:00',50.00,110,108,'1排1座,1排2座'),
(8,10,'星汇影城','3号厅','2026-10-16 18:00:00',50.00,120,119,'1排1座'),
(9,3,'万达影城','2号厅','2026-10-16 16:40:00',42.00,100,100,''),
(10,5,'CGV影城','1号IMAX厅','2026-10-16 22:00:00',55.00,140,140,'');

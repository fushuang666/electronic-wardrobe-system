-- 电子衣橱系统 数据库初始化脚本（MySQL 5.7+ / 8.0）
-- 执行方式：mysql -u root -p wardrobe < schema.sql
-- 说明：表引擎 InnoDB，字符集 utf8mb4；外键按需级联。

CREATE DATABASE IF NOT EXISTS wardrobe
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_general_ci;

USE wardrobe;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS reminder;
DROP TABLE IF EXISTS outfit_item;
DROP TABLE IF EXISTS outfit;
DROP TABLE IF EXISTS clothing;
DROP TABLE IF EXISTS weather_preference;
DROP TABLE IF EXISTS wardrobe_member;
DROP TABLE IF EXISTS wardrobe;
DROP TABLE IF EXISTS user;

CREATE TABLE `user` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `username`    VARCHAR(50)  NOT NULL,
    `password`    VARCHAR(100) NOT NULL,
    `nickname`    VARCHAR(50),
    `avatar`      VARCHAR(500),
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE `wardrobe` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `name`        VARCHAR(50)  NOT NULL,
    `description` VARCHAR(200),
    `type`        VARCHAR(20)  DEFAULT 'PERSONAL',
    `owner_id`    BIGINT       NOT NULL,
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_owner` (`owner_id`),
    CONSTRAINT `fk_wardrobe_owner` FOREIGN KEY (`owner_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE `wardrobe_member` (
    `id`          BIGINT      NOT NULL AUTO_INCREMENT,
    `wardrobe_id` BIGINT      NOT NULL,
    `user_id`     BIGINT      NOT NULL,
    `role`        VARCHAR(20) DEFAULT 'MEMBER',
    `create_time` DATETIME    DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_wm` (`wardrobe_id`, `user_id`),
    CONSTRAINT `fk_wm_wardrobe` FOREIGN KEY (`wardrobe_id`) REFERENCES `wardrobe` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_wm_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE `clothing` (
    `id`             BIGINT       NOT NULL AUTO_INCREMENT,
    `wardrobe_id`    BIGINT       NOT NULL,
    `name`           VARCHAR(100) NOT NULL,
    `category`       VARCHAR(30),
    `season`         VARCHAR(20),
    `color`          VARCHAR(30),
    `style`          VARCHAR(30),
    `occasion`       VARCHAR(30),
    `image_url`      VARCHAR(500),
    `brand`          VARCHAR(50),
    `price`          DECIMAL(10, 2),
    `purchase_date`  DATETIME,
    `last_wear_date` DATETIME,
    `wear_count`     INT          DEFAULT 0,
    `note`           VARCHAR(500),
    `create_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP,
    `update_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_clothing_wardrobe` (`wardrobe_id`),
    CONSTRAINT `fk_clothing_wardrobe` FOREIGN KEY (`wardrobe_id`) REFERENCES `wardrobe` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE `outfit` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT,
    `wardrobe_id`  BIGINT       NOT NULL,
    `name`         VARCHAR(100) NOT NULL,
    `description`  VARCHAR(500),
    `cover_image`  VARCHAR(500),
    `create_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    `update_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_outfit_wardrobe` (`wardrobe_id`),
    CONSTRAINT `fk_outfit_wardrobe` FOREIGN KEY (`wardrobe_id`) REFERENCES `wardrobe` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE `outfit_item` (
    `id`          BIGINT NOT NULL AUTO_INCREMENT,
    `outfit_id`   BIGINT NOT NULL,
    `clothing_id` BIGINT NOT NULL,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_oi` (`outfit_id`, `clothing_id`),
    CONSTRAINT `fk_oi_outfit` FOREIGN KEY (`outfit_id`) REFERENCES `outfit` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_oi_clothing` FOREIGN KEY (`clothing_id`) REFERENCES `clothing` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE `weather_preference` (
    `id`          BIGINT      NOT NULL AUTO_INCREMENT,
    `user_id`     BIGINT      NOT NULL,
    `city`        VARCHAR(50),
    `city_code`   VARCHAR(50),
    `create_time` DATETIME    DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_wp_user` (`user_id`),
    CONSTRAINT `fk_wp_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE `reminder` (
    `id`          BIGINT      NOT NULL AUTO_INCREMENT,
    `wardrobe_id` BIGINT      NOT NULL,
    `type`        VARCHAR(20) NOT NULL,
    `title`       VARCHAR(100) NOT NULL,
    `content`     VARCHAR(500),
    `status`      VARCHAR(20) DEFAULT 'UNREAD',
    `related_id`  BIGINT,
    `create_time` DATETIME    DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_reminder_wardrobe` (`wardrobe_id`),
    CONSTRAINT `fk_reminder_wardrobe` FOREIGN KEY (`wardrobe_id`) REFERENCES `wardrobe` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;

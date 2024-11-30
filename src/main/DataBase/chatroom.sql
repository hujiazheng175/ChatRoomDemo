/*
 Navicat MySQL Data Transfer

 Source Server         : rr
 Source Server Type    : MySQL
 Source Server Version : 80031 (8.0.31)
 Source Host           : localhost:3306
 Source Schema         : chatroom

 Target Server Type    : MySQL
 Target Server Version : 80031 (8.0.31)
 File Encoding         : 65001

 Date: 29/11/2024 21:58:51
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for messages
-- ----------------------------
DROP TABLE IF EXISTS `messages`;
CREATE TABLE `messages`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `sender_id` bigint NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `sender_id`(`sender_id` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE,
  CONSTRAINT `messages_ibfk_1` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 29 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of messages
-- ----------------------------
INSERT INTO `messages` VALUES (1, 1, 'Welcome!', '2024-11-29 09:04:15');
INSERT INTO `messages` VALUES (14, 3, '你好', '2024-11-29 11:39:04');
INSERT INTO `messages` VALUES (15, 2, '你也好', '2024-11-29 11:39:14');
INSERT INTO `messages` VALUES (16, 3, '还有人吗？', '2024-11-29 11:39:39');
INSERT INTO `messages` VALUES (17, 2, '有人呀', '2024-11-29 11:39:44');
INSERT INTO `messages` VALUES (18, 2, '现在是什么时间', '2024-11-29 11:47:05');
INSERT INTO `messages` VALUES (19, 2, 'hello', '2024-11-29 11:56:26');
INSERT INTO `messages` VALUES (20, 3, '你怎么退出了？', '2024-11-29 11:59:30');
INSERT INTO `messages` VALUES (21, 3, 'Test', '2024-11-29 12:05:44');
INSERT INTO `messages` VALUES (22, 3, 'Test2', '2024-11-29 12:39:55');
INSERT INTO `messages` VALUES (23, 5, '你是谁', '2024-11-29 13:34:03');
INSERT INTO `messages` VALUES (24, 5, '真相只有一个！', '2024-11-29 13:34:11');
INSERT INTO `messages` VALUES (25, 2, 'shab', '2024-11-29 13:34:24');
INSERT INTO `messages` VALUES (26, 5, '傻逼', '2024-11-29 13:34:28');
INSERT INTO `messages` VALUES (27, 6, '真实你', '2024-11-29 13:43:05');
INSERT INTO `messages` VALUES (28, 2, 'HELLO', '2024-11-29 13:43:35');

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `avatar_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `last_active` timestamp NULL DEFAULT NULL,
  `status` tinyint NULL DEFAULT 1 COMMENT '1:正常, 0:禁用',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  INDEX `idx_username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, 'admin', '123', './assets/system.jpg', '2024-11-29 09:03:42', '2024-11-29 12:59:38', 1);
INSERT INTO `users` VALUES (2, 'XiaoGong', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '/uploads/18e1661a-8fcf-4e55-a39e-9d16809b8c23.png', '2024-11-29 03:48:39', '2024-11-29 21:55:44', 0);
INSERT INTO `users` VALUES (3, 'XiaoMing', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '/uploads/b6e105c6-e16e-4af6-97a7-f3de0f0395da.png', '2024-11-29 05:45:17', '2024-11-29 21:26:11', 0);
INSERT INTO `users` VALUES (4, 'James', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '/assets/useravatar/ec1f6ebb-d1a6-45e3-92f1-941db6e961e3.png', '2024-11-29 12:46:30', '2024-11-29 20:57:11', 1);
INSERT INTO `users` VALUES (5, '江户川柯南', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '/assets/useravatar/e70ae309-7802-4987-a690-f287d14afcff.jpg', '2024-11-29 13:33:54', '2024-11-29 21:33:58', 0);
INSERT INTO `users` VALUES (6, 'xzd', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '/assets/useravatar/7199f57a-8ebe-412e-b96c-e8d59730ae67.png', '2024-11-29 13:42:42', '2024-11-29 21:43:29', 0);

SET FOREIGN_KEY_CHECKS = 1;

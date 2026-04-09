/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80031
 Source Host           : localhost:3306
 Source Schema         : lease

 Target Server Type    : MySQL
 Target Server Version : 80031
 File Encoding         : 65001

 Date: 19/08/2023 23:38:48
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for apartment_facility
-- ----------------------------
DROP TABLE IF EXISTS `apartment_facility`;
CREATE TABLE `apartment_facility`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `apartment_id` bigint NULL DEFAULT NULL COMMENT '公寓id',
  `facility_id` bigint NULL DEFAULT NULL COMMENT '设施id',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 231 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '公寓&配套关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of apartment_facility
-- ----------------------------
INSERT INTO `apartment_facility` VALUES (11, 4, 24, '2023-06-19 23:20:06', NULL, 1);
INSERT INTO `apartment_facility` VALUES (12, 4, 26, '2023-06-19 23:20:06', NULL, 1);
INSERT INTO `apartment_facility` VALUES (13, 4, 24, '2023-06-19 23:21:42', NULL, 1);
INSERT INTO `apartment_facility` VALUES (14, 4, 25, '2023-06-19 23:21:42', NULL, 1);
INSERT INTO `apartment_facility` VALUES (15, 4, 26, '2023-06-19 23:21:42', NULL, 1);
INSERT INTO `apartment_facility` VALUES (16, 5, 26, '2023-06-20 08:47:57', NULL, 1);
INSERT INTO `apartment_facility` VALUES (17, 5, 27, '2023-06-20 08:47:57', NULL, 1);
INSERT INTO `apartment_facility` VALUES (18, 6, 26, '2023-06-20 08:53:00', NULL, 1);
INSERT INTO `apartment_facility` VALUES (19, 6, 27, '2023-06-20 08:53:00', NULL, 1);
INSERT INTO `apartment_facility` VALUES (20, 7, 26, '2023-06-20 08:53:35', NULL, 1);
INSERT INTO `apartment_facility` VALUES (21, 7, 27, '2023-06-20 08:53:35', NULL, 1);
INSERT INTO `apartment_facility` VALUES (22, 8, 26, '2023-06-20 08:53:59', NULL, 1);
INSERT INTO `apartment_facility` VALUES (23, 8, 27, '2023-06-20 08:53:59', NULL, 1);
INSERT INTO `apartment_facility` VALUES (24, 8, 26, '2023-06-20 08:54:30', NULL, 1);
INSERT INTO `apartment_facility` VALUES (25, 8, 27, '2023-06-20 08:54:30', NULL, 1);
INSERT INTO `apartment_facility` VALUES (26, 9, 26, '2023-06-20 09:13:52', NULL, 1);
INSERT INTO `apartment_facility` VALUES (27, 9, 27, '2023-06-20 09:13:52', NULL, 1);
INSERT INTO `apartment_facility` VALUES (28, 9, 26, '2023-06-20 09:15:41', NULL, 1);
INSERT INTO `apartment_facility` VALUES (29, 9, 27, '2023-06-20 09:15:41', NULL, 1);
INSERT INTO `apartment_facility` VALUES (30, 9, 26, '2023-06-20 10:15:43', NULL, 1);
INSERT INTO `apartment_facility` VALUES (31, 9, 27, '2023-06-20 10:15:43', NULL, 1);
INSERT INTO `apartment_facility` VALUES (32, 9, 26, '2023-06-20 10:17:44', NULL, 1);
INSERT INTO `apartment_facility` VALUES (33, 9, 27, '2023-06-20 10:17:44', NULL, 1);
INSERT INTO `apartment_facility` VALUES (34, 10, 24, '2023-06-21 10:17:59', NULL, 1);
INSERT INTO `apartment_facility` VALUES (35, 10, 25, '2023-06-21 10:17:59', NULL, 1);
INSERT INTO `apartment_facility` VALUES (36, 10, 26, '2023-06-21 10:17:59', NULL, 1);
INSERT INTO `apartment_facility` VALUES (37, 11, 24, '2023-06-21 10:19:46', NULL, 1);
INSERT INTO `apartment_facility` VALUES (38, 11, 25, '2023-06-21 10:19:46', NULL, 1);
INSERT INTO `apartment_facility` VALUES (39, 11, 26, '2023-06-21 10:19:46', NULL, 1);
INSERT INTO `apartment_facility` VALUES (40, 10, 24, '2023-06-21 10:21:36', NULL, 1);
INSERT INTO `apartment_facility` VALUES (41, 10, 25, '2023-06-21 10:21:36', NULL, 1);
INSERT INTO `apartment_facility` VALUES (42, 10, 24, '2023-06-21 10:22:10', NULL, 1);
INSERT INTO `apartment_facility` VALUES (43, 10, 25, '2023-06-21 10:22:10', NULL, 1);
INSERT INTO `apartment_facility` VALUES (44, 9, 24, '2023-07-18 16:29:06', NULL, 1);
INSERT INTO `apartment_facility` VALUES (45, 9, 26, '2023-07-18 16:29:06', NULL, 1);
INSERT INTO `apartment_facility` VALUES (46, 9, 25, '2023-07-18 16:29:06', NULL, 1);
INSERT INTO `apartment_facility` VALUES (47, 10, 24, '2023-07-18 16:30:04', NULL, 1);
INSERT INTO `apartment_facility` VALUES (48, 10, 25, '2023-07-18 16:30:04', NULL, 1);
INSERT INTO `apartment_facility` VALUES (49, 9, 24, '2023-07-22 10:44:44', NULL, 1);
INSERT INTO `apartment_facility` VALUES (50, 9, 25, '2023-07-22 10:44:44', NULL, 1);
INSERT INTO `apartment_facility` VALUES (51, 9, 26, '2023-07-22 10:44:44', NULL, 1);
INSERT INTO `apartment_facility` VALUES (52, 9, 24, '2023-07-22 13:52:19', NULL, 1);
INSERT INTO `apartment_facility` VALUES (53, 9, 25, '2023-07-22 13:52:19', NULL, 1);
INSERT INTO `apartment_facility` VALUES (54, 9, 26, '2023-07-22 13:52:19', NULL, 1);
INSERT INTO `apartment_facility` VALUES (55, 9, 24, '2023-08-10 18:52:30', NULL, 1);
INSERT INTO `apartment_facility` VALUES (56, 9, 25, '2023-08-10 18:52:30', NULL, 1);
INSERT INTO `apartment_facility` VALUES (57, 9, 26, '2023-08-10 18:52:30', NULL, 1);
INSERT INTO `apartment_facility` VALUES (58, 9, 40, '2023-08-10 18:52:30', NULL, 1);
INSERT INTO `apartment_facility` VALUES (59, 9, 41, '2023-08-10 18:52:30', NULL, 1);
INSERT INTO `apartment_facility` VALUES (60, 9, 42, '2023-08-10 18:52:30', NULL, 1);
INSERT INTO `apartment_facility` VALUES (61, 9, 43, '2023-08-10 18:52:30', NULL, 1);
INSERT INTO `apartment_facility` VALUES (62, 9, 44, '2023-08-10 18:52:30', NULL, 1);
INSERT INTO `apartment_facility` VALUES (63, 9, 45, '2023-08-10 18:52:30', NULL, 1);
INSERT INTO `apartment_facility` VALUES (64, 9, 46, '2023-08-10 18:52:30', NULL, 1);
INSERT INTO `apartment_facility` VALUES (65, 9, 47, '2023-08-10 18:52:30', NULL, 1);
INSERT INTO `apartment_facility` VALUES (66, 9, 57, '2023-08-10 18:52:30', NULL, 1);
INSERT INTO `apartment_facility` VALUES (67, 10, 24, '2023-08-10 18:53:11', NULL, 1);
INSERT INTO `apartment_facility` VALUES (68, 10, 25, '2023-08-10 18:53:11', NULL, 1);
INSERT INTO `apartment_facility` VALUES (69, 10, 26, '2023-08-10 18:53:11', NULL, 1);
INSERT INTO `apartment_facility` VALUES (70, 10, 40, '2023-08-10 18:53:11', NULL, 1);
INSERT INTO `apartment_facility` VALUES (71, 10, 41, '2023-08-10 18:53:11', NULL, 1);
INSERT INTO `apartment_facility` VALUES (72, 10, 43, '2023-08-10 18:53:11', NULL, 1);
INSERT INTO `apartment_facility` VALUES (73, 10, 44, '2023-08-10 18:53:11', NULL, 1);
INSERT INTO `apartment_facility` VALUES (74, 10, 42, '2023-08-10 18:53:11', NULL, 1);
INSERT INTO `apartment_facility` VALUES (75, 10, 57, '2023-08-10 18:53:11', NULL, 1);
INSERT INTO `apartment_facility` VALUES (76, 10, 46, '2023-08-10 18:53:11', NULL, 1);
INSERT INTO `apartment_facility` VALUES (77, 9, 24, '2023-08-14 00:04:56', NULL, 1);
INSERT INTO `apartment_facility` VALUES (78, 9, 25, '2023-08-14 00:04:56', NULL, 1);
INSERT INTO `apartment_facility` VALUES (79, 9, 26, '2023-08-14 00:04:56', NULL, 1);
INSERT INTO `apartment_facility` VALUES (80, 9, 40, '2023-08-14 00:04:56', NULL, 1);
INSERT INTO `apartment_facility` VALUES (81, 9, 41, '2023-08-14 00:04:56', NULL, 1);
INSERT INTO `apartment_facility` VALUES (82, 9, 42, '2023-08-14 00:04:56', NULL, 1);
INSERT INTO `apartment_facility` VALUES (83, 9, 43, '2023-08-14 00:04:56', NULL, 1);
INSERT INTO `apartment_facility` VALUES (84, 9, 44, '2023-08-14 00:04:56', NULL, 1);
INSERT INTO `apartment_facility` VALUES (85, 9, 45, '2023-08-14 00:04:56', NULL, 1);
INSERT INTO `apartment_facility` VALUES (86, 9, 46, '2023-08-14 00:04:56', NULL, 1);
INSERT INTO `apartment_facility` VALUES (87, 9, 47, '2023-08-14 00:04:56', NULL, 1);
INSERT INTO `apartment_facility` VALUES (88, 9, 57, '2023-08-14 00:04:56', NULL, 1);
INSERT INTO `apartment_facility` VALUES (89, 10, 24, '2023-08-14 00:05:06', NULL, 1);
INSERT INTO `apartment_facility` VALUES (90, 10, 25, '2023-08-14 00:05:06', NULL, 1);
INSERT INTO `apartment_facility` VALUES (91, 10, 26, '2023-08-14 00:05:06', NULL, 1);
INSERT INTO `apartment_facility` VALUES (92, 10, 40, '2023-08-14 00:05:06', NULL, 1);
INSERT INTO `apartment_facility` VALUES (93, 10, 41, '2023-08-14 00:05:06', NULL, 1);
INSERT INTO `apartment_facility` VALUES (94, 10, 43, '2023-08-14 00:05:06', NULL, 1);
INSERT INTO `apartment_facility` VALUES (95, 10, 44, '2023-08-14 00:05:06', NULL, 1);
INSERT INTO `apartment_facility` VALUES (96, 10, 42, '2023-08-14 00:05:06', NULL, 1);
INSERT INTO `apartment_facility` VALUES (97, 10, 57, '2023-08-14 00:05:06', NULL, 1);
INSERT INTO `apartment_facility` VALUES (98, 10, 46, '2023-08-14 00:05:06', NULL, 1);
INSERT INTO `apartment_facility` VALUES (99, 9, 24, '2023-08-14 00:13:51', NULL, 1);
INSERT INTO `apartment_facility` VALUES (100, 9, 25, '2023-08-14 00:13:51', NULL, 1);
INSERT INTO `apartment_facility` VALUES (101, 9, 26, '2023-08-14 00:13:51', NULL, 1);
INSERT INTO `apartment_facility` VALUES (102, 9, 40, '2023-08-14 00:13:51', NULL, 1);
INSERT INTO `apartment_facility` VALUES (103, 9, 41, '2023-08-14 00:13:51', NULL, 1);
INSERT INTO `apartment_facility` VALUES (104, 9, 42, '2023-08-14 00:13:51', NULL, 1);
INSERT INTO `apartment_facility` VALUES (105, 9, 43, '2023-08-14 00:13:51', NULL, 1);
INSERT INTO `apartment_facility` VALUES (106, 9, 44, '2023-08-14 00:13:51', NULL, 1);
INSERT INTO `apartment_facility` VALUES (107, 9, 45, '2023-08-14 00:13:51', NULL, 1);
INSERT INTO `apartment_facility` VALUES (108, 9, 46, '2023-08-14 00:13:51', NULL, 1);
INSERT INTO `apartment_facility` VALUES (109, 9, 47, '2023-08-14 00:13:51', NULL, 1);
INSERT INTO `apartment_facility` VALUES (110, 9, 57, '2023-08-14 00:13:51', NULL, 1);
INSERT INTO `apartment_facility` VALUES (111, 10, 24, '2023-08-14 00:13:56', NULL, 1);
INSERT INTO `apartment_facility` VALUES (112, 10, 25, '2023-08-14 00:13:56', NULL, 1);
INSERT INTO `apartment_facility` VALUES (113, 10, 26, '2023-08-14 00:13:56', NULL, 1);
INSERT INTO `apartment_facility` VALUES (114, 10, 40, '2023-08-14 00:13:56', NULL, 1);
INSERT INTO `apartment_facility` VALUES (115, 10, 41, '2023-08-14 00:13:56', NULL, 1);
INSERT INTO `apartment_facility` VALUES (116, 10, 43, '2023-08-14 00:13:56', NULL, 1);
INSERT INTO `apartment_facility` VALUES (117, 10, 44, '2023-08-14 00:13:56', NULL, 1);
INSERT INTO `apartment_facility` VALUES (118, 10, 42, '2023-08-14 00:13:56', NULL, 1);
INSERT INTO `apartment_facility` VALUES (119, 10, 57, '2023-08-14 00:13:56', NULL, 1);
INSERT INTO `apartment_facility` VALUES (120, 10, 46, '2023-08-14 00:13:56', NULL, 1);
INSERT INTO `apartment_facility` VALUES (121, 9, 24, '2023-08-14 11:31:25', NULL, 1);
INSERT INTO `apartment_facility` VALUES (122, 9, 25, '2023-08-14 11:31:25', NULL, 1);
INSERT INTO `apartment_facility` VALUES (123, 9, 26, '2023-08-14 11:31:25', NULL, 1);
INSERT INTO `apartment_facility` VALUES (124, 9, 40, '2023-08-14 11:31:25', NULL, 1);
INSERT INTO `apartment_facility` VALUES (125, 9, 41, '2023-08-14 11:31:25', NULL, 1);
INSERT INTO `apartment_facility` VALUES (126, 9, 42, '2023-08-14 11:31:25', NULL, 1);
INSERT INTO `apartment_facility` VALUES (127, 9, 43, '2023-08-14 11:31:25', NULL, 1);
INSERT INTO `apartment_facility` VALUES (128, 9, 44, '2023-08-14 11:31:25', NULL, 1);
INSERT INTO `apartment_facility` VALUES (129, 9, 45, '2023-08-14 11:31:25', NULL, 1);
INSERT INTO `apartment_facility` VALUES (130, 9, 46, '2023-08-14 11:31:25', NULL, 1);
INSERT INTO `apartment_facility` VALUES (131, 9, 47, '2023-08-14 11:31:25', NULL, 1);
INSERT INTO `apartment_facility` VALUES (132, 9, 57, '2023-08-14 11:31:25', NULL, 1);
INSERT INTO `apartment_facility` VALUES (133, 10, 24, '2023-08-14 11:36:36', NULL, 1);
INSERT INTO `apartment_facility` VALUES (134, 10, 25, '2023-08-14 11:36:36', NULL, 1);
INSERT INTO `apartment_facility` VALUES (135, 10, 26, '2023-08-14 11:36:36', NULL, 1);
INSERT INTO `apartment_facility` VALUES (136, 10, 40, '2023-08-14 11:36:36', NULL, 1);
INSERT INTO `apartment_facility` VALUES (137, 10, 41, '2023-08-14 11:36:36', NULL, 1);
INSERT INTO `apartment_facility` VALUES (138, 10, 43, '2023-08-14 11:36:36', NULL, 1);
INSERT INTO `apartment_facility` VALUES (139, 10, 44, '2023-08-14 11:36:36', NULL, 1);
INSERT INTO `apartment_facility` VALUES (140, 10, 42, '2023-08-14 11:36:36', NULL, 1);
INSERT INTO `apartment_facility` VALUES (141, 10, 57, '2023-08-14 11:36:36', NULL, 1);
INSERT INTO `apartment_facility` VALUES (142, 10, 46, '2023-08-14 11:36:36', NULL, 1);
INSERT INTO `apartment_facility` VALUES (143, 10, 24, '2023-08-14 11:36:59', NULL, 1);
INSERT INTO `apartment_facility` VALUES (144, 10, 25, '2023-08-14 11:36:59', NULL, 1);
INSERT INTO `apartment_facility` VALUES (145, 10, 26, '2023-08-14 11:36:59', NULL, 1);
INSERT INTO `apartment_facility` VALUES (146, 10, 40, '2023-08-14 11:36:59', NULL, 1);
INSERT INTO `apartment_facility` VALUES (147, 10, 41, '2023-08-14 11:36:59', NULL, 1);
INSERT INTO `apartment_facility` VALUES (148, 10, 43, '2023-08-14 11:36:59', NULL, 1);
INSERT INTO `apartment_facility` VALUES (149, 10, 44, '2023-08-14 11:36:59', NULL, 1);
INSERT INTO `apartment_facility` VALUES (150, 10, 42, '2023-08-14 11:36:59', NULL, 1);
INSERT INTO `apartment_facility` VALUES (151, 10, 57, '2023-08-14 11:36:59', NULL, 1);
INSERT INTO `apartment_facility` VALUES (152, 10, 46, '2023-08-14 11:36:59', NULL, 1);
INSERT INTO `apartment_facility` VALUES (153, 9, 24, '2023-08-14 11:40:47', NULL, 1);
INSERT INTO `apartment_facility` VALUES (154, 9, 25, '2023-08-14 11:40:47', NULL, 1);
INSERT INTO `apartment_facility` VALUES (155, 9, 26, '2023-08-14 11:40:47', NULL, 1);
INSERT INTO `apartment_facility` VALUES (156, 9, 40, '2023-08-14 11:40:47', NULL, 1);
INSERT INTO `apartment_facility` VALUES (157, 9, 41, '2023-08-14 11:40:47', NULL, 1);
INSERT INTO `apartment_facility` VALUES (158, 9, 42, '2023-08-14 11:40:47', NULL, 1);
INSERT INTO `apartment_facility` VALUES (159, 9, 43, '2023-08-14 11:40:47', NULL, 1);
INSERT INTO `apartment_facility` VALUES (160, 9, 44, '2023-08-14 11:40:47', NULL, 1);
INSERT INTO `apartment_facility` VALUES (161, 9, 45, '2023-08-14 11:40:47', NULL, 1);
INSERT INTO `apartment_facility` VALUES (162, 9, 46, '2023-08-14 11:40:47', NULL, 1);
INSERT INTO `apartment_facility` VALUES (163, 9, 47, '2023-08-14 11:40:47', NULL, 1);
INSERT INTO `apartment_facility` VALUES (164, 9, 57, '2023-08-14 11:40:47', NULL, 1);
INSERT INTO `apartment_facility` VALUES (165, 10, 24, '2023-08-14 11:45:22', NULL, 1);
INSERT INTO `apartment_facility` VALUES (166, 10, 25, '2023-08-14 11:45:22', NULL, 1);
INSERT INTO `apartment_facility` VALUES (167, 10, 26, '2023-08-14 11:45:22', NULL, 1);
INSERT INTO `apartment_facility` VALUES (168, 10, 40, '2023-08-14 11:45:22', NULL, 1);
INSERT INTO `apartment_facility` VALUES (169, 10, 41, '2023-08-14 11:45:22', NULL, 1);
INSERT INTO `apartment_facility` VALUES (170, 10, 43, '2023-08-14 11:45:22', NULL, 1);
INSERT INTO `apartment_facility` VALUES (171, 10, 44, '2023-08-14 11:45:22', NULL, 1);
INSERT INTO `apartment_facility` VALUES (172, 10, 42, '2023-08-14 11:45:22', NULL, 1);
INSERT INTO `apartment_facility` VALUES (173, 10, 57, '2023-08-14 11:45:22', NULL, 1);
INSERT INTO `apartment_facility` VALUES (174, 10, 46, '2023-08-14 11:45:22', NULL, 1);
INSERT INTO `apartment_facility` VALUES (175, 9, 24, '2023-08-14 11:55:11', NULL, 1);
INSERT INTO `apartment_facility` VALUES (176, 9, 25, '2023-08-14 11:55:11', NULL, 1);
INSERT INTO `apartment_facility` VALUES (177, 9, 26, '2023-08-14 11:55:11', NULL, 1);
INSERT INTO `apartment_facility` VALUES (178, 9, 40, '2023-08-14 11:55:11', NULL, 1);
INSERT INTO `apartment_facility` VALUES (179, 9, 41, '2023-08-14 11:55:11', NULL, 1);
INSERT INTO `apartment_facility` VALUES (180, 9, 42, '2023-08-14 11:55:11', NULL, 1);
INSERT INTO `apartment_facility` VALUES (181, 9, 43, '2023-08-14 11:55:11', NULL, 1);
INSERT INTO `apartment_facility` VALUES (182, 9, 44, '2023-08-14 11:55:11', NULL, 1);
INSERT INTO `apartment_facility` VALUES (183, 9, 45, '2023-08-14 11:55:11', NULL, 1);
INSERT INTO `apartment_facility` VALUES (184, 9, 46, '2023-08-14 11:55:11', NULL, 1);
INSERT INTO `apartment_facility` VALUES (185, 9, 47, '2023-08-14 11:55:11', NULL, 1);
INSERT INTO `apartment_facility` VALUES (186, 9, 57, '2023-08-14 11:55:11', NULL, 1);
INSERT INTO `apartment_facility` VALUES (187, 9, 24, '2023-08-14 12:30:55', NULL, 1);
INSERT INTO `apartment_facility` VALUES (188, 9, 25, '2023-08-14 12:30:55', NULL, 1);
INSERT INTO `apartment_facility` VALUES (189, 9, 26, '2023-08-14 12:30:55', NULL, 1);
INSERT INTO `apartment_facility` VALUES (190, 9, 40, '2023-08-14 12:30:55', NULL, 1);
INSERT INTO `apartment_facility` VALUES (191, 9, 41, '2023-08-14 12:30:55', NULL, 1);
INSERT INTO `apartment_facility` VALUES (192, 9, 42, '2023-08-14 12:30:55', NULL, 1);
INSERT INTO `apartment_facility` VALUES (193, 9, 43, '2023-08-14 12:30:55', NULL, 1);
INSERT INTO `apartment_facility` VALUES (194, 9, 44, '2023-08-14 12:30:55', NULL, 1);
INSERT INTO `apartment_facility` VALUES (195, 9, 45, '2023-08-14 12:30:55', NULL, 1);
INSERT INTO `apartment_facility` VALUES (196, 9, 46, '2023-08-14 12:30:55', NULL, 1);
INSERT INTO `apartment_facility` VALUES (197, 9, 47, '2023-08-14 12:30:55', NULL, 1);
INSERT INTO `apartment_facility` VALUES (198, 9, 57, '2023-08-14 12:30:55', NULL, 1);
INSERT INTO `apartment_facility` VALUES (199, 10, 24, '2023-08-14 12:31:07', NULL, 1);
INSERT INTO `apartment_facility` VALUES (200, 10, 25, '2023-08-14 12:31:07', NULL, 1);
INSERT INTO `apartment_facility` VALUES (201, 10, 26, '2023-08-14 12:31:07', NULL, 1);
INSERT INTO `apartment_facility` VALUES (202, 10, 40, '2023-08-14 12:31:07', NULL, 1);
INSERT INTO `apartment_facility` VALUES (203, 10, 41, '2023-08-14 12:31:07', NULL, 1);
INSERT INTO `apartment_facility` VALUES (204, 10, 43, '2023-08-14 12:31:07', NULL, 1);
INSERT INTO `apartment_facility` VALUES (205, 10, 44, '2023-08-14 12:31:07', NULL, 1);
INSERT INTO `apartment_facility` VALUES (206, 10, 42, '2023-08-14 12:31:07', NULL, 1);
INSERT INTO `apartment_facility` VALUES (207, 10, 57, '2023-08-14 12:31:07', NULL, 1);
INSERT INTO `apartment_facility` VALUES (208, 10, 46, '2023-08-14 12:31:07', NULL, 1);
INSERT INTO `apartment_facility` VALUES (209, 9, 24, '2023-08-19 15:44:26', NULL, 0);
INSERT INTO `apartment_facility` VALUES (210, 9, 25, '2023-08-19 15:44:26', NULL, 0);
INSERT INTO `apartment_facility` VALUES (211, 9, 26, '2023-08-19 15:44:26', NULL, 0);
INSERT INTO `apartment_facility` VALUES (212, 9, 40, '2023-08-19 15:44:26', NULL, 0);
INSERT INTO `apartment_facility` VALUES (213, 9, 41, '2023-08-19 15:44:26', NULL, 0);
INSERT INTO `apartment_facility` VALUES (214, 9, 42, '2023-08-19 15:44:26', NULL, 0);
INSERT INTO `apartment_facility` VALUES (215, 9, 43, '2023-08-19 15:44:26', NULL, 0);
INSERT INTO `apartment_facility` VALUES (216, 9, 44, '2023-08-19 15:44:26', NULL, 0);
INSERT INTO `apartment_facility` VALUES (217, 9, 45, '2023-08-19 15:44:26', NULL, 0);
INSERT INTO `apartment_facility` VALUES (218, 9, 46, '2023-08-19 15:44:26', NULL, 0);
INSERT INTO `apartment_facility` VALUES (219, 9, 47, '2023-08-19 15:44:26', NULL, 0);
INSERT INTO `apartment_facility` VALUES (220, 9, 57, '2023-08-19 15:44:26', NULL, 0);
INSERT INTO `apartment_facility` VALUES (221, 10, 24, '2023-08-19 15:44:50', NULL, 0);
INSERT INTO `apartment_facility` VALUES (222, 10, 25, '2023-08-19 15:44:50', NULL, 0);
INSERT INTO `apartment_facility` VALUES (223, 10, 26, '2023-08-19 15:44:50', NULL, 0);
INSERT INTO `apartment_facility` VALUES (224, 10, 40, '2023-08-19 15:44:50', NULL, 0);
INSERT INTO `apartment_facility` VALUES (225, 10, 41, '2023-08-19 15:44:50', NULL, 0);
INSERT INTO `apartment_facility` VALUES (226, 10, 43, '2023-08-19 15:44:50', NULL, 0);
INSERT INTO `apartment_facility` VALUES (227, 10, 44, '2023-08-19 15:44:50', NULL, 0);
INSERT INTO `apartment_facility` VALUES (228, 10, 42, '2023-08-19 15:44:50', NULL, 0);
INSERT INTO `apartment_facility` VALUES (229, 10, 57, '2023-08-19 15:44:50', NULL, 0);
INSERT INTO `apartment_facility` VALUES (230, 10, 46, '2023-08-19 15:44:50', NULL, 0);

-- ----------------------------
-- Table structure for apartment_fee_value
-- ----------------------------
DROP TABLE IF EXISTS `apartment_fee_value`;
CREATE TABLE `apartment_fee_value`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `apartment_id` bigint NULL DEFAULT NULL COMMENT '公寓id',
  `fee_value_id` bigint NULL DEFAULT NULL COMMENT '收费项value_id',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 99 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '公寓&杂费关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of apartment_fee_value
-- ----------------------------
INSERT INTO `apartment_fee_value` VALUES (1, 9, NULL, '2023-06-20 10:15:43', NULL, 1);
INSERT INTO `apartment_fee_value` VALUES (2, 9, NULL, '2023-06-20 10:15:43', NULL, 1);
INSERT INTO `apartment_fee_value` VALUES (3, 9, 3, '2023-06-20 10:17:44', NULL, 1);
INSERT INTO `apartment_fee_value` VALUES (4, 9, 7, '2023-06-20 10:17:44', NULL, 1);
INSERT INTO `apartment_fee_value` VALUES (5, 10, 2, '2023-06-21 10:17:59', NULL, 1);
INSERT INTO `apartment_fee_value` VALUES (6, 10, 7, '2023-06-21 10:17:59', NULL, 1);
INSERT INTO `apartment_fee_value` VALUES (7, 11, 2, '2023-06-21 10:19:46', NULL, 1);
INSERT INTO `apartment_fee_value` VALUES (8, 11, 7, '2023-06-21 10:19:46', NULL, 1);
INSERT INTO `apartment_fee_value` VALUES (9, 10, 2, '2023-06-21 10:21:36', NULL, 1);
INSERT INTO `apartment_fee_value` VALUES (10, 10, 2, '2023-06-21 10:22:10', NULL, 1);
INSERT INTO `apartment_fee_value` VALUES (11, 9, 2, '2023-07-18 16:29:06', NULL, 1);
INSERT INTO `apartment_fee_value` VALUES (12, 9, 5, '2023-07-18 16:29:06', NULL, 1);
INSERT INTO `apartment_fee_value` VALUES (13, 9, 12, '2023-07-18 16:29:06', NULL, 1);
INSERT INTO `apartment_fee_value` VALUES (14, 10, 2, '2023-07-18 16:30:05', NULL, 1);
INSERT INTO `apartment_fee_value` VALUES (15, 9, 2, '2023-07-22 10:44:44', NULL, 1);
INSERT INTO `apartment_fee_value` VALUES (16, 9, 5, '2023-07-22 10:44:44', NULL, 1);
INSERT INTO `apartment_fee_value` VALUES (17, 9, 2, '2023-07-22 13:52:20', NULL, 1);
INSERT INTO `apartment_fee_value` VALUES (18, 9, 5, '2023-07-22 13:52:20', NULL, 1);
INSERT INTO `apartment_fee_value` VALUES (19, 9, 2, '2023-08-10 18:52:30', NULL, 1);
INSERT INTO `apartment_fee_value` VALUES (20, 9, 5, '2023-08-10 18:52:30', NULL, 1);
INSERT INTO `apartment_fee_value` VALUES (21, 9, 13, '2023-08-10 18:52:30', NULL, 1);
INSERT INTO `apartment_fee_value` VALUES (22, 9, 16, '2023-08-10 18:52:30', NULL, 1);
INSERT INTO `apartment_fee_value` VALUES (23, 9, 21, '2023-08-10 18:52:30', NULL, 1);
INSERT INTO `apartment_fee_value` VALUES (24, 10, 2, '2023-08-10 18:53:11', NULL, 1);
INSERT INTO `apartment_fee_value` VALUES (25, 10, 6, '2023-08-10 18:53:11', NULL, 1);
INSERT INTO `apartment_fee_value` VALUES (26, 10, 14, '2023-08-10 18:53:11', NULL, 1);

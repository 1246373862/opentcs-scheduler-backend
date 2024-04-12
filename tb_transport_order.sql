/*
 Navicat Premium Data Transfer

 Source Server         : local
 Source Server Type    : MySQL
 Source Server Version : 80033
 Source Host           : localhost:3306
 Source Schema         : ry

 Target Server Type    : MySQL
 Target Server Version : 80033
 File Encoding         : 65001

 Date: 12/04/2024 18:17:01
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tb_transport_order
-- ----------------------------
DROP TABLE IF EXISTS `tb_transport_order`;
CREATE TABLE `tb_transport_order`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `dispensable` tinyint(1) NULL DEFAULT NULL,
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `state` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `intended_vehicle` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `processing_vehicle` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `destinations` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `creation_time` datetime NULL DEFAULT NULL,
  `deadline` datetime NULL DEFAULT NULL,
  `finished_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_transport_order
-- ----------------------------
INSERT INTO `tb_transport_order` VALUES (6, 'Order-1712901233737', 0, '-', 'DISPATCHABLE', 'Vehicle-0001', NULL, '[{\"locationName\":\"Location-0006\",\"operation\":\"Load cargo\",\"properties\":[],\"state\":\"PRISTINE\"},{\"locationName\":\"Location-0006\",\"operation\":\"NOP\",\"properties\":[],\"state\":\"PRISTINE\"}]', '2024-04-12 13:53:54', '2024-04-12 14:53:54', '2024-04-12 13:53:54');
INSERT INTO `tb_transport_order` VALUES (7, 'Order-1712901295315', 0, '-', 'DISPATCHABLE', NULL, NULL, '[{\"locationName\":\"Location-0001\",\"operation\":\"NOP\",\"properties\":[],\"state\":\"PRISTINE\"}]', '2024-04-12 13:54:55', '2024-04-12 14:54:55', '2024-04-12 13:54:55');
INSERT INTO `tb_transport_order` VALUES (8, 'TOrder-01HV8E1TV3RM0CZ3VM7HFYTJQX', 0, '-', 'DISPATCHABLE', NULL, NULL, '[{\"locationName\":\"Location-0001\",\"operation\":\"Unload cargo\",\"properties\":[],\"state\":\"PRISTINE\"}]', '2024-04-12 13:56:25', '2024-04-12 14:56:00', '2024-04-12 13:56:25');
INSERT INTO `tb_transport_order` VALUES (9, 'TOrder-01HV8E24VZQXR65WT7TKZRZDET', 0, '-', 'DISPATCHABLE', 'Vehicle-0001', NULL, '[{\"locationName\":\"Location-0004\",\"operation\":\"Load cargo\",\"properties\":[],\"state\":\"PRISTINE\"}]', '2024-04-12 13:56:35', '2024-04-12 14:56:00', '2024-04-12 13:56:35');
INSERT INTO `tb_transport_order` VALUES (10, 'Order-1712901662864', 0, '-', 'FINISHED', NULL, 'Vehicle-0002', '[{\"locationName\":\"Location-0001\",\"operation\":\"NOP\",\"properties\":[],\"state\":\"FINISHED\"}]', '2024-04-12 14:01:03', '2024-04-12 15:01:03', '2024-04-12 14:01:03');
INSERT INTO `tb_transport_order` VALUES (11, 'Order-1712901742454', 0, '-', 'FINISHED', '', 'Vehicle-0002', '[{\"locationName\":\"Location-0004\",\"operation\":\"NOP\",\"properties\":[],\"state\":\"FINISHED\"}]', '2024-04-12 14:02:22', '2024-04-12 15:02:22', '2024-04-12 14:02:22');
INSERT INTO `tb_transport_order` VALUES (12, 'Order-1712905748938', 0, '-', 'FINISHED', 'Vehicle-0002', 'Vehicle-0002', '[{\"locationName\":\"Location-0005\",\"operation\":\"NOP\",\"properties\":[],\"state\":\"FINISHED\"}]', '2024-04-12 15:09:09', '2024-04-12 16:09:09', '2024-04-12 15:09:09');
INSERT INTO `tb_transport_order` VALUES (13, 'Order-1712905895248', 0, '-', 'FINISHED', 'Vehicle-0002', 'Vehicle-0002', '[{\"locationName\":\"Location-0003\",\"operation\":\"Load cargo\",\"properties\":[],\"state\":\"FINISHED\"}]', '2024-04-12 15:11:35', '2024-04-12 16:11:35', '2024-04-12 15:11:35');
INSERT INTO `tb_transport_order` VALUES (14, 'Order-1712908255171', 1, '-', 'FINISHED', NULL, 'Vehicle-0001', '[{\"locationName\":\"Location-0004\",\"operation\":\"Load cargo\",\"properties\":[],\"state\":\"FINISHED\"}]', '2024-04-12 15:50:55', '2024-04-12 16:50:55', '2024-04-12 15:50:55');

SET FOREIGN_KEY_CHECKS = 1;

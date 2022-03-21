/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50733
 Source Host           : localhost:3306
 Source Schema         : esop

 Target Server Type    : MySQL
 Target Server Version : 50733
 File Encoding         : 65001

 Date: 21/03/2022 09:54:03
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tbl_children_folder
-- ----------------------------
DROP TABLE IF EXISTS `tbl_children_folder`;
CREATE TABLE `tbl_children_folder`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `fid` int(11) NULL DEFAULT NULL COMMENT '父文件夹的id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 96 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tbl_children_folder
-- ----------------------------
INSERT INTO `tbl_children_folder` VALUES (81, '01', 52);
INSERT INTO `tbl_children_folder` VALUES (85, '08', 52);
INSERT INTO `tbl_children_folder` VALUES (86, '09', 52);
INSERT INTO `tbl_children_folder` VALUES (87, '44', 52);
INSERT INTO `tbl_children_folder` VALUES (88, '78', 52);
INSERT INTO `tbl_children_folder` VALUES (89, '54', 52);
INSERT INTO `tbl_children_folder` VALUES (90, '02', 52);
INSERT INTO `tbl_children_folder` VALUES (92, '03', 52);
INSERT INTO `tbl_children_folder` VALUES (94, '04', 52);
INSERT INTO `tbl_children_folder` VALUES (95, '11', 52);

-- ----------------------------
-- Table structure for tbl_folder
-- ----------------------------
DROP TABLE IF EXISTS `tbl_folder`;
CREATE TABLE `tbl_folder`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `origin_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '上传文件的原名称',
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '上传完成后的名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 53 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tbl_folder
-- ----------------------------
INSERT INTO `tbl_folder` VALUES (52, '测试', '7829705155b74dffa138fa8ef20c0f49');

-- ----------------------------
-- Table structure for tbl_img
-- ----------------------------
DROP TABLE IF EXISTS `tbl_img`;
CREATE TABLE `tbl_img`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `origin_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `img_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `fid` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 83 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tbl_img
-- ----------------------------
INSERT INTO `tbl_img` VALUES (68, '123.png', '1b6fcee29d4048eb92b871aaa74bba1c.png', '/image/7829705155b74dffa138fa8ef20c0f49/01/1b6fcee29d4048eb92b871aaa74bba1c.png', 81);
INSERT INTO `tbl_img` VALUES (72, '621b77142f7411e39a8f22000a9f195b_6', '326ed533d7904c2098e5b59c6952c2a0.jpg', '/image/7829705155b74dffa138fa8ef20c0f49/08/326ed533d7904c2098e5b59c6952c2a0.jpg', 85);
INSERT INTO `tbl_img` VALUES (73, '666afbec30d111e396cd22000a1fd1c2_6', 'b6d377d4f54b4595a28a24b477f609a4.jpg', '/image/7829705155b74dffa138fa8ef20c0f49/09/b6d377d4f54b4595a28a24b477f609a4.jpg', 86);
INSERT INTO `tbl_img` VALUES (74, 'f3f72dc42f3111e3ba2c1231391eb9f5_8', '5d6f0349b860436ab7ae87453466ff57.jpg', '/image/7829705155b74dffa138fa8ef20c0f49/44/5d6f0349b860436ab7ae87453466ff57.jpg', 87);
INSERT INTO `tbl_img` VALUES (75, '961ff1b04a0a11e3b98b126fa4e6a4fb_6', '47aa891980ab4ddd9fd5aa7d3b3b25f2.jpg', '/image/7829705155b74dffa138fa8ef20c0f49/78/47aa891980ab4ddd9fd5aa7d3b3b25f2.jpg', 88);
INSERT INTO `tbl_img` VALUES (76, '火柴截图20220309141830953', '249440ec0db34dd0b1e8ef2d551a8d93.png', '/image/7829705155b74dffa138fa8ef20c0f49/54/249440ec0db34dd0b1e8ef2d551a8d93.png', 89);
INSERT INTO `tbl_img` VALUES (77, '0dab58047ed211e39686129d69e7bd65_6', '43401c4a189c48a18faac5761baa293c.jpg', '/image/7829705155b74dffa138fa8ef20c0f49/02/43401c4a189c48a18faac5761baa293c.jpg', 90);
INSERT INTO `tbl_img` VALUES (79, '火柴截图20220222100144795', 'f30485fd52ad4ddc936fb246a0bb457e.jpg', '/image/7829705155b74dffa138fa8ef20c0f49/03/f30485fd52ad4ddc936fb246a0bb457e.jpg', 92);
INSERT INTO `tbl_img` VALUES (81, 'minimalistic-landscape-mountains-forest-bird-sky-artwork-others-13757', '00d5bca7808d49c393ce9a56f05886d7.jpg', '/image/7829705155b74dffa138fa8ef20c0f49/04/00d5bca7808d49c393ce9a56f05886d7.jpg', 94);
INSERT INTO `tbl_img` VALUES (82, 'getsuga-tenshou-kurosaki-ichigo-mugetsu-bleach-anime-11113', 'f8408c570b564e1992c3e94ddd16ea55.jpg', '/image/7829705155b74dffa138fa8ef20c0f49/11/f8408c570b564e1992c3e94ddd16ea55.jpg', 95);

-- ----------------------------
-- Table structure for tbl_user
-- ----------------------------
DROP TABLE IF EXISTS `tbl_user`;
CREATE TABLE `tbl_user`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `nickname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tbl_user
-- ----------------------------
INSERT INTO `tbl_user` VALUES (1, 'admin', 'admin', '管理员');

SET FOREIGN_KEY_CHECKS = 1;

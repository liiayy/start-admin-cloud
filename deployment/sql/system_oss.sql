-- ----------------------------
-- 1. 文件元数据表
-- ----------------------------
DROP TABLE IF EXISTS "public"."system_oss";
CREATE TABLE "public"."system_oss" (
  "id" int8 NOT NULL,
  "file_name" varchar(255) NOT NULL,
  "original_name" varchar(255) NOT NULL,
  "file_suffix" varchar(10) NOT NULL,
  "url" varchar(500) NOT NULL,
  "size" int8 NOT NULL,
  "service" varchar(20) NOT NULL,
  "content_type" varchar(100),
  "md5" varchar(64),
  "creator" varchar(64),
  "create_time" timestamp,
  "updater" varchar(64),
  "update_time" timestamp,
  "deleted" bool DEFAULT false,
  PRIMARY KEY ("id")
);

COMMENT ON TABLE "public"."system_oss" IS '文件元数据记录表';
COMMENT ON COLUMN "public"."system_oss"."id" IS '自增主键';
COMMENT ON COLUMN "public"."system_oss"."file_name" IS '对象存储中的文件名 (Object Key)';
COMMENT ON COLUMN "public"."system_oss"."original_name" IS '上传时的原始文件名';
COMMENT ON COLUMN "public"."system_oss"."file_suffix" IS '文件后缀名 (无点, 如 jpg)';
COMMENT ON COLUMN "public"."system_oss"."url" IS '资源访问 URL';
COMMENT ON COLUMN "public"."system_oss"."size" IS '文件大小 (Byte)';
COMMENT ON COLUMN "public"."system_oss"."service" IS '存储配置键 (oss_config_key)';
COMMENT ON COLUMN "public"."system_oss"."content_type" IS '内容类型 (MIME)';
COMMENT ON COLUMN "public"."system_oss"."md5" IS '文件 MD5 校验值';
COMMENT ON COLUMN "public"."system_oss"."creator" IS '创建者';
COMMENT ON COLUMN "public"."system_oss"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."system_oss"."updater" IS '修改者';
COMMENT ON COLUMN "public"."system_oss"."update_time" IS '修改时间';
COMMENT ON COLUMN "public"."system_oss"."deleted" IS '是否删除';

-- ----------------------------
-- 2. 对象存储配置表
-- ----------------------------
DROP TABLE IF EXISTS "public"."system_oss_config";
CREATE TABLE "public"."system_oss_config" (
  "id" int8 NOT NULL,
  "config_key" varchar(20) NOT NULL,
  "service" varchar(20) DEFAULT 'local',
  "access_key" varchar(255),
  "secret_key" varchar(255),
  "bucket_name" varchar(255),
  "prefix" varchar(255),
  "endpoint" varchar(255),
  "domain" varchar(255),
  "region" varchar(50),
  "is_https" bool DEFAULT false,
  "access_policy" int2 DEFAULT 0,
  "status" int2 DEFAULT 0,
  "remark" varchar(500),
  "creator" varchar(64),
  "create_time" timestamp,
  "updater" varchar(64),
  "update_time" timestamp,
  "deleted" bool DEFAULT false,
  PRIMARY KEY ("id"),
  CONSTRAINT "uni_oss_config_key" UNIQUE ("config_key")
);

COMMENT ON TABLE "public"."system_oss_config" IS '对象存储配置表';
COMMENT ON COLUMN "public"."system_oss_config"."id" IS '自增主键';
COMMENT ON COLUMN "public"."system_oss_config"."config_key" IS '配置标识 (唯一且不能重复)';
COMMENT ON COLUMN "public"."system_oss_config"."access_key" IS 'Access Key (AK)';
COMMENT ON COLUMN "public"."system_oss_config"."secret_key" IS 'Secret Key (SK)';
COMMENT ON COLUMN "public"."system_oss_config"."bucket_name" IS '存储桶名称';
COMMENT ON COLUMN "public"."system_oss_config"."prefix" IS '存储前缀';
COMMENT ON COLUMN "public"."system_oss_config"."endpoint" IS '服务端点 (OSS/MinIO 为必填)';
COMMENT ON COLUMN "public"."system_oss_config"."domain" IS '展示域名/自定义域名';
COMMENT ON COLUMN "public"."system_oss_config"."region" IS '区域标识';
COMMENT ON COLUMN "public"."system_oss_config"."is_https" IS '是否使用 HTTPS';
COMMENT ON COLUMN "public"."system_oss_config"."access_policy" IS '桶权限类型 (0私有 1公开读 2公开读写)';
COMMENT ON COLUMN "public"."system_oss_config"."status" IS '配置状态 (0正常 1停用)';
COMMENT ON COLUMN "public"."system_oss_config"."remark" IS '备注';
COMMENT ON COLUMN "public"."system_oss_config"."creator" IS '创建者';
COMMENT ON COLUMN "public"."system_oss_config"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."system_oss_config"."updater" IS '修改者';
COMMENT ON COLUMN "public"."system_oss_config"."update_time" IS '修改时间';
COMMENT ON COLUMN "public"."system_oss_config"."deleted" IS '是否删除';

-- ----------------------------
-- 插入初始化默认配置 (Local 存储)
-- ----------------------------
INSERT INTO "public"."system_oss_config" ("id", "config_key", "bucket_name", "prefix", "endpoint", "status", "remark", "create_time") 
VALUES (1, 'local', 'default', 'upload', 'f:/start-admin/upload', 0, '默认本地存储', now());

-- ----------------------------
-- 3. 菜单/按钮权限初始化
-- ----------------------------
-- 先清除旧数据 (如果存在)
DELETE FROM "public"."system_menu" WHERE id >= 1500 AND id <= 1510;

-- 一级菜单: 文件管理 (Page)
INSERT INTO "public"."system_menu" ("id", "name", "permission", "type", "sort", "parent_id", "path", "component")
VALUES (1500, '文件管理', '', 2, 50, 100, 'oss', 'system/oss/index');

-- 按钮: 资源查询
INSERT INTO "public"."system_menu" ("id", "name", "permission", "type", "sort", "parent_id")
VALUES (1501, '资源查询', 'system:oss:query', 3, 10, 1500);

-- 按钮: 文件上传
INSERT INTO "public"."system_menu" ("id", "name", "permission", "type", "sort", "parent_id")
VALUES (1502, '文件上传', 'system:oss:upload', 3, 20, 1500);

-- 按钮: 文件删除
INSERT INTO "public"."system_menu" ("id", "name", "permission", "type", "sort", "parent_id")
VALUES (1503, '文件删除', 'system:oss:delete', 3, 30, 1500);

-- 按钮: 存储配置 (打开弹窗)
INSERT INTO "public"."system_menu" ("id", "name", "permission", "type", "sort", "parent_id")
VALUES (1504, '存储配置', 'system:oss:config', 3, 40, 1500);

-- 配置子权限 (按钮级别)
INSERT INTO "public"."system_menu" ("id", "name", "permission", "type", "sort", "parent_id")
VALUES (1505, '配置新增', 'system:oss:config:add', 3, 50, 1500);

INSERT INTO "public"."system_menu" ("id", "name", "permission", "type", "sort", "parent_id")
VALUES (1506, '配置删除', 'system:oss:config:delete', 3, 60, 1500);

INSERT INTO "public"."system_menu" ("id", "name", "permission", "type", "sort", "parent_id")
VALUES (1507, '配置重载', 'system:oss:config:update', 3, 70, 1500);

-- ----------------------------
-- 3. 字典初始化数据
-- ----------------------------

-- 字典类型
INSERT INTO "public"."system_dict_type" ("name", "type", "status", "remark", "creator", "create_time") 
VALUES 
('OSS存储平台', 'sys_oss_service', 0, '对象存储服务商类型', 'admin', now()),
('OSS桶权限类型', 'sys_oss_access_policy', 0, '对象存储权限策略', 'admin', now());

-- 存储平台字典数据
INSERT INTO "public"."system_dict_data" ("sort", "label", "value", "dict_type", "status", "color_type", "remark", "creator", "create_time")
VALUES 
(1, '本地存储', 'local', 'sys_oss_service', 0, 'primary', '本地存储策略', 'admin', now()),
(2, '阿里云OSS', 'aliyun', 'sys_oss_service', 0, 'success', '阿里云对象存储', 'admin', now()),
(3, '腾讯云COS', 'tencent', 'sys_oss_service', 0, 'info', '腾讯云对象存储', 'admin', now()),
(4, '七牛云', 'qiniu', 'sys_oss_service', 0, 'warning', '七牛云存储', 'admin', now()),
(5, '华为云OBS', 'huawei', 'sys_oss_service', 0, 'danger', '华为云对象存储', 'admin', now()),
(6, 'MinIO', 'minio', 'sys_oss_service', 0, 'info', 'MinIO私有化部署', 'admin', now());

-- 桶权限字典数据
INSERT INTO "public"."system_dict_data" ("sort", "label", "value", "dict_type", "status", "color_type", "remark", "creator", "create_time")
VALUES 
(1, '私有', '0', 'sys_oss_access_policy', 0, 'info', '私有读写', 'admin', now()),
(2, '公开读', '1', 'sys_oss_access_policy', 0, 'success', '公开读，私有写', 'admin', now()),
(3, '公开读写', '2', 'sys_oss_access_policy', 0, 'danger', '公开读写', 'admin', now());

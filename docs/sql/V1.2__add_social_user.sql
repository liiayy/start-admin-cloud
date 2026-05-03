-- ----------------------------
-- 社交用户绑定表 (PostgreSQL)
-- ----------------------------
CREATE TABLE IF NOT EXISTS system_social_user (
    id          BIGINT PRIMARY KEY,
    user_id     BIGINT,
    source      VARCHAR(32)  NOT NULL,
    uuid        VARCHAR(128) NOT NULL,
    username    VARCHAR(128),
    nickname    VARCHAR(128),
    avatar      VARCHAR(512),
    access_token  TEXT,
    refresh_token TEXT,
    raw_user_info TEXT,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted     BOOLEAN NOT NULL DEFAULT FALSE
);

COMMENT ON TABLE system_social_user IS '社交用户绑定表';
COMMENT ON COLUMN system_social_user.id IS '主键';
COMMENT ON COLUMN system_social_user.user_id IS '系统用户ID';
COMMENT ON COLUMN system_social_user.source IS '社交平台类型 (github, gitee等)';
COMMENT ON COLUMN system_social_user.uuid IS '第三方平台唯一标识';
COMMENT ON COLUMN system_social_user.username IS '第三方平台用户名';
COMMENT ON COLUMN system_social_user.nickname IS '第三方平台昵称';
COMMENT ON COLUMN system_social_user.avatar IS '第三方平台头像';
COMMENT ON COLUMN system_social_user.access_token IS '访问令牌';
COMMENT ON COLUMN system_social_user.refresh_token IS '刷新令牌';
COMMENT ON COLUMN system_social_user.raw_user_info IS '第三方原始用户信息';
COMMENT ON COLUMN system_social_user.create_time IS '创建时间';
COMMENT ON COLUMN system_social_user.update_time IS '更新时间';
COMMENT ON COLUMN system_social_user.deleted IS '是否删除';

-- 创建索引
CREATE UNIQUE INDEX IF NOT EXISTS uk_social_source_uuid ON system_social_user (source, uuid);
CREATE INDEX IF NOT EXISTS idx_social_user_id ON system_social_user (user_id);

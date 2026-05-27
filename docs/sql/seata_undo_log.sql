-- ----------------------------------------------------------------------------------------------------------------
-- Seata Undo Log Table
-- ----------------------------------------------------------------------------------------------------------------
-- 注意：此脚本适用于 PostgreSQL 数据库。
-- 如果您使用的是 MySQL 或 Oracle，请参考 Seata 官方文档获取对应的建表语句。
-- ----------------------------------------------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS public.undo_log
(
    id            SERIAL       NOT NULL,
    branch_id     BIGINT       NOT NULL,
    xid           VARCHAR(128) NOT NULL,
    context       VARCHAR(128) NOT NULL,
    rollback_info BYTEA        NOT NULL,
    log_status    INT          NOT NULL,
    log_created   timestamp(0) NOT NULL,
    log_modified  timestamp(0) NOT NULL,
    CONSTRAINT pk_undo_log PRIMARY KEY (id),
    CONSTRAINT ux_undo_log UNIQUE (xid, branch_id)
);

-- 添加注释
COMMENT ON TABLE public.undo_log IS 'Seata AT 模式回滚日志表';
COMMENT ON COLUMN public.undo_log.branch_id IS '分支事务 ID';
COMMENT ON COLUMN public.undo_log.xid IS '全局事务 ID';
COMMENT ON COLUMN public.undo_log.context IS '上下文信息';
COMMENT ON COLUMN public.undo_log.rollback_info IS '回滚数据';
COMMENT ON COLUMN public.undo_log.log_status IS '状态';
COMMENT ON COLUMN public.undo_log.log_created IS '创建时间';
COMMENT ON COLUMN public.undo_log.log_modified IS '修改时间';

-- 索引
CREATE INDEX IF NOT EXISTS ix_log_created ON undo_log(log_created);

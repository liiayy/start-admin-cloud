-- ----------------------------
-- 菜单 SQL - 系统监控
-- ----------------------------

-- 1. 一级目录：系统监控 (ID: 500)
INSERT INTO system_menu (id, name, parent_id, sort, path, component, type, icon, permission, status, visible, keep_alive, creator)
VALUES (500, '系统监控', 0, 5, '/monitor', NULL, 1, 'i-ep:monitor', '', 0, true, true, 'admin');

-- 2. 二级菜单：操作日志 (ID: 501)
INSERT INTO system_menu (id, name, parent_id, sort, path, component, type, icon, permission, status, visible, keep_alive, creator)
VALUES (501, '操作日志', 500, 1, 'operlog', 'system/monitor/operlog/index', 2, 'i-ep:document', 'monitor:operlog:query', 0, true, true, 'admin');

-- 3. 操作日志按钮权限
INSERT INTO system_menu (id, name, parent_id, sort, path, component, type, icon, permission, status, visible, keep_alive, creator)
VALUES (502, '操作查询', 501, 1, '', NULL, 3, '#', 'monitor:operlog:query', 0, true, true, 'admin');
INSERT INTO system_menu (id, name, parent_id, sort, path, component, type, icon, permission, status, visible, keep_alive, creator)
VALUES (503, '操作删除', 501, 2, '', NULL, 3, '#', 'monitor:operlog:remove', 0, true, true, 'admin');
INSERT INTO system_menu (id, name, parent_id, sort, path, component, type, icon, permission, status, visible, keep_alive, creator)
VALUES (504, '日志清空', 501, 3, '', NULL, 3, '#', 'monitor:operlog:remove', 0, true, true, 'admin');

-- 4. 二级菜单：登录日志 (ID: 505)
INSERT INTO system_menu (id, name, parent_id, sort, path, component, type, icon, permission, status, visible, keep_alive, creator)
VALUES (505, '登录日志', 500, 2, 'loginlog', 'system/monitor/loginlog/index', 2, 'i-ep:info-filled', 'monitor:loginlog:query', 0, true, true, 'admin');

-- 5. 登录日志按钮权限
INSERT INTO system_menu (id, name, parent_id, sort, path, component, type, icon, permission, status, visible, keep_alive, creator)
VALUES (506, '登录查询', 505, 1, '', NULL, 3, '#', 'monitor:loginlog:query', 0, true, true, 'admin');
INSERT INTO system_menu (id, name, parent_id, sort, path, component, type, icon, permission, status, visible, keep_alive, creator)
VALUES (507, '登录删除', 505, 2, '', NULL, 3, '#', 'monitor:loginlog:remove', 0, true, true, 'admin');
INSERT INTO system_menu (id, name, parent_id, sort, path, component, type, icon, permission, status, visible, keep_alive, creator)
VALUES (508, '日志清空', 505, 3, '', NULL, 3, '#', 'monitor:loginlog:remove', 0, true, true, 'admin');

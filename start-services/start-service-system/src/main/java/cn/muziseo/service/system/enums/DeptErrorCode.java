package cn.muziseo.service.system.enums;

import cn.muziseo.common.core.exception.errorCode.IErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 部门模块错误码（6xxxx）
 *
 * @author 木子软件
 */
@Getter
@AllArgsConstructor
public enum DeptErrorCode implements IErrorCode {

    DEPT_NOT_EXISTS(10104001, "部门不存在"),
    DEPT_NAME_EXISTS(10104002, "部门名称已存在"),
    DEPT_HAS_CHILDREN(10104003, "存在子部门，无法删除"),
    DEPT_HAS_USERS(10104004, "部门下存在用户，无法删除"),
    DEPT_HAS_POSTS(10104005, "部门下存在岗位，无法删除"),
    ;

    private final int code;
    private final String message;
}

package cn.muziseo.common.core.exception.errorCode;

/**
 * 错误码接口
 * <p>
 * 所有错误码枚举实现此接口，统一错误码的获取方式
 * <p>
 * 错误码规则：5 位数字
 * <ul>
 *   <li>1xxxx — 系统级错误</li>
 *   <li>2xxxx — 通用业务错误</li>
 *   <li>3xxxx — 用户模块</li>
 *   <li>4xxxx — 角色模块（预留）</li>
 *   <li>5xxxx — 菜单模块（预留）</li>
 *   <li>6xxxx — 部门模块（预留）</li>
 *   <li>7xxxx — 字典模块（预留）</li>
 *   <li>9xxxx — 扩展模块</li>
 * </ul>
 *
 * @author 木子软件
 */
public interface IErrorCode {

    /**
     * 获取错误码
     */
    int getCode();

    /**
     * 获取错误消息
     */
    String getMessage();
}
